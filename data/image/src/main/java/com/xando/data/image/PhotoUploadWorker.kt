package com.xando.data.image

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.xando.core.api_models.ApiException
import com.xando.core.api_models.NetworkException
import com.xando.core.api_models.ServerException
import com.xando.core.api_models.ServerUnavailableException
import com.xando.core.api_models.TimeoutException

/**
 * Отправляет на сервер фотографию.
 *
 * Работа переживает уход с экрана и смерть процесса, а при временных ошибках повторяется с
 * нарастающей задержкой. Это важно там, где фотографию выбирают сильно раньше, чем отправляют:
 * например, при регистрации приложение уходит из auth-флоу вместе со своими ViewModel ещё до того,
 * как отправка станет возможной.
 *
 * Наследник знает адресата: куда отправлять и где лежит локальная копия. Всё остальное - чтение
 * входных данных и политика повторов - одинаково для любой фотографии и живёт здесь.
 */
abstract class PhotoUploadWorker(
    context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters) {

    internal companion object {

        /** Ключ [Uri] локальной копии фотографии во входных данных работы. */
        const val KEY_PHOTO_URI = "photo_uri"

        /** Максимальное число попыток отправки, включая первую. */
        const val MAX_ATTEMPTS = 2
    }

    /**
     * Отправляет фотографию. Реализация знает адрес и формат ответа.
     *
     * Дополнительные данные адресата, если они есть, читаются из `inputData`.
     *
     * @throws ApiException Если запрос не удался.
     */
    protected abstract suspend fun upload(photoUri: Uri)

    /**
     * Вызывается после успешной отправки: наследник убирает локальную копию, потому что каталог
     * знает только он.
     *
     * На результат работы не влияет - отправка уже состоялась.
     */
    protected abstract suspend fun onUploaded(photoUri: Uri)

    final override suspend fun doWork(): Result {
        val photoUri = inputData.getString(KEY_PHOTO_URI)?.toUri() ?: return Result.failure()

        return try {
            upload(photoUri)
            onUploaded(photoUri)
            Result.success()
        } catch (ex: ApiException) {
            // Локальная копия остаётся: пока её не вытеснил новый выбор, отправку можно повторить.
            if (ex.isTransient() && runAttemptCount < MAX_ATTEMPTS - 1) Result.retry() else Result.failure()
        } catch (_: IllegalStateException) {
            // Локальной копии нет или её не прочитать - повтор ничего не изменит.
            Result.failure()
        }
    }

    /**
     * Проверяет, есть ли смысл повторять запрос: проблема на стороне сети или сервера, а не в
     * самих данных.
     */
    private fun ApiException.isTransient(): Boolean = when (this) {
        is NetworkException, is ServerUnavailableException, is TimeoutException, is ServerException -> true
        else -> false
    }
}

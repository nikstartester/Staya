package com.xando.data.user

import android.content.Context
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.xando.core.api_models.ApiException
import com.xando.core.api_models.NetworkException
import com.xando.core.api_models.ServerException
import com.xando.core.api_models.ServerUnavailableException
import com.xando.core.api_models.TimeoutException
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Отправляет на сервер фотографию профиля, сохранённую [AvatarLocalStorage].
 *
 * Загрузка возможна только после авторизации, а сразу после подтверждения e-mail приложение уходит
 * из auth-флоу вместе со своими ViewModel. Работа переживает и это, и смерть процесса, а при
 * временных ошибках повторяется с нарастающей задержкой.
 */
@HiltWorker
internal class AvatarUploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: UserRepository,
    private val localStorage: AvatarLocalStorage,
) : CoroutineWorker(context, workerParameters) {

    internal companion object {

        /** Ключ [Uri][android.net.Uri] локальной копии фотографии во входных данных работы. */
        const val KEY_PHOTO_URI = "photo_uri"

        /** Максимальное число попыток отправки, включая первую. */
        const val MAX_ATTEMPTS = 2
    }

    override suspend fun doWork(): Result {
        val photoUri = inputData.getString(KEY_PHOTO_URI)?.toUri() ?: return Result.failure()

        return try {
            repository.uploadAvatar(photoUri)
            localStorage.clear()
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

package com.xando.data.user

import android.net.Uri
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit

/**
 * Загружает фотографию профиля в фоне, переживая уход с экрана и смерть процесса.
 *
 * Сама отправка выполняется в [AvatarUploadWorker]: загрузка возможна только после авторизации, а
 * сразу после подтверждения e-mail приложение уходит из auth-флоу вместе со своими ViewModel.
 *
 * Загрузчик создаётся под конкретный [originTag] и показывает в [state] только помеченные им
 * загрузки. Это фильтр представления, а не отдельная очередь: работа у всех загрузчиков одна на
 * приложение, поэтому загрузка, начатая с одной меткой, вытеснит незавершённую загрузку с любой
 * другой.
 *
 * @param originTag Метка, которой помечаются начатые этим загрузчиком загрузки. Задаёт её тот, кто
 * загрузки начинает, чтобы о результате отчитывался ровно он.
 */
class AvatarUploader @AssistedInject constructor(
    @Assisted private val originTag: String,
    private val workManager: WorkManager,
    private val localStorage: AvatarLocalStorage
) {

    /**@SelfDocumented*/
    @AssistedFactory
    interface Factory {

        /**@SelfDocumented*/
        fun create(originTag: String): AvatarUploader
    }

    private companion object {

        /** Имя работы. Новая фотография отменяет и заменяет отправку предыдущей. */
        const val UNIQUE_WORK_NAME = "avatar_upload"
    }

    /**
     * Состояние текущей или последней завершившейся загрузки, начатой с [originTag]. Чужие загрузки
     * неотличимы от их отсутствия.
     *
     * Завершившаяся загрузка выдаётся один раз: сразу после выдачи запись о ней удаляется, поэтому
     * повторно, в том числе после перезапуска приложения, результат не придёт. Если показать его
     * было некому - результат теряется, отдельного подтверждения от подписчика не требуется.
     */
    val state: Flow<AvatarUploadState> = workManager
        .getWorkInfosForUniqueWorkFlow(UNIQUE_WORK_NAME)
        .map { workInfos -> workInfos.filter { originTag in it.tags }.actual().toUploadState() }
        .distinctUntilChanged()
        .onEach { state -> if (state is AvatarUploadState.Success || state is AvatarUploadState.Failed) consume() }

    /**
     * Кэширует фотографию по [sourceUri] в кеш приложения.
     *
     * Отправка ранее выбранной фотографии отменяется: сохранение вытесняет её локальную копию, без
     * которой отправку всё равно не завершить.
     *
     * @param sourceUri Uri, выданный пикером.
     * @return Uri локальной копии.
     * @throws IllegalStateException Если файл по [sourceUri] не удалось прочитать.
     */
    suspend fun cache(sourceUri: Uri): Uri {
        workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
        return localStorage.save(sourceUri)
    }

    /**
     * Ставит загрузку фотографии в очередь. Ошибка загрузки не прерывает пользовательский сценарий.
     */
    fun enqueue(photoUri: Uri) {
        val request = OneTimeWorkRequestBuilder<AvatarUploadWorker>()
            .setInputData(workDataOf(AvatarUploadWorker.KEY_PHOTO_URI to photoUri.toString()))
            .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag(originTag)
            .build()

        workManager.enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }

    /**
     * Убирает запись о завершившейся загрузке, чтобы её результат не выдавался повторно.
     */
    //FIXME: метод отчищает вообще все завершенные результаты, не только этого WorkManager. Нужно иметь это ввиду.
    // Фикс выглядит очень костыльно, поэтому до востребованности пока так.
    private fun consume() {
        workManager.pruneWork()
    }

    /**
     * Выбирает работу, состояние которой актуально: незавершённую, а если таких нет - последнюю
     * завершившуюся.
     */
    private fun List<WorkInfo>.actual(): WorkInfo? = firstOrNull { !it.state.isFinished } ?: lastOrNull()

    /**
     * Переводит состояние работы в состояние загрузки.
     *
     * Отменённая работа - это работа, вытесненная загрузкой новой фотографии, показывать по ней
     * нечего.
     */
    private fun WorkInfo?.toUploadState(): AvatarUploadState = when (this?.state) {
        WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING, WorkInfo.State.BLOCKED -> AvatarUploadState.InProgress
        WorkInfo.State.SUCCEEDED -> AvatarUploadState.Success
        WorkInfo.State.FAILED -> AvatarUploadState.Failed
        WorkInfo.State.CANCELLED, null -> AvatarUploadState.Idle
    }
}

package com.xando.data.image

import android.net.Uri
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
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
import java.util.concurrent.TimeUnit

/**
 * Отправляет фотографию в фоне, переживая уход с экрана и смерть процесса.
 *
 * Сама отправка выполняется воркером из [target]: он знает адрес, а загрузчик - только очередь.
 *
 * Загрузчик создаётся под конкретный [originTag] и показывает в [state] только помеченные им
 * отправки. Это фильтр представления, а не отдельная очередь: очередь задаётся адресатом, поэтому
 * две фичи, отправляющие одну и ту же фотографию, вытесняют отправки друг друга - и это верно, ведь
 * ресурс на сервере у них общий.
 *
 * @param target Адресат: воркер, имя работы и его дополнительные данные.
 * @param originTag Метка, которой помечаются начатые этим загрузчиком отправки. Задаёт её тот, кто
 * отправки начинает, чтобы о результате отчитывался ровно он.
 */
class PhotoUploader @AssistedInject constructor(
    @Assisted private val target: PhotoUploadTarget,
    @Assisted private val originTag: String,
    private val workManager: WorkManager,
) {

    /**@SelfDocumented*/
    @AssistedFactory
    interface Factory {

        /**@SelfDocumented*/
        fun create(target: PhotoUploadTarget, originTag: String): PhotoUploader
    }

    private val workInfos: Flow<List<WorkInfo>> = workManager.getWorkInfosForUniqueWorkFlow(target.workName)

    /**
     * Состояние текущей или последней завершившейся отправки, начатой с [originTag]. Чужие отправки
     * неотличимы от их отсутствия.
     *
     * Для того, кто отправку начал: по нему показывают её результат.
     */
    val originState: Flow<PhotoUploadState> = workInfos
        .map { infos -> infos.filter { originTag in it.tags }.actual().toUploadState() }
        .distinctUntilChanged()

    /**
     * Состояние отправки в этот ресурс, чья бы метка на ней ни стояла.
     */
    val workState: Flow<PhotoUploadState> = workInfos
        .map { infos -> infos.actual().toUploadState() }
        .distinctUntilChanged()

    /**
     * Ставит отправку фотографии в очередь, вытесняя незавершённую отправку в тот же ресурс.
     *
     * Ошибка отправки не прерывает пользовательский сценарий.
     *
     * @param photoUri Uri локальной копии, полученной из [PhotoLocalStorage].
     */
    fun enqueue(photoUri: Uri) {
        val request = OneTimeWorkRequest.Builder(target.workerClass)
            .setInputData(photoUri.toInputData())
            .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag(originTag)
            .build()

        workManager.enqueueUniqueWork(target.workName, ExistingWorkPolicy.REPLACE, request)
    }

    /**
     * Отменяет незавершённую отправку в этот ресурс, чью бы метку она ни несла.
     */
    fun cancel() {
        workManager.cancelUniqueWork(target.workName)
    }

    /**
     * Собирает входные данные работы: uri фотографии и то, что адресат добавил от себя.
     */
    private fun Uri.toInputData(): Data = Data.Builder()
        .putAll(target.extras)
        .putAll(workDataOf(PhotoUploadWorker.KEY_PHOTO_URI to toString()))
        .build()

    /**
     * Выбирает работу, состояние которой актуально: незавершённую, а если таких нет - последнюю
     * завершившуюся.
     */
    private fun List<WorkInfo>.actual(): WorkInfo? = firstOrNull { !it.state.isFinished } ?: lastOrNull()

    /**
     * Переводит состояние работы в состояние отправки.
     *
     * Отменённая работа - это работа, вытесненная отправкой новой фотографии, показывать по ней
     * нечего.
     */
    private fun WorkInfo?.toUploadState(): PhotoUploadState = when (this?.state) {
        WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING, WorkInfo.State.BLOCKED -> PhotoUploadState.InProgress
        WorkInfo.State.SUCCEEDED -> PhotoUploadState.Success
        WorkInfo.State.FAILED -> PhotoUploadState.Failed
        WorkInfo.State.CANCELLED, null -> PhotoUploadState.Idle
    }
}

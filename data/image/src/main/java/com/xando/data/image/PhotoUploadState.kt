package com.xando.data.image

/**
 * Состояние отправки фотографии на сервер.
 *
 * Отправка живёт в WorkManager и переживает уход с экрана, смерть процесса и перезагрузку
 * устройства, поэтому наблюдать за ней может любой экран, а не только тот, с которого её начали.
 */
sealed interface PhotoUploadState {

    /** Отправок нет: ни выполняющихся, ни завершившихся непоказанным результатом. */
    data object Idle : PhotoUploadState

    /** Отправка выполняется или ждёт выполнения условий, например появления сети. */
    data object InProgress : PhotoUploadState

    /** Фотография отправлена. */
    data object Success : PhotoUploadState

    /** Отправить фотографию не удалось, повторять бессмысленно. */
    data object Failed : PhotoUploadState
}

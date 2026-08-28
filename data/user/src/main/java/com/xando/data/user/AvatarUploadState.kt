package com.xando.data.user

/**
 * Состояние загрузки фотографии профиля на сервер.
 *
 * Загрузка живёт в WorkManager и переживает уход с экрана, смерть процесса и перезагрузку
 * устройства, поэтому наблюдать за ней может любой экран, а не только тот, с которого её запустили.
 */
sealed interface AvatarUploadState {

    /** Загрузок нет: ни выполняющихся, ни завершившихся непоказанным результатом. */
    data object Idle : AvatarUploadState

    /** Загрузка выполняется или ждёт выполнения условий, например появления сети. */
    data object InProgress : AvatarUploadState

    /** Фотография загружена. */
    data object Success : AvatarUploadState

    /** Загрузить фотографию не удалось, повторять бессмысленно. */
    data object Failed : AvatarUploadState
}

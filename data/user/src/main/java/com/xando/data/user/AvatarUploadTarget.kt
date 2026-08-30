package com.xando.data.user

import com.xando.data.image.PhotoUploadTarget

/**
 * Адресат отправки фотографии профиля.
 *
 * Общий для всех экранов, которые отправляют аватар: ресурс на сервере один, поэтому и имя работы у них
 * должно быть одно - новая фотография обязана вытеснять предыдущую, ещё не отправленную.
 */
val AvatarUploadTarget = PhotoUploadTarget(
    workerClass = AvatarUploadWorker::class.java,
    workName = "user_avatar",
)

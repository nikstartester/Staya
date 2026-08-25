package com.xando.auth.sign_up.domain.use_case

import android.net.Uri
import com.xando.data.user.AvatarUploader
import javax.inject.Inject

/**
 * Юзкейс отправки аватара пользователя.
 */
// TODO: будет в общем модуле.
internal class AvatarUploadUseCase @Inject constructor(
    private val avatarUploader: AvatarUploader
) {

    /**
     * Ставит в очередь загрузку фотографии профиля. Требует уже подтверждённого e-mail.
     *
     * @param photoUri Uri выбранной пользователем фотографии.
     */
    fun uploadAvatar(photoUri: Uri) {
        avatarUploader.enqueue(photoUri)
    }

    /**
     * Сохраняет выбранную фотографию в кеш приложения, чтобы доступ к ней не зависел от гранта пикера.
     *
     * @param photoUri Uri, выданный пикером.
     * @return Uri локальной копии.
     */
    suspend fun cachePhoto(photoUri: Uri): Uri = avatarUploader.cache(photoUri)
}
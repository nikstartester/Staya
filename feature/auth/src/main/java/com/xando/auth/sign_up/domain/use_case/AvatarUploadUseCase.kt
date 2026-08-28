package com.xando.auth.sign_up.domain.use_case

import android.net.Uri
import com.xando.data.user.AvatarUploadState
import com.xando.data.user.AvatarUploader
import kotlinx.coroutines.flow.Flow

/**
 * Юзкейс отправки аватара пользователя.
 *
 * Собирается DI-модулем того, кто загрузку начинает: [avatarUploader] создаётся под его метку, и
 * поэтому юзкейс видит только его загрузки.
 */
// TODO: будет в общем модуле.
internal class AvatarUploadUseCase(private val avatarUploader: AvatarUploader) {

    /**
     * Состояние текущей или последней завершившейся загрузки фотографии профиля.
     *
     * Завершившаяся загрузка выдаётся один раз: показать её результат можно только тому, кто
     * подписан в этот момент.
     */
    val uploadState: Flow<AvatarUploadState> = avatarUploader.state

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
package com.xando.auth.sign_up.domain.use_case

import android.graphics.RectF
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
     * Ранее выбранная фотография сохраняется: заменит её только подтверждённая через [cropPhoto].
     *
     * @param photoUri Uri, выданный пикером.
     * @return Uri локальной копии.
     */
    suspend fun cachePhoto(photoUri: Uri): Uri = avatarUploader.cache(photoUri)

    /**
     * Обрезает фотографию по выбранной пользователем области и делает её фотографией профиля.
     *
     * @param photoUri Uri локальной копии, полученной из [cachePhoto].
     * @param cropRect Область в долях сторон копии: границы лежат в 0..1, а не в пикселях.
     * @return Uri обрезанной копии.
     */
    suspend fun cropPhoto(photoUri: Uri, cropRect: RectF): Uri =
        avatarUploader.crop(photoUri, cropRect)

    /**
     * Удаляет копию фотографии, от которой пользователь отказался.
     */
    suspend fun discardPhoto(photoUri: Uri) = avatarUploader.discard(photoUri)
}
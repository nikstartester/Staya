package com.xando.auth.sign_up.domain.use_case

import android.graphics.RectF
import android.net.Uri
import com.xando.data.image.PhotoLocalStorage
import com.xando.data.image.PhotoUploadState
import com.xando.data.image.PhotoUploader
import kotlinx.coroutines.flow.Flow

/**
 * Юзкейс выбора и отправки фотографии профиля.
 *
 * Хранилище общее для всех экранов, отправляющих аватар, а [photoUploader] собирается под метку того,
 * кто отправку начинает - поэтому юзкейс видит только свои отправки.
 */
internal class AvatarUploadUseCase(
    private val photoLocalStorage: PhotoLocalStorage,
    private val photoUploader: PhotoUploader,
) {

    /**
     * Состояние текущей или последней завершившейся отправки фотографии профиля.
     *
     * Завершившаяся отправка выдаётся один раз: показать её результат можно только тому, кто
     * подписан в этот момент.
     */
    val uploadState: Flow<PhotoUploadState> = photoUploader.state

    /**
     * Сохраняет выбранную фотографию в кеш приложения, чтобы доступ к ней не зависел от гранта пикера.
     *
     * Ранее выбранная фотография сохраняется: заменит её только подтверждённая через [cutPhoto].
     *
     * @param photoUri Uri, выданный пикером.
     * @return Uri локальной копии.
     */
    suspend fun cachePhoto(photoUri: Uri): Uri = photoLocalStorage.save(photoUri)

    /**
     * Обрезает фотографию по выбранной пользователем области и делает её фотографией профиля.
     *
     * Это точка подтверждения выбора: прежняя копия удаляется, а её отправка отменяется - без
     * локальной копии завершить отправку всё равно нечем.
     *
     * @param photoUri Uri локальной копии, полученной из [cachePhoto].
     * @param cropRect Область в долях сторон копии: границы лежат в 0..1, а не в пикселях.
     * @return Uri обрезанной копии.
     */
    suspend fun cutPhoto(photoUri: Uri, cropRect: RectF): Uri {
        val croppedPhotoUri = photoLocalStorage.cut(photoUri, cropRect)

        photoUploader.cancel()
        photoLocalStorage.keepOnly(croppedPhotoUri)

        return croppedPhotoUri
    }

    /**
     * Удаляет копию фотографии, от которой пользователь отказался.
     */
    suspend fun discardPhoto(photoUri: Uri) = photoLocalStorage.delete(photoUri)

    /**
     * Ставит в очередь отправку фотографии профиля. Требует уже подтверждённого e-mail.
     *
     * @param photoUri Uri локальной копии, подтверждённой в [cutPhoto].
     */
    fun uploadAvatar(photoUri: Uri) {
        photoUploader.enqueue(photoUri)
    }
}

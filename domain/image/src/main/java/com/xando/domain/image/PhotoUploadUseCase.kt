package com.xando.domain.image

import android.graphics.RectF
import android.net.Uri
import com.xando.data.image.PhotoLocalStorage
import com.xando.data.image.PhotoUploadState
import com.xando.data.image.PhotoUploader
import kotlinx.coroutines.flow.Flow

/**
 * Юзкейс выбора и отправки фотографии.
 *
 * Сценарий один и тот же для любой фотографии: выбрать, положить копию в кеш, при необходимости
 * обрезать, подтвердить выбор и поставить отправку в очередь. Отличается только конфигурация -
 * [photoLocalStorage] знает каталог ресурса, а [photoUploader] собран под метку того, кто отправку
 * начинает, поэтому юзкейс видит только свои отправки.
 *
 * Шаг обрезки необязателен: кому она не нужна, тот подтверждает выбор сразу после [cachePhoto].
 */
class PhotoUploadUseCase(
    private val photoLocalStorage: PhotoLocalStorage,
    private val photoUploader: PhotoUploader,
) {

    /**
     * Состояние отправки, начатой этим юзкейсом.
     *
     * Для того, кто отправку начал: по нему показывают её результат.
     */
    val originUploadState: Flow<PhotoUploadState> = photoUploader.originState

    /**
     * Состояние отправки в тот же ресурс, кто бы её ни начал.
     */
    val workUploadState: Flow<PhotoUploadState> = photoUploader.workState

    /**
     * Сохраняет выбранную фотографию в кеш приложения, чтобы доступ к ней не зависел от гранта пикера.
     *
     * Ранее выбранная фотография остаётся на месте: заменит её только [confirmPhoto].
     *
     * @param photoUri Uri, выданный пикером.
     * @return Uri локальной копии.
     */
    suspend fun cachePhoto(photoUri: Uri): Uri = photoLocalStorage.save(photoUri)

    /**
     * Обрезает копию по выбранной пользователем области.
     *
     * Результат - ещё одна копия рядом с исходной: выбор этим не подтверждается, отказаться от него
     * можно через [discardPhoto].
     *
     * @param photoUri Uri локальной копии, полученной из [cachePhoto].
     * @param cutRect Область в долях сторон копии: границы лежат в 0..1, а не в пикселях.
     * @return Uri обрезанной копии.
     */
    suspend fun cutPhoto(photoUri: Uri, cutRect: RectF): Uri = photoLocalStorage.cut(photoUri, cutRect)

    /**
     * Подтверждает выбор: копия по [photoUri] становится единственной, а все прочие удаляются.
     *
     * Отправка прежней фотографии отменяется - без локальной копии завершить её всё равно нечем.
     */
    suspend fun confirmPhoto(photoUri: Uri) {
        photoUploader.cancel()
        photoLocalStorage.keepOnly(photoUri)
    }

    /**
     * Удаляет копию фотографии, от которой пользователь отказался.
     */
    suspend fun discardPhoto(photoUri: Uri) = photoLocalStorage.delete(photoUri)

    /**
     * Ставит в очередь отправку подтверждённой фотографии.
     *
     * @param photoUri Uri локальной копии, подтверждённой в [confirmPhoto].
     */
    fun upload(photoUri: Uri) {
        photoUploader.enqueue(photoUri)
    }
}

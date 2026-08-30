package com.xando.data.image

import android.content.ContentResolver
import android.content.Context
import android.graphics.RectF
import android.net.Uri
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * Каталог локальных копий фотографии.
 *
 * Системный фотопикер выдаёт [Uri] с временным доступом на чтение, который не переживает смерть
 * процесса и не может быть продлён через `takePersistableUriPermission`. Поэтому файл копируется
 * в кеш приложения сразу при выборе, и дальше работа идёт уже со своей копией.
 *
 * Копия сохраняется уменьшенным JPEG независимо от исходного формата.
 *
 * Копии не вытесняют друг друга: только что выбранная фотография лежит рядом с прежней, пока
 * пользователь не подтвердит выбор. До подтверждения от новой копии можно отказаться через [delete],
 * а после - убрать все остальные через [keepOnly]. Так отмена не стоит пользователю уже выбранной
 * ранее фотографии.
 *
 * Каталог задаётся ресурсом, а не экраном: две фичи, правящие одну и ту же фотографию, работают с
 * одним каталогом, и выбор, сделанный в одной из них, корректно вытесняет выбор из другой.
 *
 * За пиксели отвечает [PhotoProcessor], хранилище только раскладывает готовые байты по файлам.
 *
 * @param directoryName Имя каталога копий внутри кеша приложения.
 * @param maxSizePx Максимальная сторона копии в пикселях.
 */
class PhotoLocalStorage @AssistedInject internal constructor(
    @Assisted private val directoryName: String,
    @Assisted private val maxSizePx: Int,
    private val processor: PhotoProcessor,
    @param:ApplicationContext private val context: Context,
) {

    /**@SelfDocumented*/
    @AssistedFactory
    interface Factory {

        /**@SelfDocumented*/
        fun create(directoryName: String, maxSizePx: Int = MAX_SIZE_PX): PhotoLocalStorage
    }

    private companion object {

        /** Максимальная сторона копии в пикселях. */
        const val MAX_SIZE_PX = 1536
    }

    private val directory: File
        get() = File(context.cacheDir, directoryName)

    /**
     * Сохраняет фотографию по [sourceUri] в кеш приложения, уменьшая её и перекодируя в JPEG.
     *
     * @param sourceUri Uri, выданный пикером.
     * @return Uri локальной копии.
     * @throws IllegalStateException Если изображение не удалось прочитать или его формат не
     * поддерживается системным декодером.
     */
    suspend fun save(sourceUri: Uri): Uri = withContext(Dispatchers.IO) {
        write(processor.normalize(sourceUri, maxSizePx))
    }

    /**
     * Вырезает из копии по [sourceUri] квадрат, заданный [cutRect], и сохраняет его отдельной копией.
     *
     * Обрезается именно копия из [save]: она уже развёрнута по EXIF и приведена к JPEG, поэтому в
     * квадрат попадают ровно те пиксели, которые пользователь видел, когда выбирал область.
     *
     * @param sourceUri Uri копии, полученной из [save].
     * @param cutRect Область в долях сторон копии: границы лежат в 0..1, а не в пикселях.
     * @return Uri обрезанной копии.
     * @throws IllegalStateException Если копию не удалось прочитать или сжать.
     */
    suspend fun cut(sourceUri: Uri, cutRect: RectF): Uri = withContext(Dispatchers.IO) {
        write(processor.cutSquare(sourceUri, cutRect))
    }

    /**
     * Удаляет все копии, кроме копии по [photoUri].
     */
    suspend fun keepOnly(photoUri: Uri) {
        withContext(Dispatchers.IO) {
            val kept = photoUri.toLocalFile()
            directory.listFiles()?.forEach { file -> if (file != kept) file.delete() }
        }
    }

    /**
     * Удаляет копию по [photoUri]. Uri, указывающий не на копию, игнорируется.
     */
    suspend fun delete(photoUri: Uri) {
        withContext(Dispatchers.IO) { photoUri.toLocalFile()?.delete() }
    }

    /**
     * Удаляет все копии.
     */
    fun clear() {
        directory.deleteRecursively()
    }

    /**
     * Кладёт [bytes] новым файлом каталога.
     */
    private fun write(bytes: ByteArray): Uri {
        directory.mkdirs()

        val target = File(directory, "${UUID.randomUUID()}.${PhotoProcessor.EXTENSION}")
        target.writeBytes(bytes)

        return Uri.fromFile(target)
    }

    /**
     * Приводит [Uri] к файлу копии. Возвращает `null`, если Uri указывает не на копию.
     */
    private fun Uri.toLocalFile(): File? {
        if (scheme != ContentResolver.SCHEME_FILE) return null

        val file = File(path ?: return null)

        return file.takeIf { it.parentFile == directory }
    }
}

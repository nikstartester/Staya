package com.xando.data.user

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.exifinterface.media.ExifInterface
import com.xando.data.user.AvatarLocalStorage.Companion.MAX_SIZE_PX
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Локальная копия выбранной фотографии профиля.
 *
 * Системный фотопикер выдаёт [Uri] с временным доступом на чтение, который не переживает смерть
 * процесса и не может быть продлён через `takePersistableUriPermission`. Поэтому файл копируется
 * в кеш приложения сразу при выборе, и дальше флоу работает уже со своей копией.
 *
 * Копия сохраняется уменьшенным JPEG независимо от исходного формата.
 *
 * Копии не вытесняют друг друга: только что выбранная фотография лежит рядом с прежней, пока
 * пользователь не подтвердит выбор. До подтверждения от новой копии можно отказаться через [delete],
 * а после - убрать все остальные через [keepOnly]. Так отмена обрезки не стоит пользователю уже
 * выбранной ранее фотографии.
 */
class AvatarLocalStorage @Inject constructor(@param:ApplicationContext private val context: Context) {

    private companion object {
        const val DIRECTORY_NAME = "signup_avatar"
        const val EXTENSION = "jpg"

        /** Максимальная сторона копии в пикселях. */
        const val MAX_SIZE_PX = 1024

        const val QUALITY = 90
    }

    private val directory: File
        get() = File(context.cacheDir, DIRECTORY_NAME)

    /**
     * Сохраняет фотографию по [sourceUri] в кеш приложения, уменьшая её и перекодируя в JPEG.
     *
     * @param sourceUri Uri, выданный пикером.
     * @return Uri локальной копии.
     * @throws IllegalStateException Если изображение не удалось прочитать или его формат не
     * поддерживается системным декодером.
     */
    suspend fun save(sourceUri: Uri): Uri = withContext(Dispatchers.IO) {
        Uri.fromFile(write(flattenAlpha(scaleDown(decode(sourceUri))), sourceUri))
    }

    /**
     * Вырезает из копии по [sourceUri] квадрат, заданный [cropRect], и сохраняет его отдельной копией.
     *
     * Обрезается именно копия из [save]: она уже развёрнута по EXIF и приведена к JPEG, поэтому в
     * квадрат попадают ровно те пиксели, которые пользователь видел, когда выбирал область.
     *
     * @param sourceUri Uri копии, полученной из [save].
     * @param cropRect Область в долях сторон копии: границы лежат в 0..1, а не в пикселях.
     * @return Uri обрезанной копии.
     * @throws IllegalStateException Если копию не удалось прочитать или сжать.
     */
    suspend fun crop(sourceUri: Uri, cropRect: RectF): Uri = withContext(Dispatchers.IO) {
        Uri.fromFile(write(cutOutSquare(decode(sourceUri), cropRect), sourceUri))
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
     * Удаляет локальные копии фотографии.
     */
    fun clear() {
        directory.deleteRecursively()
    }

    /**
     * Пишет [bitmap] новой копией и освобождает его.
     *
     * @param sourceUri Uri источника, нужен только для сообщения об ошибке.
     */
    private fun write(bitmap: Bitmap, sourceUri: Uri): File {
        directory.mkdirs()

        val target = File(directory, "${UUID.randomUUID()}.$EXTENSION")
        val isCompressed = try {
            target.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, it) }
        } finally {
            bitmap.recycle()
        }
        check(isCompressed) { "Не удалось сжать изображение по uri: $sourceUri" }

        return target
    }

    /**
     * Вырезает из [bitmap] квадрат, заданный [cropRect].
     *
     * Область задавалась квадратом, и стороны здесь расходятся разве что на пиксель округления,
     * поэтому за сторону берётся меньшая из них.
     *
     * @param cropRect Область в долях сторон [bitmap]: границы лежат в 0..1, а не в пикселях.
     */
    private fun cutOutSquare(bitmap: Bitmap, cropRect: RectF): Bitmap {
        val left = (cropRect.left * bitmap.width).roundToInt().coerceIn(0, bitmap.width - 1)
        val top = (cropRect.top * bitmap.height).roundToInt().coerceIn(0, bitmap.height - 1)
        val right = (cropRect.right * bitmap.width).roundToInt().coerceIn(left + 1, bitmap.width)
        val bottom = (cropRect.bottom * bitmap.height).roundToInt().coerceIn(top + 1, bitmap.height)

        val side = min(right - left, bottom - top)

        val cropped = Bitmap.createBitmap(bitmap, left, top, side, side)
        // createBitmap отдаёт исходный битмап, когда область совпала с ним целиком.
        if (cropped !== bitmap) bitmap.recycle()

        return cropped
    }

    /**
     * Приводит [Uri] к файлу копии. Возвращает `null`, если Uri указывает не на копию.
     */
    private fun Uri.toLocalFile(): File? {
        if (scheme != ContentResolver.SCHEME_FILE) return null

        val file = File(path ?: return null)

        return file.takeIf { it.parentFile == directory }
    }

    /**
     * Декодирует изображение, сразу отбрасывая лишние пиксели.
     *
     * Формат определяет системный декодер: что он не открыл - то приложение и не поддерживает.
     * Так, avif читается только с Android 12.
     */
    private fun decode(sourceUri: Uri): Bitmap {
        val bitmap = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                decodeWithImageDecoder(sourceUri)
            } else {
                decodeWithBitmapFactory(sourceUri)
            }
        } catch (ex: IOException) {
            throw IllegalStateException("Не удалось декодировать изображение по uri: $sourceUri", ex)
        }

        return bitmap ?: error("Не удалось декодировать изображение по uri: $sourceUri")
    }

    /**
     * Декодирует изображение через [ImageDecoder]. Ориентация из EXIF применяется декодером сама.
     */
    @RequiresApi(Build.VERSION_CODES.P)
    private fun decodeWithImageDecoder(sourceUri: Uri): Bitmap {
        val source = ImageDecoder.createSource(context.contentResolver, sourceUri)

        return ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            // Аппаратный битмап нельзя нарисовать в софтверный Canvas, а он нужен для ресайза.
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            decoder.setTargetSampleSize(calculateSampleSize(info.size.width, info.size.height))
        }
    }

    /**
     * Декодирует изображение для версий до [Build.VERSION_CODES.P], где нет [ImageDecoder].
     */
    private fun decodeWithBitmapFactory(sourceUri: Uri): Bitmap? {
        val contentResolver = context.contentResolver

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        contentResolver.openInputStream(sourceUri)?.use { BitmapFactory.decodeStream(it, null, bounds) }

        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateSampleSize(bounds.outWidth, bounds.outHeight)
        }
        val bitmap = contentResolver.openInputStream(sourceUri)
            ?.use { BitmapFactory.decodeStream(it, null, options) }
            ?: return null

        return applyExifRotation(bitmap, sourceUri)
    }

    /**
     * Подбирает степень двойки, при которой изображение остаётся не меньше [MAX_SIZE_PX] по
     * большей стороне: точный размер доводится уже ресайзом.
     */
    private fun calculateSampleSize(width: Int, height: Int): Int {
        var sampleSize = 1
        while (max(width, height) / (sampleSize * 2) >= MAX_SIZE_PX) {
            sampleSize *= 2
        }

        return sampleSize
    }

    /**
     * Разворачивает изображение по ориентации из EXIF.
     */
    private fun applyExifRotation(bitmap: Bitmap, sourceUri: Uri): Bitmap {
        val orientation = context.contentResolver.openInputStream(sourceUri)?.use { input ->
            ExifInterface(input).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        } ?: ExifInterface.ORIENTATION_NORMAL

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap
        }

        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()

        return rotated
    }

    /**
     * Уменьшает изображение до [MAX_SIZE_PX] по большей стороне, сохраняя пропорции.
     */
    private fun scaleDown(bitmap: Bitmap): Bitmap {
        val maxDimension = max(bitmap.width, bitmap.height)
        if (maxDimension <= MAX_SIZE_PX) return bitmap

        val scale = MAX_SIZE_PX.toFloat() / maxDimension
        val scaled = bitmap.scale(
            (bitmap.width * scale).roundToInt().coerceAtLeast(1),
            (bitmap.height * scale).roundToInt().coerceAtLeast(1),
        )
        bitmap.recycle()

        return scaled
    }

    /**
     * Кладёт изображение на белый фон.
     *
     * JPEG не хранит альфу, и без заливки прозрачные пиксели png или webp станут чёрными.
     */
    private fun flattenAlpha(bitmap: Bitmap): Bitmap {
        if (!bitmap.hasAlpha()) return bitmap

        val flattened = createBitmap(bitmap.width, bitmap.height)
        Canvas(flattened).apply {
            drawColor(Color.WHITE)
            drawBitmap(bitmap, 0f, 0f, null)
        }
        bitmap.recycle()

        return flattened
    }
}

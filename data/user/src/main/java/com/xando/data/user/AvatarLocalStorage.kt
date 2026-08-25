package com.xando.data.user

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Matrix
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
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Локальная копия выбранной фотографии профиля.
 *
 * Системный фотопикер выдаёт [Uri] с временным доступом на чтение, который не переживает смерть
 * процесса и не может быть продлён через `takePersistableUriPermission`. Поэтому файл копируется
 * в кеш приложения сразу при выборе, и дальше флоу работает уже со своей копией.
 *
 * Копия сохраняется уменьшенным JPEG независимо от исходного формата.
 */
@Singleton
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
        val bitmap = flattenAlpha(scaleDown(decode(sourceUri)))

        clear()
        directory.mkdirs()

        val target = File(directory, "${UUID.randomUUID()}.$EXTENSION")
        val isCompressed = try {
            target.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, it) }
        } finally {
            bitmap.recycle()
        }
        check(isCompressed) { "Не удалось сжать изображение по uri: $sourceUri" }

        Uri.fromFile(target)
    }

    /**
     * Удаляет локальную копию фотографии.
     */
    fun clear() {
        directory.deleteRecursively()
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

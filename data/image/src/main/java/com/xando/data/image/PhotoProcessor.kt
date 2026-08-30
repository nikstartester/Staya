package com.xando.data.image

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
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Обработка фотографии: декодирование, преобразование и кодирование. Наружу отдаёт готовые байты.
 *
 * Результат всегда JPEG - см. [EXTENSION].
 */
internal class PhotoProcessor @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    companion object {

        /** Расширение файла, соответствующее формату, в котором процессор отдаёт байты. */
        const val EXTENSION = "jpg"

        private const val QUALITY = 90

        /** Предел, при котором прореживание не нужно: источник уже нужного размера. */
        private const val NO_SUBSAMPLING = Int.MAX_VALUE
    }

    /**
     * Приводит фотографию по [sourceUri] к виду, пригодному для хранения и отправки: разворачивает
     * по EXIF, уменьшает до [maxSizePx] по большей стороне и перекодирует в JPEG.
     *
     * @param sourceUri Uri, выданный пикером.
     * @param maxSizePx Максимальная сторона результата в пикселях.
     * @throws IllegalStateException Если изображение не удалось прочитать, его формат не
     * поддерживается системным декодером или результат не удалось сжать.
     */
    fun normalize(sourceUri: Uri, maxSizePx: Int): ByteArray =
        encode(flattenAlpha(scaleDown(decode(sourceUri, maxSizePx), maxSizePx)), sourceUri)

    /**
     * Вырезает из фотографии по [sourceUri] квадрат, заданный [cutRect].
     *
     * Ожидается уже нормализованная копия из [normalize]: она развёрнута по EXIF и приведена к
     * JPEG, поэтому в квадрат попадают ровно те пиксели, которые пользователь видел, когда выбирал
     * область.
     *
     * @param cutRect Область в долях сторон изображения: границы лежат в 0..1, а не в пикселях.
     * @throws IllegalStateException Если изображение не удалось прочитать или сжать.
     */
    fun cutSquare(sourceUri: Uri, cutRect: RectF): ByteArray =
        encode(cutOutSquare(decode(sourceUri, maxSizePx = NO_SUBSAMPLING), cutRect), sourceUri)

    /**
     * Кодирует [bitmap] в JPEG и освобождает его.
     *
     * @param sourceUri Uri источника, нужен только для сообщения об ошибке.
     */
    private fun encode(bitmap: Bitmap, sourceUri: Uri): ByteArray {
        val output = ByteArrayOutputStream()
        val isCompressed = try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, output)
        } finally {
            bitmap.recycle()
        }
        check(isCompressed) { "Не удалось сжать изображение по uri: $sourceUri" }

        return output.toByteArray()
    }

    /**
     * Вырезает из [bitmap] квадрат, заданный [cutRect].
     *
     * Область задавалась квадратом, и стороны здесь расходятся разве что на пиксель округления,
     * поэтому за сторону берётся меньшая из них.
     *
     * @param cutRect Область в долях сторон [bitmap]: границы лежат в 0..1, а не в пикселях.
     */
    private fun cutOutSquare(bitmap: Bitmap, cutRect: RectF): Bitmap {
        val left = (cutRect.left * bitmap.width).roundToInt().coerceIn(0, bitmap.width - 1)
        val top = (cutRect.top * bitmap.height).roundToInt().coerceIn(0, bitmap.height - 1)
        val right = (cutRect.right * bitmap.width).roundToInt().coerceIn(left + 1, bitmap.width)
        val bottom = (cutRect.bottom * bitmap.height).roundToInt().coerceIn(top + 1, bitmap.height)

        val side = min(right - left, bottom - top)

        val cut = Bitmap.createBitmap(bitmap, left, top, side, side)
        // createBitmap отдаёт исходный битмап, когда область совпала с ним целиком.
        if (cut !== bitmap) bitmap.recycle()

        return cut
    }

    /**
     * Декодирует изображение, сразу отбрасывая лишние пиксели.
     *
     * Формат определяет системный декодер: что он не открыл - то приложение и не поддерживает.
     * Так, avif читается только с Android 12.
     *
     * @param maxSizePx Размер, ниже которого прореживание уже не опускается.
     */
    private fun decode(sourceUri: Uri, maxSizePx: Int): Bitmap {
        val bitmap = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                decodeWithImageDecoder(sourceUri, maxSizePx)
            } else {
                decodeWithBitmapFactory(sourceUri, maxSizePx)
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
    private fun decodeWithImageDecoder(sourceUri: Uri, maxSizePx: Int): Bitmap {
        val source = ImageDecoder.createSource(context.contentResolver, sourceUri)

        return ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            // Аппаратный битмап нельзя нарисовать в софтверный Canvas, а он нужен для ресайза.
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            decoder.setTargetSampleSize(calculateSampleSize(info.size.width, info.size.height, maxSizePx))
        }
    }

    /**
     * Декодирует изображение для версий до [Build.VERSION_CODES.P], где нет [ImageDecoder].
     */
    private fun decodeWithBitmapFactory(sourceUri: Uri, maxSizePx: Int): Bitmap? {
        val contentResolver = context.contentResolver

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        contentResolver.openInputStream(sourceUri)?.use { BitmapFactory.decodeStream(it, null, bounds) }

        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateSampleSize(bounds.outWidth, bounds.outHeight, maxSizePx)
        }
        val bitmap = contentResolver.openInputStream(sourceUri)
            ?.use { BitmapFactory.decodeStream(it, null, options) }
            ?: return null

        return applyExifRotation(bitmap, sourceUri)
    }

    /**
     * Подбирает степень двойки, при которой изображение остаётся не меньше [maxSizePx] по
     * большей стороне: точный размер доводится уже ресайзом.
     */
    private fun calculateSampleSize(width: Int, height: Int, maxSizePx: Int): Int {
        var sampleSize = 1
        while (max(width, height) / (sampleSize * 2) >= maxSizePx) {
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
     * Уменьшает изображение до [maxSizePx] по большей стороне, сохраняя пропорции.
     */
    private fun scaleDown(bitmap: Bitmap, maxSizePx: Int): Bitmap {
        val maxDimension = max(bitmap.width, bitmap.height)
        if (maxDimension <= maxSizePx) return bitmap

        val scale = maxSizePx.toFloat() / maxDimension
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

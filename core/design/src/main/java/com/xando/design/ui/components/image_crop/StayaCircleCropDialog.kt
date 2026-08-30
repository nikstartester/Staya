package com.xando.design.ui.components.image_crop

import android.graphics.RectF
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.xando.core.design.R
import com.xando.design.ui.components.button.StayaButton
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sign
import coil3.size.Size as CoilSize

/** Отступ круга выделения от краёв экрана. На фотографию не распространяется: она закрывает экран целиком. */
private val CropPadding = 16.dp

/** Во сколько раз фотографию можно увеличить относительно масштаба, при котором она закрывает круг. */
private const val MAX_ZOOM = 5f

/** Во сколько раз за пределы допустимого масштаба фотографию вообще можно увести жестом. */
private const val SCALE_OVERSHOOT = 2f

/** Показатель степени затухания масштаба за пределами допустимого: чем меньше, тем жёстче упор. */
private const val SCALE_RESISTANCE = 0.35f

/** Коэффициент затухания смещения за пределами круга: чем меньше, тем жёстче упор. */
private const val OFFSET_RESISTANCE = 0.55f

private val ScrimColor = Color.Black.copy(alpha = 0.7f)

/** Возврат в границы после отпускания - без отскока: это подгонка кадра, а не пружина. */
private val SettleSpec = spring<Float>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow,
)

/**
 * Диалог обрезки фотографии по кругу.
 *
 * Занимает весь экран: всё вне круга затемнено, фотографию можно перемещать и масштабировать.
 * Круг фотография закрывает всегда - за его границы она выходит с сопротивлением и после отпускания
 * плавно возвращается обратно.
 *
 * Компонент не работает с файлами: он отдаёт только выбранную область в долях сторон изображения,
 * а обрезка пикселей остаётся за вызывающей стороной.
 *
 * @param model Изображение в понимании Coil: [android.net.Uri], путь, ссылка. Ожидается локальная
 * копия разумного размера - она декодируется целиком.
 * @param onCropConfirmed Вызывается с выбранной областью по кнопке подтверждения. Границы области -
 * доли сторон изображения в 0..1, а не пиксели: так область остаётся верной для любой копии
 * изображения независимо от того, в каком разрешении её показывали при выборе.
 * @param onDismiss Вызывается при отказе от обрезки - по кнопке закрытия или системной кнопке "назад".
 */
@Composable
fun StayaCircleCropDialog(
    model: Any?,
    onCropConfirmed: (RectF) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        )
    ) {
        CircleCropContent(
            model = model,
            onCropConfirmed = onCropConfirmed,
            onDismiss = onDismiss,
        )
    }
}

/**
 * Содержимое диалога: редактор поверх чёрного фона и кнопка закрытия.
 *
 * Пока изображение не загружено, редактора нет - его геометрия целиком считается от размеров
 * исходного изображения.
 */
@Composable
private fun CircleCropContent(
    model: Any?,
    onCropConfirmed: (RectF) -> Unit,
    onDismiss: () -> Unit,
) {
    val platformContext = LocalPlatformContext.current
    val request = remember(model, platformContext) {
        ImageRequest.Builder(platformContext)
            .data(model)
            // Область считается в долях сторон, но пропорции обязаны совпасть с исходником.
            .size(CoilSize.ORIGINAL)
            .build()
    }

    val painter = rememberAsyncImagePainter(request)
    val painterState by painter.state.collectAsState()

    val imageSize = (painterState as? AsyncImagePainter.State.Success)?.painter?.intrinsicSize

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (imageSize != null && imageSize.isSpecified && imageSize.minDimension > 0f) {
            CircleCropEditor(
                painter = painter,
                imageSize = imageSize,
                onCropConfirmed = onCropConfirmed,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White,
            )
        }

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(4.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.design_ic_close_24px),
                contentDescription = stringResource(R.string.design_image_crop_close_content_description),
                tint = Color.White,
            )
        }
    }
}

/**
 * Редактор области: фотография, затемнение с окном-кругом и кнопка подтверждения.
 */
@Composable
private fun CircleCropEditor(
    painter: Painter,
    imageSize: Size,
    onCropConfirmed: (RectF) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val containerSize = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
        val containerCenter = Offset(containerSize.width / 2f, containerSize.height / 2f)
        val cropPadding = with(LocalDensity.current) { CropPadding.toPx() }
        val circleDiameter = (containerSize.minDimension - cropPadding * 2f).coerceAtLeast(1f)
        val circleRadius = circleDiameter / 2f

        // Открывается фотография во весь экран: круг меньше неё на CropPadding с каждой стороны.
        val initialScale = max(
            containerSize.minDimension / imageSize.width,
            containerSize.minDimension / imageSize.height,
        )

        // Минимум - фотография, вписанная в круг: меньше него круг остался бы незакрытым. Свести
        // пальцы сильнее можно, но это уже затухающий перелёт, к которому она и не приходит.
        val minScale = max(circleDiameter / imageSize.width, circleDiameter / imageSize.height)
        val maxScale = minScale * MAX_ZOOM

        // Накопители жеста: показываются они с затуханием, а копятся без него, иначе затухание
        // складывалось бы само с собой на каждом кадре и утягивало фотографию к границе.
        var rawScale by remember(initialScale) { mutableFloatStateOf(initialScale) }
        var rawOffset by remember(initialScale) { mutableStateOf(Offset.Zero) }

        val scope = rememberCoroutineScope()

        val scrimPath = remember(containerSize, circleRadius) {
            Path().apply {
                addRect(Rect(Offset.Zero, containerSize))
                addOval(Rect(center = containerCenter, radius = circleRadius))
                fillType = PathFillType.EvenOdd
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(imageSize, circleDiameter, minScale, maxScale) {
                    var settleJob: Job? = null

                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false)
                        settleJob?.cancel()

                        var event: PointerEvent
                        do {
                            event = awaitPointerEvent()
                            if (event.changes.any { it.isConsumed }) break

                            val zoom = event.calculateZoom()
                            val pan = event.calculatePan()
                            if (zoom == 1f && pan == Offset.Zero) continue

                            val centroid = event.calculateCentroid(useCurrent = false)
                            val newScale = (rawScale * zoom)
                                .coerceIn(minScale / SCALE_OVERSHOOT, maxScale * SCALE_OVERSHOOT)

                            // Считаем по видимому масштабу, а не по накопленному: на упоре видимый
                            // масштаб уже не растёт, и по накопленному фотография уезжала бы в
                            // сторону вместо того, чтобы стоять на месте.
                            val visibleZoom = rubberBandScale(newScale, minScale, maxScale) /
                                    rubberBandScale(rawScale, minScale, maxScale)

                            // Точка под пальцами остаётся на месте: масштабируем вокруг неё, а не
                            // вокруг центра круга.
                            val pivot = centroid - containerCenter
                            rawOffset = pivot - (pivot - rawOffset) * visibleZoom + pan
                            rawScale = newScale

                            event.changes.forEach { if (it.positionChanged()) it.consume() }
                        } while (event.changes.any { it.pressed })

                        settleJob = scope.launch {
                            val startScale = rawScale
                            val startOffset = rawOffset
                            val target = settleTarget(
                                scale = startScale,
                                offset = startOffset,
                                imageSize = imageSize,
                                minScale = minScale,
                                maxScale = maxScale,
                                circleDiameter = circleDiameter,
                            )

                            animate(initialValue = 0f, targetValue = 1f, animationSpec = SettleSpec) { fraction, _ ->
                                rawScale = startScale + (target.scale - startScale) * fraction
                                rawOffset = lerp(startOffset, target.offset, fraction)
                            }
                        }
                    }
                }
        ) {
            val scale = rubberBandScale(rawScale, minScale, maxScale)
            val offset = rubberBandOffset(rawOffset, imageSize, scale, circleDiameter)

            val drawSize = imageSize * scale
            val topLeft = center + offset - Offset(drawSize.width / 2f, drawSize.height / 2f)

            translate(left = topLeft.x, top = topLeft.y) {
                with(painter) { draw(drawSize) }
            }

            drawPath(path = scrimPath, color = ScrimColor)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.design_image_crop_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            StayaButton(
                text = stringResource(R.string.design_image_crop_confirm),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    // Считаем по допустимым значениям: подтвердить могли и на упоре, и во время возврата.
                    val target = settleTarget(
                        scale = rawScale,
                        offset = rawOffset,
                        imageSize = imageSize,
                        minScale = minScale,
                        maxScale = maxScale,
                        circleDiameter = circleDiameter,
                    )

                    onCropConfirmed(cropRect(imageSize, target.scale, target.offset, circleDiameter))
                },
            )
        }
    }
}

/** Положение фотографии: масштаб и смещение её центра относительно центра круга. */
private data class CropTransform(val scale: Float, val offset: Offset)

/**
 * Приводит положение фотографии к допустимым значениям.
 *
 * Масштаб возвращается вокруг центра круга, а не вокруг центра фотографии: точка изображения под
 * центром круга остаётся на месте. Иначе, съезжая с упора, фотография уходила бы в сторону.
 */
private fun settleTarget(
    scale: Float,
    offset: Offset,
    imageSize: Size,
    minScale: Float,
    maxScale: Float,
    circleDiameter: Float,
): CropTransform {
    val settledScale = scale.coerceIn(minScale, maxScale)
    val anchoredOffset = offset * (settledScale / rubberBandScale(scale, minScale, maxScale))

    return CropTransform(
        scale = settledScale,
        offset = clampOffset(anchoredOffset, imageSize, settledScale, circleDiameter),
    )
}

/**
 * Насколько центр фотографии можно увести от центра круга, чтобы круг остался закрытым.
 */
private fun offsetLimit(imageDimension: Float, scale: Float, circleDiameter: Float): Float =
    ((imageDimension * scale - circleDiameter) / 2f).coerceAtLeast(0f)

/**
 * Загоняет смещение в допустимые границы.
 */
private fun clampOffset(offset: Offset, imageSize: Size, scale: Float, circleDiameter: Float): Offset {
    val limitX = offsetLimit(imageSize.width, scale, circleDiameter)
    val limitY = offsetLimit(imageSize.height, scale, circleDiameter)

    return Offset(offset.x.coerceIn(-limitX, limitX), offset.y.coerceIn(-limitY, limitY))
}

/**
 * Смещение с сопротивлением: за границей оно растёт всё медленнее и упирается в [circleDiameter].
 */
private fun rubberBandOffset(offset: Offset, imageSize: Size, scale: Float, circleDiameter: Float): Offset {
    val limitX = offsetLimit(imageSize.width, scale, circleDiameter)
    val limitY = offsetLimit(imageSize.height, scale, circleDiameter)

    return Offset(
        rubberBand(offset.x, limitX, circleDiameter),
        rubberBand(offset.y, limitY, circleDiameter),
    )
}

/**
 * Затухание величины за пределами [limit]: перелёт стремится к [dimension], но не достигает его.
 */
private fun rubberBand(value: Float, limit: Float, dimension: Float): Float {
    val overshoot = abs(value) - limit
    if (overshoot <= 0f) return value

    val damped = dimension * (1f - 1f / (overshoot * OFFSET_RESISTANCE / dimension + 1f))

    return sign(value) * (limit + damped)
}

/**
 * Затухание масштаба за пределами допустимого диапазона.
 */
private fun rubberBandScale(scale: Float, minScale: Float, maxScale: Float): Float = when {
    scale < minScale -> minScale * (scale / minScale).pow(SCALE_RESISTANCE)
    scale > maxScale -> maxScale * (scale / maxScale).pow(SCALE_RESISTANCE)
    else -> scale
}

/**
 * Переводит положение фотографии в область изображения, попавшую в круг.
 *
 * Круг вписан в квадрат со стороной [circleDiameter], этот квадрат и становится областью обрезки.
 *
 * @return Область в долях сторон изображения: все четыре границы лежат в 0..1, а не в пикселях.
 */
private fun cropRect(imageSize: Size, scale: Float, offset: Offset, circleDiameter: Float): RectF {
    val drawnWidth = imageSize.width * scale
    val drawnHeight = imageSize.height * scale

    val left = (drawnWidth - circleDiameter) / 2f - offset.x
    val top = (drawnHeight - circleDiameter) / 2f - offset.y

    return RectF(
        (left / drawnWidth).coerceIn(0f, 1f),
        (top / drawnHeight).coerceIn(0f, 1f),
        ((left + circleDiameter) / drawnWidth).coerceIn(0f, 1f),
        ((top + circleDiameter) / drawnHeight).coerceIn(0f, 1f),
    )
}

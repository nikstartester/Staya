package com.xando.design.ui.components.photo_picker

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.xando.core.design.R
import com.xando.design.ui.theme.extendedColors

/** Размер иконки камеры, пока фото не выбрано. */
private val CameraIconSize = 40.dp

/** Отступ иконки и подписи от края круга: подпись не должна упираться в обводку. */
private val CaptionPadding = 12.dp

/** До какого размера может ужиматься подпись, чтобы уместиться в одну строку. */
private val CaptionMinFontSize = 6.sp

/** Размер кнопки удаления фото. */
private val RemoveButtonSize = 28.dp

/** Размер крестика внутри кнопки удаления фото. */
private val RemoveIconSize = 16.dp

/**
 * Круглый пикер фотографии. Нажатие открывает системный выбор изображения. Выбранное фото
 * показывается внутри круга, пока его нет - иконка камеры с подписью [caption]. Если передан
 * [onRemoveClick], поверх фото справа сверху показывается кнопка, которая его убирает.
 *
 * @param model Что показывать: Uri локальной копии, URL с сервера или `null`, если фото нет.
 * @param contentDescription Описание фото для accessibility.
 * @param onPhotoSelected Вызывается с Uri, выданным системным пикером.
 * @param modifier Модификатор для кастомизации компонента.
 * @param caption Подпись под иконкой камеры, пока фото нет.
 * @param onRemoveClick Обработчик кнопки удаления фото; `null`, если убирать фото нельзя.
 */
@Composable
fun StayaPhotoPicker(
    model: Any?,
    contentDescription: String,
    onPhotoSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier,
    caption: String = stringResource(R.string.design_photo_picker_caption),
    onRemoveClick: (() -> Unit)? = null,
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(contract = PickVisualMedia()) { uri ->
        if (uri != null) onPhotoSelected(uri)
    }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .clickable(role = Role.Button) {
                    photoPickerLauncher.launch(PickVisualMediaRequest(mediaType = PickVisualMedia.ImageOnly))
                },
            contentAlignment = Alignment.Center
        ) {
            if (model != null) {
                AsyncImage(
                    model = model,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    modifier = Modifier.padding(CaptionPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.size(CameraIconSize),
                        painter = painterResource(R.drawable.design_ic_photo_camera_24dp),
                        contentDescription = null,
                        tint = MaterialTheme.extendedColors.iconColor
                    )
                    Text(
                        text = caption,
                        autoSize = TextAutoSize.StepBased(
                            minFontSize = CaptionMinFontSize,
                            maxFontSize = MaterialTheme.typography.bodyMedium.fontSize
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        color = MaterialTheme.extendedColors.textColor
                    )
                }
            }
        }

        if (model != null && onRemoveClick != null) {
            RemovePhotoButton(
                onClick = onRemoveClick,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

/**
 * Небольшая круглая кнопка с крестиком поверх фото.
 */
@Composable
private fun RemovePhotoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(RemoveButtonSize)
            .clip(CircleShape)
            .background(MaterialTheme.extendedColors.secondaryBackgroundColor)
            .clickable(onClick = onClick, role = Role.Button),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.design_ic_close_24px),
            contentDescription = stringResource(R.string.design_photo_picker_remove_content_description),
            tint = MaterialTheme.extendedColors.secondaryBackgroundTextColor,
            modifier = Modifier.size(RemoveIconSize)
        )
    }
}

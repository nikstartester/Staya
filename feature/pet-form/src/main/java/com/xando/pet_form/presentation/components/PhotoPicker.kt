package com.xando.pet_form.presentation.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import coil3.compose.AsyncImage
import com.xando.design.ui.theme.extendedColors
import com.xando.feature.pet_form.R
import com.xando.core.design.R as RDesign

/** Размер кнопки удаления фото. */
private val RemovePhotoButtonSize = 28.dp

/** Размер крестика внутри кнопки удаления фото. */
private val RemovePhotoIconSize = 16.dp

/**
 * Круглый пикер фотографии питомца. Нажатие открывает системный выбор изображения; пока фото есть,
 * справа сверху показывается кнопка, которая его убирает.
 *
 * @param photoUri Uri новой локальной копии фото; показывается вместо [existingPhotoUrl].
 * @param existingPhotoUrl URL фото с сервера, пока пользователь не выбрал новое и не убрал его.
 * @param onPhotoSelected Вызывается с Uri, выданным системным пикером.
 * @param onPhotoRemove Вызывается по нажатию на кнопку удаления фото.
 */
@Composable
internal fun PhotoPicker(
    photoUri: Uri?,
    existingPhotoUrl: String?,
    onPhotoSelected: (Uri) -> Unit,
    onPhotoRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(contract = PickVisualMedia()) { uri ->
        if (uri != null) onPhotoSelected(uri)
    }
    val model = photoUri ?: existingPhotoUrl

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .clickable {
                    photoPickerLauncher.launch(PickVisualMediaRequest(mediaType = PickVisualMedia.ImageOnly))
                },
            contentAlignment = Alignment.Center
        ) {
            if (model != null) {
                AsyncImage(
                    model = model,
                    contentDescription = stringResource(R.string.pet_form_photo_content_description),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(RDesign.drawable.design_ic_photo_camera_24dp),
                        contentDescription = null,
                        tint = MaterialTheme.extendedColors.iconColor
                    )
                    Text(
                        text = stringResource(R.string.pet_form_photo_caption),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.extendedColors.textColor
                    )
                }
            }
        }

        if (model != null) {
            RemovePhotoButton(
                onClick = onPhotoRemove,
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
            .size(RemovePhotoButtonSize)
            .clip(CircleShape)
            .background(MaterialTheme.extendedColors.secondaryBackgroundColor)
            .clickable(onClick = onClick, role = Role.Button),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(RDesign.drawable.design_ic_close_24px),
            contentDescription = stringResource(R.string.pet_form_photo_remove),
            tint = MaterialTheme.extendedColors.secondaryBackgroundTextColor,
            modifier = Modifier.size(RemovePhotoIconSize)
        )
    }
}

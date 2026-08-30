package com.xando.auth.sign_up.snackbar

import com.xando.auth.sign_up.di.SignUpAvatar
import com.xando.data.image.PhotoUploadState
import com.xando.design.ui.snackbar.BackgroundSnackbarSource
import com.xando.design.ui.snackbar.SnackbarType
import com.xando.design.ui.snackbar.StayaSnackbarData
import com.xando.domain.image.PhotoUploadUseCase
import com.xando.feature.auth.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject
import com.xando.core.design.R as RDesign

/**
 * Сообщает о результате загрузки фотографии профиля.
 *
 * Фотография выбирается при регистрации, а загружается уже после ухода из auth-флоу, поэтому
 * показать результат экрану регистрации некому - это делает корень приложения.
 */
internal class AvatarUploadSnackbarSource @Inject constructor(
    @SignUpAvatar photoUploadUseCase: PhotoUploadUseCase,
) : BackgroundSnackbarSource {

    override val snackbars: Flow<StayaSnackbarData> = photoUploadUseCase.originUploadState
        // Первое значение описывает состояние на момент подписки, а не событие: неудачу, случившуюся
        // до запуска приложения показывать снова не нужно.
        .drop(1)
        .mapNotNull { state ->
            when (state) {
                PhotoUploadState.Failed -> StayaSnackbarData(
                    type = SnackbarType.ERROR,
                    messageResId = R.string.auth_avatar_upload_error,
                    iconRes = RDesign.drawable.design_ic_error_24px
                )

                // Об удачной загрузке пользователю знать незачем: он её и так увидит по фото в профиле.
                PhotoUploadState.Success, PhotoUploadState.InProgress, PhotoUploadState.Idle -> null
            }
        }
}

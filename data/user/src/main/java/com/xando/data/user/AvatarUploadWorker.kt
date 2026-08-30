package com.xando.data.user

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.xando.data.image.PhotoLocalStorage
import com.xando.data.image.PhotoUploadWorker
import com.xando.data.user.di.UserAvatar
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Отправляет на сервер фотографию профиля.
 *
 * Отправка возможна только после авторизации, а сразу после подтверждения e-mail приложение уходит
 * из auth-флоу вместе со своими ViewModel - поэтому она и живёт в воркере, переживающий и это, и
 * смерть процесса.
 */
@HiltWorker
internal class AvatarUploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: UserRepository,
    @param:UserAvatar private val localStorage: PhotoLocalStorage,
) : PhotoUploadWorker(context, workerParameters) {

    override suspend fun upload(photoUri: Uri) {
        repository.uploadAvatar(photoUri)
    }

    /**
     * Удаляется именно отправленная копия, а не весь каталог: пока шла отправка, пользователь мог
     * выбрать новую фотографию, и её копия должна остаться.
     */
    override suspend fun onUploaded(photoUri: Uri) {
        localStorage.delete(photoUri)
    }
}

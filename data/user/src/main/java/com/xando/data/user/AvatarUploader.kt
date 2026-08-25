package com.xando.data.user

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Загружает фотографию профиля в фоне, переживая уход с экрана.
 *
 * Загрузка возможна только после авторизации, а сразу после подтверждения e-mail приложение уходит
 * из auth-флоу вместе со своими ViewModel. Поэтому запрос выполняется в собственном scope.
 */
// TODO: Подход мне нравится, но пока так. Переделать. Создаем scope, но не чистим его.
@Singleton
class AvatarUploader @Inject constructor(
    private val repository: UserRepository,
    private val localStorage: AvatarLocalStorage,
) {

    private companion object {
        const val TAG = "AvatarUploader"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Кэширует фотографию по [sourceUri] в кеш приложения.
     *
     * @param sourceUri Uri, выданный пикером.
     * @return Uri локальной копии.
     * @throws IllegalStateException Если файл по [sourceUri] не удалось прочитать.
     */
    suspend fun cache(sourceUri: Uri): Uri = localStorage.save(sourceUri)

    /**
     * Ставит загрузку фотографии в очередь. Ошибка загрузки не прерывает пользовательский сценарий.
     */
    fun enqueue(photoUri: Uri) {
        scope.launch {
            try {
                repository.uploadAvatar(photoUri)
                localStorage.clear()
            } catch (ex: CancellationException) {
                throw ex
            } catch (th: Throwable) {
                // TODO: перейти на Timber и добавить повторную отправку.
                Log.w(TAG, "Не удалось загрузить фото профиля", th)
            }
        }
    }
}

package com.xando.auth.sign_up.domain.use_case

import com.xando.data.auth.AuthRepository
import javax.inject.Inject

/**
 * Юзкейс проверки логина на доступность.
 */
internal class CheckLoginAvailabilityUseCase @Inject constructor(private val repository: AuthRepository) {

    /**
     * Проверяет, свободен ли логин.
     *
     * @param login Проверяемый логин.
     * @return `true`, если логин свободен.
     */
    suspend fun isAvailable(login: String): Boolean = repository.checkLoginAvailability(login)
}

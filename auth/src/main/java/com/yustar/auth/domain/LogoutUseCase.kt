package com.yustar.auth.domain

import com.yustar.core.data.remote.model.Status
import com.yustar.core.data.repository.UserRepository
import com.yustar.core.session.SessionManager
import javax.inject.Inject

/**
 * Created by Yustar Pramudana on 09/03/26.
 */

class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke() {
        val token = sessionManager.getAccessToken()
        if (token != null) {
            val resource = userRepository.logout(token)
            if (resource.status == Status.SUCCESS) {
                sessionManager.logout()
            }
        } else {
            sessionManager.logout()
        }
    }
}

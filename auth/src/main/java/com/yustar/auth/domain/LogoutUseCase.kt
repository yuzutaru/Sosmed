package com.yustar.auth.domain

import com.yustar.core.session.SessionManager

/**
 * Created by Yustar Pramudana on 09/03/26.
 */

class LogoutUseCase(
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke() {
        sessionManager.logout()
    }
}

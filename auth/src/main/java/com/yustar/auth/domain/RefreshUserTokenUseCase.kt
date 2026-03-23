package com.yustar.auth.domain

import com.yustar.core.data.remote.model.RefreshTokenRequest
import com.yustar.core.data.remote.model.Status
import com.yustar.core.data.repository.UserRepository
import com.yustar.core.session.SessionManager
import javax.inject.Inject

/**
 * Created by Yustar Pramudana on 06/03/26.
 */

class RefreshUserTokenUseCase @Inject constructor(
    private val repository: UserRepository,
    private val session: SessionManager
) {
    suspend operator fun invoke(): Boolean {
        val refreshToken = session.getRefreshToken() ?: return false
        val response = repository.refreshToken(RefreshTokenRequest(refreshToken))
        val data = response.data

        return if (response.status == Status.SUCCESS && data != null) {
            session.updateAccessToken(data.accessToken)
            // Optionally update refresh token if the API returns a new one
            // session.saveTokens(data.accessToken, data.refreshToken) 
            true
        } else {
            false
        }
    }
}

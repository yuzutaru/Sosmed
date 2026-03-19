package com.yustar.auth.domain

import com.yustar.core.data.remote.model.LoginRequest
import com.yustar.core.data.remote.model.Status
import com.yustar.core.data.repository.UserRepository
import com.yustar.core.session.SessionManager

/**
 * Created by Yustar Pramudana on 06/03/26.
 */

open class LoginUserUseCase(
    private val repository: UserRepository,
    private val session: SessionManager
) {

    open suspend operator fun invoke(
        username: String,
        password: String
    ): LoginResult {
        val response = repository.login(LoginRequest(username, password))
        val data = response.data
        return if (response.status == Status.SUCCESS && data != null) {
            session.login(username)
            session.saveTokens(data.accessToken, data.refreshToken)
            LoginResult.Success
        } else {
            if (response.message?.contains("invalid", ignoreCase = true) == true) {
                LoginResult.InvalidPassword
            } else {
                LoginResult.UserNotFound
            }
        }
    }
}

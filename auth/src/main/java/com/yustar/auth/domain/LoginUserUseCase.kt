package com.yustar.auth.domain

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
        val user = repository.getUser(username) ?: return LoginResult.UserNotFound

        val success = repository.login(username, password)

        if (success) {
            session.login(username)
            return LoginResult.Success
        }

        return LoginResult.InvalidPassword
    }
}

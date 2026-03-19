package com.yustar.auth.domain

/**
 * Created by Yustar Pramudana on 06/03/26.
 */

sealed class LoginResult {
    object Success : LoginResult()
    object InvalidPassword : LoginResult()
    object UserNotFound : LoginResult()
}

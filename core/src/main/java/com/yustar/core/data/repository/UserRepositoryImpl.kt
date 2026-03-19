package com.yustar.core.data.repository

import com.yustar.core.data.local.User
import com.yustar.core.data.local.UserDao
import java.util.Objects.hash

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun register(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        address: String,
        phoneNumber: String
    ) {
        val user = User(
            username = username,
            password = hash(password).toString(),
            firstName = firstName,
            lastName = lastName,
            address = address,
            phoneNumber = phoneNumber
        )
        userDao.insertUser(user)
    }

    override suspend fun login(username: String, password: String): Boolean {
        val user = userDao.getUserByUsername(username) ?: return false
        return user.password == hash(password).toString()
    }

    override suspend fun getUser(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    override suspend fun updateUserProfile(
        username: String,
        firstName: String?,
        lastName: String?,
        address: String?,
        phoneNumber: String?
    ) {
        userDao.updateUserProfile(username, firstName, lastName, address, phoneNumber)
    }
}
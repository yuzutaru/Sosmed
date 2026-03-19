package com.yustar.core.data.repository

import com.yustar.core.data.local.User
import com.yustar.core.data.local.UserDao
import com.yustar.core.data.repository.UserRepositoryImpl
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.Objects

class UserRepositoryImplTest {private val userDao: UserDao = mockk()
    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setup() {
        repository = UserRepositoryImpl(userDao)
    }

    @Test
    fun `login returns true when credentials match`() = runTest {
        // Arrange
        val username = "testuser"
        val password = "password123"
        val hashedPassword = Objects.hash(password).toString()
        val mockUser = User(username, hashedPassword, "First", "Last", "Address", "123")
        
        coEvery { userDao.getUserByUsername(username) } returns mockUser

        // Act
        val result = repository.login(username, password)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `login returns false when user does not exist`() = runTest {
        // Arrange
        coEvery { userDao.getUserByUsername(any()) } returns null

        // Act
        val result = repository.login("wronguser", "password")

        // Assert
        assertFalse(result)
    }

    @Test
    fun `register calls insertUser with hashed password`() = runTest {
        // Arrange
        val password = "myPassword"
        val expectedHash = Objects.hash(password).toString()
        coEvery { userDao.insertUser(any()) } just Runs

        // Act
        repository.register("user", password, "F", "L", "A", "P")

        // Assert
        coVerify { 
            userDao.insertUser(match { it.password == expectedHash }) 
        }
    }
}
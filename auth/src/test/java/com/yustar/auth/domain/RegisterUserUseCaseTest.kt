package com.yustar.auth.domain

import com.yustar.core.data.local.User
import com.yustar.core.data.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class RegisterUserUseCaseTest {

    private val repository: UserRepository = mockk()
    private lateinit var registerUserUseCase: RegisterUserUseCase

    @Before
    fun setUp() {
        registerUserUseCase = RegisterUserUseCase(repository)
    }

    @Test
    fun `when invoke is called and user does not exist, it should call repository register`() = runTest {
        // Given
        val username = "testUser"
        val password = "password123"
        val firstName = "John"
        val lastName = "Doe"
        val address = "123 Main St"
        val phoneNumber = "555-1234"

        coEvery { repository.getUser(username) } returns null
        coEvery {
            repository.register(username, password, firstName, lastName, address, phoneNumber)
        } just runs

        // When
        registerUserUseCase(username, password, firstName, lastName, address, phoneNumber)

        // Then
        coVerify { repository.getUser(username) }
        coVerify {
            repository.register(username, password, firstName, lastName, address, phoneNumber)
        }
    }

    @Test
    fun `when invoke is called and user already exists, it should throw Exception`() = runTest {
        // Given
        val username = "testUser"
        val password = "password123"
        val existingUser = User(username, "hashed_password", "First", "Last", "Address", "12345")

        coEvery { repository.getUser(username) } returns existingUser

        // When
        val exception = assertThrows(Exception::class.java) {
            runBlocking {
                registerUserUseCase(username, password)
            }
        }

        // Then
        assertEquals("User with this username/email is already registered", exception.message)
        coVerify { repository.getUser(username) }
        coVerify(exactly = 0) { repository.register(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `when invoke is called with only username and password and user does not exist, it should call repository register with default empty strings`() = runTest {
        // Given
        val username = "testUser"
        val password = "password123"

        coEvery { repository.getUser(username) } returns null
        coEvery {
            repository.register(username, password, "", "", "", "")
        } just runs

        // When
        registerUserUseCase(username, password)

        // Then
        coVerify { repository.getUser(username) }
        coVerify {
            repository.register(username, password, "", "", "", "")
        }
    }
}
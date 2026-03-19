package com.yustar.core.repository

import com.yustar.core.data.remote.UsersApi
import com.yustar.core.data.remote.model.AuthRequest
import com.yustar.core.data.remote.model.AuthResponse
import com.yustar.core.data.remote.model.LoginRequest
import com.yustar.core.data.remote.model.LoginResponse
import com.yustar.core.data.remote.model.ProfileRequest
import com.yustar.core.data.remote.model.RefreshTokenRequest
import com.yustar.core.data.remote.model.RefreshTokenResponse
import com.yustar.core.data.remote.model.RegisterData
import com.yustar.core.data.remote.model.ResponseHandler
import com.yustar.core.data.remote.model.Status
import com.yustar.core.data.remote.model.UpdateProfileRequest
import com.yustar.core.data.repository.UserRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class UserRepositoryImplTest {

    private val api: UsersApi = mockk()
    private val responseHandler = ResponseHandler()
    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setup() {
        repository = UserRepositoryImpl(api, responseHandler)
    }

    @Test
    fun `authRegister success returns success resource`() = runTest {
        // Arrange
        val authRequest = AuthRequest("test@email.com", "password", RegisterData("username"))
        val authResponse = mockk<AuthResponse>()

        coEvery { api.authRegister(authRequest, any(), any()) } returns authResponse

        // Act
        val result = repository.authRegister(authRequest)

        // Assert
        assertEquals(Status.SUCCESS, result.status)
        assertEquals(authResponse, result.data)
    }

    @Test
    fun `authRegister exception returns error resource`() = runTest {
        // Arrange
        val authRequest = AuthRequest("test@email.com", "password", RegisterData("username"))
        coEvery { api.authRegister(authRequest, any(), any()) } throws Exception()

        // Act
        val result = repository.authRegister(authRequest)

        // Assert
        assertEquals(Status.ERROR, result.status)
        assertNotNull(result.message)
    }

    @Test
    fun `login success returns success resource`() = runTest {
        // Arrange
        val loginRequest = LoginRequest("test@email.com", "password")
        val loginResponse = mockk<LoginResponse>()

        coEvery { api.login(any(), loginRequest, any()) } returns loginResponse

        // Act
        val result = repository.login(loginRequest)

        // Assert
        assertEquals(Status.SUCCESS, result.status)
        assertEquals(loginResponse, result.data)
    }

    @Test
    fun `login exception returns error resource`() = runTest {
        // Arrange
        val loginRequest = LoginRequest("test@email.com", "password")
        coEvery { api.login(any(), loginRequest, any()) } throws Exception()

        // Act
        val result = repository.login(loginRequest)

        // Assert
        assertEquals(Status.ERROR, result.status)
        assertNotNull(result.message)
    }

    @Test
    fun `refreshToken success returns success resource`() = runTest {
        // Arrange
        val refreshTokenRequest = RefreshTokenRequest("token")
        val refreshTokenResponse = mockk<RefreshTokenResponse>()

        coEvery { api.refreshToken(any(), refreshTokenRequest, any()) } returns refreshTokenResponse

        // Act
        val result = repository.refreshToken(refreshTokenRequest)

        // Assert
        assertEquals(Status.SUCCESS, result.status)
        assertEquals(refreshTokenResponse, result.data)
    }

    @Test
    fun `refreshToken exception returns error resource`() = runTest {
        // Arrange
        val refreshTokenRequest = RefreshTokenRequest("token")
        coEvery { api.refreshToken(any(), refreshTokenRequest, any()) } throws Exception()

        // Act
        val result = repository.refreshToken(refreshTokenRequest)

        // Assert
        assertEquals(Status.ERROR, result.status)
        assertNotNull(result.message)
    }

    @Test
    fun `logout success with 204 returns success resource`() = runTest {
        // Arrange
        val token = "token"
        val response = Response.success(204, Unit)

        coEvery { api.logout(any(), any()) } returns response

        // Act
        val result = repository.logout(token)

        // Assert
        assertEquals(Status.SUCCESS, result.status)
    }

    @Test
    fun `logout failure with not 204 returns error resource`() = runTest {
        // Arrange
        val token = "token"
        val response = Response.success(200, Unit) // Success but not 204

        coEvery { api.logout(any(), any()) } returns response

        // Act
        val result = repository.logout(token)

        // Assert
        assertEquals(Status.ERROR, result.status)
        assertEquals("Failed to logout", result.message)
    }

    @Test
    fun `logout exception returns error resource`() = runTest {
        // Arrange
        val token = "token"
        coEvery { api.logout(any(), any()) } throws Exception()

        // Act
        val result = repository.logout(token)

        // Assert
        assertEquals(Status.ERROR, result.status)
        assertNotNull(result.message)
    }

    @Test
    fun `profileSignUp success with 201 returns success resource`() = runTest {
        // Arrange
        val profileRequest = ProfileRequest("id", "First", "Last", "Address", "123")
        val response = Response.success(201, Unit)

        coEvery { api.profileSignUp(profileRequest, any(), any(), any()) } returns response

        // Act
        val result = repository.profileSignUp(profileRequest)

        // Assert
        assertEquals(Status.SUCCESS, result.status)
    }

    @Test
    fun `profileSignUp failure with not 201 returns error resource`() = runTest {
        // Arrange
        val profileRequest = ProfileRequest("id", "First", "Last", "Address", "123")
        val response = Response.success(200, Unit) // Success but not 201

        coEvery { api.profileSignUp(profileRequest, any(), any(), any()) } returns response

        // Act
        val result = repository.profileSignUp(profileRequest)

        // Assert
        assertEquals(Status.ERROR, result.status)
        assertEquals("Failed to create profile", result.message)
    }

    @Test
    fun `profileSignUp exception returns error resource`() = runTest {
        // Arrange
        val profileRequest = ProfileRequest("id", "First", "Last", "Address", "123")
        coEvery { api.profileSignUp(profileRequest, any(), any(), any()) } throws Exception()

        // Act
        val result = repository.profileSignUp(profileRequest)

        // Assert
        assertEquals(Status.ERROR, result.status)
        assertNotNull(result.message)
    }

    @Test
    fun `updateProfile success with 204 returns success resource`() = runTest {
        // Arrange
        val id = "user123"
        val updateProfileRequest = UpdateProfileRequest("First", "Last", "Address", "123")
        val response = Response.success(204, Unit)

        coEvery { api.updateProfile("eq.$id", updateProfileRequest, any(), any(), any()) } returns response

        // Act
        val result = repository.updateProfile(id, updateProfileRequest)

        // Assert
        assertEquals(Status.SUCCESS, result.status)
    }

    @Test
    fun `updateProfile failure with not 204 returns error resource`() = runTest {
        // Arrange
        val id = "user123"
        val updateProfileRequest = UpdateProfileRequest("First", "Last", "Address", "123")
        val response = Response.success(200, Unit) // Success but not 204

        coEvery { api.updateProfile("eq.$id", updateProfileRequest, any(), any(), any()) } returns response

        // Act
        val result = repository.updateProfile(id, updateProfileRequest)

        // Assert
        assertEquals(Status.ERROR, result.status)
        assertEquals("Failed to update profile", result.message)
    }

    @Test
    fun `updateProfile exception returns error resource`() = runTest {
        // Arrange
        val id = "user123"
        val updateProfileRequest = UpdateProfileRequest("First", "Last", "Address", "123")
        coEvery { api.updateProfile("eq.$id", updateProfileRequest, any(), any(), any()) } throws Exception()

        // Act
        val result = repository.updateProfile(id, updateProfileRequest)

        // Assert
        assertEquals(Status.ERROR, result.status)
        assertNotNull(result.message)
    }
}

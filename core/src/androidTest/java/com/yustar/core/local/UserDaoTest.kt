package com.yustar.core.local

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yustar.core.data.local.User
import com.yustar.core.data.local.UserDB
import com.yustar.core.data.local.UserDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var db: UserDB
    private lateinit var userDao: UserDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Use an in-memory database for testing
        db = Room.inMemoryDatabaseBuilder(context, UserDB::class.java)
            .allowMainThreadQueries() // Only for tests
            .build()
        userDao = db.userDao()
    }

    @After
    fun closeDb() {
        if (::db.isInitialized) {
            db.close()
        }
    }

    @Test
    fun insertAndGetUser_returnsCorrectData() = runBlocking {
        val user = User(
            username = "ash_ketchum",
            password = "password123",
            firstName = "Ash",
            lastName = "Ketchum",
            address = "Pallet Town",
            phoneNumber = "1234567890"
        )
        userDao.insertUser(user)

        val retrievedUser = userDao.getUserByUsername("ash_ketchum")

        Assert.assertEquals("Username should match", user.username, retrievedUser?.username)
        Assert.assertEquals("Password should match", user.password, retrievedUser?.password)
        Assert.assertEquals("FirstName should match", user.firstName, retrievedUser?.firstName)
        Assert.assertEquals("LastName should match", user.lastName, retrievedUser?.lastName)
        Assert.assertEquals("Address should match", user.address, retrievedUser?.address)
        Assert.assertEquals("PhoneNumber should match", user.phoneNumber, retrievedUser?.phoneNumber)
    }

    @Test
    fun getUserByUsername_returnsNullIfUserDoesNotExist() = runBlocking {
        val retrievedUser = userDao.getUserByUsername("non_existent")
        Assert.assertNull(retrievedUser)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insertDuplicateUsername_throwsException() = runBlocking {
        val user1 = User(
            username = "ash_ketchum",
            password = "password123",
            firstName = "Ash",
            lastName = "Ketchum",
            address = "Pallet Town",
            phoneNumber = "1234567890"
        )
        val user2 = User(
            username = "ash_ketchum",
            password = "different_password",
            firstName = "Ash",
            lastName = "Ketchum",
            address = "Pallet Town",
            phoneNumber = "1234567890"
        )

        userDao.insertUser(user1)

        // This should throw SQLiteConstraintException because 'username' is the PrimaryKey
        // and the default Room @Insert conflict strategy is ABORT (unless specified otherwise)
        userDao.insertUser(user2)
    }

    @Test
    fun updateUser_updatesCorrectData() = runBlocking {
        val user = User(
            username = "ash_ketchum",
            password = "password123",
            firstName = "Ash",
            lastName = "Ketchum",
            address = "Pallet Town",
            phoneNumber = "1234567890"
        )
        userDao.insertUser(user)

        val updatedUser = user.copy(password = "newpassword123", address = "Cerulean City")
        userDao.updateUser(updatedUser)

        val retrievedUser = userDao.getUserByUsername("ash_ketchum")
        Assert.assertEquals("Password should be updated", "newpassword123", retrievedUser?.password)
        Assert.assertEquals("Address should be updated", "Cerulean City", retrievedUser?.address)
    }

    @Test
    fun updateUserProfile_updatesOnlyProvidedFields() = runBlocking {
        val user = User(
            username = "ash_ketchum",
            password = "password123",
            firstName = "Ash",
            lastName = "Ketchum",
            address = "Pallet Town",
            phoneNumber = "1234567890"
        )
        userDao.insertUser(user)

        userDao.updateUserProfile(
            username = "ash_ketchum",
            firstName = "Red",
            address = "Kanto"
        )

        val retrievedUser = userDao.getUserByUsername("ash_ketchum")
        Assert.assertEquals("FirstName should be updated", "Red", retrievedUser?.firstName)
        Assert.assertEquals("Address should be updated", "Kanto", retrievedUser?.address)
        Assert.assertEquals("LastName should remain unchanged", "Ketchum", retrievedUser?.lastName)
        Assert.assertEquals("PhoneNumber should remain unchanged", "1234567890", retrievedUser?.phoneNumber)
        Assert.assertEquals("Password should remain unchanged", "password123", retrievedUser?.password)
    }
}

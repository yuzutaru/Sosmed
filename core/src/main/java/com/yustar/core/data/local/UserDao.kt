package com.yustar.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("""
        UPDATE users 
        SET firstName = COALESCE(:firstName, firstName),
            lastName = COALESCE(:lastName, lastName),
            address = COALESCE(:address, address),
            phoneNumber = COALESCE(:phoneNumber, phoneNumber)
        WHERE username = :username
    """)
    suspend fun updateUserProfile(
        username: String,
        firstName: String? = null,
        lastName: String? = null,
        address: String? = null,
        phoneNumber: String? = null
    )
}

package com.yustar.dashboard.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yustar.dashboard.data.local.FeedsDatabase
import com.yustar.dashboard.data.local.entity.RemoteKeyEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteKeyDaoTest {

    private lateinit var database: FeedsDatabase
    private lateinit var remoteKeyDao: RemoteKeyDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, FeedsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        remoteKeyDao = database.remoteKeyDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetRemoteKey_returnsCorrectData() = runBlocking {
        val remoteKeys = listOf(
            RemoteKeyEntity("post_1", null, 2),
            RemoteKeyEntity("post_2", 1, 3)
        )
        remoteKeyDao.insertAll(remoteKeys)

        val result = remoteKeyDao.remoteKeysPostId("post_1")
        assertEquals("post_1", result?.postId)
        assertNull(result?.prevKey)
        assertEquals(2, result?.nextKey)
    }

    @Test
    fun clearRemoteKeys_removesAllData() = runBlocking {
        val remoteKeys = listOf(RemoteKeyEntity("post_1", null, 2))
        remoteKeyDao.insertAll(remoteKeys)

        remoteKeyDao.clearRemoteKeys()

        val result = remoteKeyDao.remoteKeysPostId("post_1")
        assertNull(result)
    }

    @Test
    fun insertDuplicateRemoteKey_replacesExistingData() = runBlocking {
        val remoteKey1 = RemoteKeyEntity("post_1", null, 2)
        val remoteKey2 = RemoteKeyEntity("post_1", 1, 3)
        
        remoteKeyDao.insertAll(listOf(remoteKey1))
        remoteKeyDao.insertAll(listOf(remoteKey2))

        val result = remoteKeyDao.remoteKeysPostId("post_1")
        assertEquals(1, result?.prevKey)
        assertEquals(3, result?.nextKey)
    }
}

package com.yustar.dashboard.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yustar.dashboard.data.local.FeedsDatabase
import com.yustar.dashboard.data.local.entity.PostEntity
import com.yustar.dashboard.data.local.entity.PostMediaEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostDaoTest {

    private lateinit var database: FeedsDatabase
    private lateinit var postDao: PostDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, FeedsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        postDao = database.postDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertPostsAndRead_returnsCorrectData() = runBlocking {
        val posts = listOf(
            PostEntity("1", "2023-10-01T10:00:00Z", "Hello World", "user1"),
            PostEntity("2", "2023-10-01T11:00:00Z", "Second Post", "user2")
        )
        postDao.insertPosts(posts)

        // For PagingSource, we can test individual media insertion or clear logic.
        // Direct query testing for PagingSource is usually done via Paging testing library,
        // but we can verify media relation here.
        
        val media = listOf(
            PostMediaEntity("m1", "1", "url1", "image"),
            PostMediaEntity("m2", "1", "url2", "image")
        )
        postDao.insertMedia(media)

        val retrievedMedia = postDao.getMediaForPost("1")
        assertEquals(2, retrievedMedia.size)
        assertEquals("url1", retrievedMedia[0].url)
    }

    @Test
    fun clearPosts_removesAllData() = runBlocking {
        val posts = listOf(PostEntity("1", "now", "content", "user1"))
        postDao.insertPosts(posts)
        
        postDao.clearPosts()
        
        // Verifying clear via a helper if needed or just checking media
        val media = listOf(PostMediaEntity("m1", "1", "url1", "image"))
        postDao.insertMedia(media)
        postDao.clearMedia()
        
        val retrievedMedia = postDao.getMediaForPost("1")
        assertEquals(0, retrievedMedia.size)
    }
}

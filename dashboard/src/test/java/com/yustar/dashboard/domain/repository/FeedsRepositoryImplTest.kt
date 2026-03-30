package com.yustar.dashboard.domain.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.yustar.core.data.remote.UsersApi
import com.yustar.core.session.SessionManager
import com.yustar.dashboard.data.local.FeedsDatabase
import com.yustar.dashboard.data.local.dao.PostDao
import com.yustar.dashboard.data.remote.FeedsApi
import com.yustar.dashboard.data.remote.SupabaseClientWrapper
import com.yustar.dashboard.data.remote.model.MediaDto
import com.yustar.dashboard.data.repository.FeedsRepositoryImpl
import com.yustar.dashboard.domain.model.MediaType
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class FeedsRepositoryImplTest {

    private lateinit var context: Context
    private lateinit var api: FeedsApi
    private lateinit var usersApi: UsersApi
    private lateinit var database: FeedsDatabase
    private lateinit var sessionManager: SessionManager
    private lateinit var supabaseWrapper: SupabaseClientWrapper
    private lateinit var repository: FeedsRepositoryImpl

    @Before
    fun setUp() {
        context = mockk()
        api = mockk()
        usersApi = mockk()
        database = mockk()
        sessionManager = mockk(relaxed = true)
        supabaseWrapper = mockk()

        repository = FeedsRepositoryImpl(
            context = context,
            api = api,
            usersApi = usersApi,
            database = database,
            sessionManager = sessionManager,
            supabaseWrapper = supabaseWrapper
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getFeedsPaged returns paging data flow`() = runTest {
        // Given
        val postDao = mockk<PostDao>()
        every { database.postDao() } returns postDao
        every { postDao.getPostsPaged() } returns mockk(relaxed = true)

        // When
        val result = repository.getFeedsPaged().first()

        // Then
        assertNotNull(result)
        verify { postDao.getPostsPaged() }
    }

    @Test
    fun `createPost success calls rpc`() = runTest {
        // Given
        val caption = "caption"
        val location = "location"
        val media = listOf(MediaDto("url", "image", 0))

        coEvery { 
            supabaseWrapper.rpc(
                functionName = "create_post_with_media", 
                parameters = any()
            ) 
        } returns Unit

        // When
        repository.createPost(caption, location, media)

        // Then
        coVerify { 
            supabaseWrapper.rpc(
                functionName = "create_post_with_media", 
                parameters = any()
            ) 
        }
    }

    @Test
    fun `getSignedUploadUrl returns path and token`() = runTest {
        // Given
        val jsonResponse = """{"path":"test/path","token":"test_token"}"""
        coEvery { supabaseWrapper.invokeFunction("create-signed-upload") } returns jsonResponse

        // When
        val (path, token) = repository.getSignedUploadUrl()

        // Then
        assertEquals("test/path", path)
        assertEquals("test_token", token)
    }

    @Test
    fun `uploadFile calls uploadToSignedUrl`() = runTest {
        // Given
        val path = "path"
        val token = "token"
        val bytes = byteArrayOf(1, 2, 3)
        coEvery { 
            supabaseWrapper.uploadToSignedUrl("post-media", path, token, bytes) 
        } returns Unit

        // When
        repository.uploadFile(path, token, bytes)

        // Then
        coVerify { 
            supabaseWrapper.uploadToSignedUrl("post-media", path, token, bytes) 
        }
    }

    @Test
    fun `getPublicUrl returns url`() = runTest {
        // Given
        val path = "path"
        val expectedUrl = "https://public.url/path"
        every { supabaseWrapper.getPublicUrl("post-media", path) } returns expectedUrl

        // When
        val result = repository.getPublicUrl(path)

        // Then
        assertEquals(expectedUrl, result)
    }

    @Test
    fun `getLocalImages returns list of images from media store`() = runTest {
        // Given
        val contentResolver = mockk<ContentResolver>()
        val cursor = mockk<Cursor>()
        val uri = mockk<Uri>()
        val externalUri = mockk<Uri>()
        
        mockkStatic(MediaStore.Files::class)
        every { MediaStore.Files.getContentUri("external") } returns externalUri

        every { context.contentResolver } returns contentResolver
        every { 
            contentResolver.query(any(), any(), any(), any(), any()) 
        } returns cursor
        
        every { cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID) } returns 0
        every { cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME) } returns 1
        every { cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED) } returns 2
        every { cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE) } returns 3
        every { cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE) } returns 4
        every { cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION) } returns 5
        
        every { cursor.moveToNext() } returnsMany listOf(true, false)
        every { cursor.getLong(0) } returns 1L
        every { cursor.getString(1) } returns "image.jpg"
        every { cursor.getLong(2) } returns 123456L
        every { cursor.getInt(3) } returns MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
        every { cursor.getString(4) } returns "image/jpeg"
        every { cursor.getLong(5) } returns 0L
        every { cursor.close() } just Runs
        
        mockkStatic(ContentUris::class)
        every { ContentUris.withAppendedId(any(), any()) } returns uri

        // When
        val result = repository.getLocalImages(type = MediaType.PHOTOS).first()

        // Then
        assertEquals(1, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("image.jpg", result[0].name)
        assertEquals(123456L, result[0].dateAdded)
        assertEquals(uri, result[0].uri)
    }

    @Test
    fun `getLocalImages returns list of videos from media store`() = runTest {
        // Given
        val contentResolver = mockk<ContentResolver>()
        val cursor = mockk<Cursor>()
        val uri = mockk<Uri>()
        val externalUri = mockk<Uri>()
        
        mockkStatic(MediaStore.Files::class)
        every { MediaStore.Files.getContentUri("external") } returns externalUri

        every { context.contentResolver } returns contentResolver
        every { 
            contentResolver.query(any(), any(), any(), any(), any()) 
        } returns cursor
        
        every { cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID) } returns 0
        every { cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME) } returns 1
        every { cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED) } returns 2
        every { cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE) } returns 3
        every { cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE) } returns 4
        every { cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION) } returns 5
        
        every { cursor.moveToNext() } returnsMany listOf(true, false)
        every { cursor.getLong(0) } returns 1L
        every { cursor.getString(1) } returns "video.mp4"
        every { cursor.getLong(2) } returns 123456L
        every { cursor.getInt(3) } returns MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
        every { cursor.getString(4) } returns "video/mp4"
        every { cursor.getLong(5) } returns 65000L // 1:05
        every { cursor.close() } just Runs
        
        mockkStatic(ContentUris::class)
        every { ContentUris.withAppendedId(any(), any()) } returns uri

        // When
        val result = repository.getLocalImages(type = MediaType.VIDEOS).first()

        // Then
        assertEquals(1, result.size)
        assertEquals(true, result[0].isVideo)
        assertEquals("1:05", result[0].duration)
    }

    @Test
    fun `getLocalImages with bucketId calls query with selection`() = runTest {
        // Given
        val contentResolver = mockk<ContentResolver>()
        val cursor = mockk<Cursor>()
        val bucketId = "bucket123"
        val externalUri = mockk<Uri>()

        mockkStatic(MediaStore.Files::class)
        every { MediaStore.Files.getContentUri("external") } returns externalUri
        
        every { context.contentResolver } returns contentResolver
        every { 
            contentResolver.query(
                any(), any(), 
                any(), 
                any(), 
                any()
            ) 
        } returns cursor
        
        every { cursor.getColumnIndexOrThrow(any()) } returns 0
        every { cursor.moveToNext() } returns false
        every { cursor.close() } just Runs

        // When
        repository.getLocalImages(bucketId, MediaType.PHOTOS).first()

        // Then
        verify { 
            contentResolver.query(
                externalUri,
                any(),
                "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ? AND ${MediaStore.Images.Media.BUCKET_ID} = ?",
                arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), bucketId),
                any()
            ) 
        }
    }

    @Test
    fun `getLocalAlbums returns list of albums from media store`() = runTest {
        // Given
        val contentResolver = mockk<ContentResolver>()
        val cursor = mockk<Cursor>()
        val uri = mockk<Uri>()
        
        every { context.contentResolver } returns contentResolver
        
        every { 
            contentResolver.query(any(), any(), any(), any(), any())
        } returns cursor
        
        every { cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID) } returns 0
        every { cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME) } returns 1
        every { cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID) } returns 2
        
        every { cursor.moveToNext() } returnsMany listOf(true, true, true, false)
        every { cursor.getString(0) } returnsMany listOf("bucket1", "bucket1", "bucket2")
        every { cursor.getString(1) } returnsMany listOf("Album 1", "Album 1", "Album 2")
        every { cursor.getLong(2) } returnsMany listOf(1L, 2L, 3L)
        every { cursor.close() } just Runs
        
        mockkStatic(ContentUris::class)
        every { ContentUris.withAppendedId(any(), any()) } returns uri
        every { uri.toString() } returns "uri_string"

        // When
        val result = repository.getLocalAlbums().first()

        // Then
        assertEquals(2, result.size)
        // Sorted by count descending: Album 1 has 2, Album 2 has 1
        assertEquals("bucket1", result[0].id)
        assertEquals("Album 1", result[0].name)
        assertEquals("2", result[0].count)
        assertEquals("uri_string", result[0].thumbnailUri)
        
        assertEquals("bucket2", result[1].id)
        assertEquals("Album 2", result[1].name)
        assertEquals("1", result[1].count)
    }
}
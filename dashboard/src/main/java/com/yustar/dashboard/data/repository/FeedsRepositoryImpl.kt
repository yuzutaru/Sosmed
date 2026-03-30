package com.yustar.dashboard.data.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.yustar.core.data.remote.UsersApi
import com.yustar.core.session.SessionManager
import com.yustar.dashboard.data.local.FeedsDatabase
import com.yustar.dashboard.data.remote.FeedsApi
import com.yustar.dashboard.data.remote.SupabaseClientWrapper
import com.yustar.dashboard.data.remote.model.MediaDto
import com.yustar.dashboard.domain.model.AlbumItem
import com.yustar.dashboard.domain.model.LocalMedia
import com.yustar.dashboard.domain.model.MediaType
import com.yustar.dashboard.domain.model.Post
import com.yustar.dashboard.domain.model.PostMedia
import com.yustar.dashboard.domain.model.PostProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class FeedsRepositoryImpl(
    private val context: Context,
    private val api: FeedsApi,
    private val usersApi: UsersApi,
    private val database: FeedsDatabase,
    private val sessionManager: SessionManager,
    private val supabaseWrapper: SupabaseClientWrapper
) : FeedsRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getFeedsPaged(): Flow<PagingData<Post>> {
        val pagingSourceFactory = { database.postDao().getPostsPaged() }

        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            remoteMediator = FeedsRemoteMediator(
                api = api,
                usersApi = usersApi,
                database = database,
                sessionManager = sessionManager
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { postWithMedia ->
                Post(
                    id = postWithMedia.post.id,
                    createdAt = postWithMedia.post.createdAt,
                    content = postWithMedia.post.content,
                    userId = postWithMedia.post.userId,
                    postMedia = postWithMedia.media.map {
                        PostMedia(
                            id = it.id,
                            postId = it.postId,
                            url = it.url,
                            mediaType = it.mediaType
                        )
                    },
                    postProfile = postWithMedia.profile?.let {
                        PostProfile(
                            firstName = it.firstName,
                            lastName = it.lastName
                        )
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    override fun getLocalImages(
        bucketId: String?,
        type: MediaType
    ): Flow<List<LocalMedia>> = flow {
        val mediaList = mutableListOf<LocalMedia>()

        // Use MediaStore.Files to support both Images and Videos
        val contentUri = MediaStore.Files.getContentUri("external")

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Video.VideoColumns.DURATION
        )

        // Build Selection String
        val selectionQueries = mutableListOf<String>()
        val selectionArgs = mutableListOf<String>()

        // 1. Filter by Media Type
        when (type) {
            MediaType.PHOTOS -> {
                selectionQueries.add("${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?")
                selectionArgs.add(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())
            }
            MediaType.VIDEOS -> {
                selectionQueries.add("${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?")
                selectionArgs.add(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
            }
            MediaType.RECENTS -> {
                selectionQueries.add("(${MediaStore.Files.FileColumns.MEDIA_TYPE} = ? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?)")
                selectionArgs.add(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())
                selectionArgs.add(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
            }
        }

        // 2. Filter by Bucket (Album) if provided
        if (bucketId != null) {
            selectionQueries.add("${MediaStore.Images.Media.BUCKET_ID} = ?")
            selectionArgs.add(bucketId)
        }

        val selection = selectionQueries.joinToString(" AND ")
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        context.contentResolver.query(
            contentUri,
            projection,
            selection,
            selectionArgs.toTypedArray(),
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
            val mediaTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val dateAdded = cursor.getLong(dateAddedColumn)
                val mediaType = cursor.getInt(mediaTypeColumn)
                val durationMs = cursor.getLong(durationColumn)

                val isVideo = mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO

                // Determine correct URI based on type
                val uri = if (isVideo) {
                    ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                } else {
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                }

                val duration = if (isVideo) {
                    val minutes = (durationMs / 1000) / 60
                    val seconds = (durationMs / 1000) % 60
                    String.format("%d:%02d", minutes, seconds)
                } else null

                mediaList.add(LocalMedia(id, uri, name, dateAdded, isVideo, duration))
            }
        }
        emit(mediaList)
    }

    override fun getLocalAlbums(): Flow<List<AlbumItem>> = flow {
        val albums = mutableListOf<AlbumItem>()
        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media._ID
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val albumMap = mutableMapOf<String, AlbumData>()

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val bucketId = cursor.getString(bucketIdColumn)
                val bucketName = cursor.getString(bucketNameColumn)
                val id = cursor.getLong(idColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id).toString()

                val data = albumMap[bucketId]
                if (data == null) {
                    albumMap[bucketId] = AlbumData(bucketName, 1, uri)
                } else {
                    albumMap[bucketId] = data.copy(count = data.count + 1)
                }
            }
        }

        albumMap.forEach { (id, data) ->
            albums.add(AlbumItem(id, data.name, data.count.toString(), data.thumbnailUri))
        }

        // Add a "Recents" album at the beginning if not empty
        if (albums.isNotEmpty()) {
             // You might want to calculate total count for Recents or just leave it as is.
        }

        emit(albums.sortedByDescending { it.count.toInt() })
    }

    override suspend fun getSignedUploadUrl(): Pair<String, String> {
        val response = supabaseWrapper.invokeFunction("create-signed-upload")

        val json = Json.parseToJsonElement(response).jsonObject
        val path = json["path"]!!.jsonPrimitive.content
        val token = json["token"]!!.jsonPrimitive.content

        return path to token
    }

    override suspend fun uploadFile(
        path: String,
        token: String,
        bytes: ByteArray
    ) {
        supabaseWrapper.uploadToSignedUrl(
            bucket = "post-media",
            path = path,
            token = token,
            data = bytes
        )
    }

    override fun getPublicUrl(path: String): String {
        return supabaseWrapper.getPublicUrl("post-media", path)
    }

    override suspend fun createPost(
        caption: String,
        location: String,
        media: List<MediaDto>
    ) {
        supabaseWrapper.rpc(
            "create_post_with_media",
            buildJsonObject {
                put("p_caption", caption)
                put("p_location", location)
                put("p_media", Json.encodeToJsonElement(ListSerializer(MediaDto.serializer()), media))
            }
        )
    }

    private data class AlbumData(val name: String, val count: Int, val thumbnailUri: String)
}
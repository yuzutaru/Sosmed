package com.yustar.dashboard.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.yustar.dashboard.data.local.entity.PostEntity
import com.yustar.dashboard.data.local.entity.PostMediaEntity
import com.yustar.dashboard.data.local.entity.PostProfileEntity
import com.yustar.dashboard.data.local.model.PostWithMedia

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: List<PostMediaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: PostProfileEntity)

    @Transaction
    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun getPostsPaged(): PagingSource<Int, PostWithMedia>

    @Query("SELECT * FROM post_media WHERE postId = :postId")
    suspend fun getMediaForPost(postId: String): List<PostMediaEntity>

    @Query("SELECT * FROM post_profiles WHERE postId = :postId")
    suspend fun getProfileForPost(postId: String): PostProfileEntity

    @Query("DELETE FROM posts")
    suspend fun clearPosts()

    @Query("DELETE FROM post_media")
    suspend fun clearMedia()

    @Query("DELETE FROM post_profiles")
    suspend fun clearProfile()
}

package com.yustar.dashboard.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yustar.dashboard.data.local.dao.PostDao
import com.yustar.dashboard.data.local.dao.RemoteKeyDao
import com.yustar.dashboard.data.local.entity.PostEntity
import com.yustar.dashboard.data.local.entity.PostMediaEntity
import com.yustar.dashboard.data.local.entity.PostProfileEntity
import com.yustar.dashboard.data.local.entity.RemoteKeyEntity

@Database(
    entities = [PostEntity::class, PostMediaEntity::class, PostProfileEntity::class, RemoteKeyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FeedsDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}

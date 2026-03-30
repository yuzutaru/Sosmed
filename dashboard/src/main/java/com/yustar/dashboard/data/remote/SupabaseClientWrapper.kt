package com.yustar.dashboard.data.remote

import com.yustar.dashboard.data.remote.model.MediaDto

interface SupabaseClientWrapper {
    suspend fun invokeFunction(functionName: String): String
    suspend fun uploadToSignedUrl(bucket: String, path: String, token: String, data: ByteArray)
    fun getPublicUrl(bucket: String, path: String): String
    suspend fun rpc(functionName: String, parameters: Any)
}
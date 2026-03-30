package com.yustar.dashboard.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.storage.storage
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.JsonObject

class SupabaseClientWrapperImpl(
    private val supabase: SupabaseClient
) : SupabaseClientWrapper {

    override suspend fun invokeFunction(functionName: String): String {
        return supabase.functions.invoke(functionName).bodyAsText()
    }

    override suspend fun uploadToSignedUrl(
        bucket: String,
        path: String,
        token: String,
        data: ByteArray
    ) {
        supabase.storage.from(bucket).uploadToSignedUrl(path, token, data)
    }

    override fun getPublicUrl(bucket: String, path: String): String {
        return supabase.storage.from(bucket).publicUrl(path)
    }

    override suspend fun rpc(functionName: String, parameters: JsonObject) {
        supabase.postgrest.rpc(functionName, parameters)
    }
}
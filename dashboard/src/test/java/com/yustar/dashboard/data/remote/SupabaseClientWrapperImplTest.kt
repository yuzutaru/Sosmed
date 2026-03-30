package com.yustar.dashboard.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.resumable.MemoryResumableCache
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.Assert.assertEquals
import org.junit.Test

class SupabaseClientWrapperImplTest {

    private lateinit var mockEngine: MockEngine
    private lateinit var supabase: SupabaseClient
    private lateinit var wrapper: SupabaseClientWrapperImpl

    private fun setupSupabase(handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData) {
        mockEngine = MockEngine(handler)
        supabase = createSupabaseClient("https://test.supabase.co", "test-key") {
            httpEngine = mockEngine
            install(Functions)
            install(Storage) {
                resumable {
                    cache = MemoryResumableCache()
                }
            }
            install(Postgrest)
        }
        wrapper = SupabaseClientWrapperImpl(supabase)
    }

    @Test
    fun `invokeFunction returns response body as text`() = runTest {
        val expectedResponse = "{\"status\":\"success\"}"
        
        setupSupabase { request ->
            if (request.url.encodedPath.endsWith("/functions/v1/test-function")) {
                respond(
                    content = expectedResponse,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            } else {
                respond("", status = HttpStatusCode.NotFound)
            }
        }

        val result = wrapper.invokeFunction("test-function")

        assertEquals(expectedResponse, result)
    }

    @Test
    fun `uploadToSignedUrl calls storage plugin with correct parameters`() = runTest {
        var capturedPath = ""
        setupSupabase { request ->
            capturedPath = request.url.encodedPath
            // The storage SDK version 3.0.1 expects "id" and "key" in the response.
            // Some versions might expect "Id" and "Key". We provide both to be safe.
            val responseBody = buildJsonObject {
                put("id", "test-id")
                put("Id", "test-id")
                put("key", "test-bucket/test/path")
                put("Key", "test-bucket/test/path")
            }.toString()
            
            respond(
                content = responseBody,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val bucket = "test-bucket"
        val path = "test/path"
        val token = "test-token"
        val data = byteArrayOf(1, 2, 3)

        wrapper.uploadToSignedUrl(bucket, path, token, data)

        // Verify the URL contains the bucket and path
        assert(capturedPath.contains(bucket))
        assert(capturedPath.contains(path))
    }

    @Test
    fun `getPublicUrl returns url from storage plugin`() = runTest {
        // No network call for getPublicUrl, but we still need a client
        setupSupabase { respond("") }
        
        val bucket = "test-bucket"
        val path = "test/path"
        val expectedUrl = "https://test.supabase.co/storage/v1/object/public/test-bucket/test/path"

        val result = wrapper.getPublicUrl(bucket, path)

        assertEquals(expectedUrl, result)
    }

    @Test
    fun `rpc calls postgrest plugin`() = runTest {
        var capturedUrl = ""
        setupSupabase { request ->
            capturedUrl = request.url.toString()
            respond(
                content = "[]",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        // Use buildJsonObject to avoid serialization issues with Any
        wrapper.rpc("test_rpc", buildJsonObject { })

        assert(capturedUrl.contains("/rest/v1/rpc/test_rpc"))
    }
}

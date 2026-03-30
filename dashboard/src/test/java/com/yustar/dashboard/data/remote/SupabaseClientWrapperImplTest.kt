package com.yustar.dashboard.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
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
            install(Storage)
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
            respond("", status = HttpStatusCode.OK)
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
            respond("[]", status = HttpStatusCode.OK)
        }

        wrapper.rpc("test_rpc", mapOf("param" to "value"))

        assert(capturedUrl.contains("/rest/v1/rpc/test_rpc"))
    }
}

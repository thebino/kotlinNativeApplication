package native

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals


class SampleTests {
    @Test
    fun testFetchData() {
        val client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = KotlinxSerializer().apply {
                    register<Response>(Response.serializer())
                }
            }

            engine {
                val jsonResponse = "{\"data\": \"foo\"}"
                val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))

                addHandler { request ->
                    when (request.url.fullUrl) {
                        "http://localhost/" -> {
                            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                            respond(jsonResponse, headers = responseHeaders)
                        }
                        else -> {
                            error("Unhandled ${request.url.fullPath}")
                        }
                    }
                }
            }
        }

        runBlocking {
            val response = client.get<Response>("http://localhost/")

            assertEquals(Response("foo"), response, "response does not match!")
        }
    }
}

private val Url.hostWithPortIfRequired: String get() = if (port == protocol.defaultPort) host else hostWithPort
private val Url.fullUrl: String get() = "${protocol.name}://$hostWithPortIfRequired$fullPath"

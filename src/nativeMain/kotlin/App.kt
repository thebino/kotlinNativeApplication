package native

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


internal val ApplicationDispatcher: CoroutineDispatcher = Dispatchers.Default

@ImplicitReflectionSerializer
fun main() {
    runBlocking {
        val result = suspendCoroutine<String> { continuation ->
            fetchData({
                continuation.resume(it.data)
            }, {
                continuation.resume("")
            })
        }

        println(result)
    }
}

@ImplicitReflectionSerializer
fun fetchData(success: (Response) -> Unit, error: (Exception) -> Unit) {
    val httpClient = HttpClient()

    GlobalScope.apply {
        launch(ApplicationDispatcher) {
            try {
                val jsonData = httpClient.get<String> {
                    url {
                        protocol = URLProtocol.HTTPS
                        port = 443
                        host = "example.com"
                    }
                }

                val response = Json(JsonConfiguration.Stable.copy(strictMode = false)).parse<Response>(jsonData)
                success(response)
            } catch (exception: Exception) {
                error(exception)
            }
        }
    }
}

@Serializable
data class Response(
    @SerialName("data")
    val data: String
)

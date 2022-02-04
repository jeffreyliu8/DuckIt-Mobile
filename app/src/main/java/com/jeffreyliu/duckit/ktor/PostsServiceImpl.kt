package com.jeffreyliu.duckit.ktor

import com.jeffreyliu.duckit.model.DuckPostRequest
import com.jeffreyliu.duckit.model.DuckPosts
import com.orhanobut.logger.Logger
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*

class PostsServiceImpl(
    private val client: HttpClient
) : PostsService {

    override suspend fun getPosts(): DuckPosts? {
        return try {
            client.get { url(HttpRoutes.GET_POSTS) }
        } catch (e: RedirectResponseException) {
            // 3xx - responses
            Logger.e("Error: ${e.response.status.description}")
            null
        } catch (e: ClientRequestException) {
            // 4xx - responses
            Logger.e("Error: ${e.response.status.description}")
            null
        } catch (e: ServerResponseException) {
            // 5xx - responses
            Logger.e("Error: ${e.response.status.description}")
            null
        } catch (e: Exception) {
            Logger.e("Error: ${e.message}")
            null
        }
    }

    override suspend fun createPost(postRequest: DuckPostRequest): String? {
        return try {
            client.post<String?> {
                url(HttpRoutes.NEW_POST)
                contentType(ContentType.Application.Json)
                body = postRequest
            }
        } catch (e: RedirectResponseException) {
            // 3xx - responses
            Logger.e("Error: ${e.response.status.description}")
            null
        } catch (e: ClientRequestException) {
            // 4xx - responses
            Logger.e("Error: ${e.response.status.description}")
            null
        } catch (e: ServerResponseException) {
            // 5xx - responses
            Logger.e("Error: ${e.response.status.description}")
            null
        } catch (e: Exception) {
            Logger.e("Error: ${e.message}")
            null
        }
    }
}
package com.jeffreyliu.duckit.ktor

import com.jeffreyliu.duckit.model.*
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*

class PostsServiceImpl(
    private val client: HttpClient
) : PostsService {

    override suspend fun getPosts(): ResponseWrapper<DuckPosts> {
        return try {
            val response = client.get<DuckPosts> { url(HttpRoutes.GET_POSTS) }
            ResponseWrapper(response = response)
        } catch (e: RedirectResponseException) {
            // 3xx - responses
            println("Error: ${e.response.status.description}")
            ResponseWrapper(e = e)
        } catch (e: ClientRequestException) {
            // 4xx - responses
            println("Error: ${e.response.status.description}")
            ResponseWrapper(e = e)
        } catch (e: ServerResponseException) {
            // 5xx - responses
            println("Error: ${e.response.status.description}")
            ResponseWrapper(e = e)
        } catch (e: Exception) {
            println("Error: ${e.message}")
            ResponseWrapper(e = e)
        }
    }

    override suspend fun createPost(
        postRequest: DuckPostRequest,
        token: String
    ): ResponseWrapper<Nothing> {
        return try {
            client.post<String?> {
                url(HttpRoutes.NEW_POST)
                contentType(ContentType.Application.Json)
                body = postRequest
                header("Authorization", "Bearer $token")
            }
            ResponseWrapper()
        } catch (e: RedirectResponseException) {
            // 3xx - responses
            println("Error: ${e.response.status.description}")
            ResponseWrapper(e = e)
        } catch (e: ClientRequestException) {
            // 4xx - responses
            println("Error: ${e.response.status.description}")
            ResponseWrapper(e = e)
        } catch (e: ServerResponseException) {
            // 5xx - responses
            println("Error: ${e.response.status.description}")
            ResponseWrapper(e = e)
        } catch (e: Exception) {
            println("Error: ${e.message}")
            ResponseWrapper(e = e)
        }
    }

    override suspend fun signIn(request: SignInUpRequestBody): ResponseWrapper<SignInUpResponse> {
        return try {
            val result = client.post<SignInUpResponse> {
                url(HttpRoutes.SIGN_IN)
                contentType(ContentType.Application.Json)
                body = request
            }
            ResponseWrapper(result)
        } catch (e: Exception) {
            println("Error: ${e.message}")
            ResponseWrapper(e = e)
        }
    }

    override suspend fun signUp(request: SignInUpRequestBody): ResponseWrapper<SignInUpResponse> {
        return try {
            val result = client.post<SignInUpResponse> {
                url(HttpRoutes.SIGN_UP)
                contentType(ContentType.Application.Json)
                body = request
            }
            ResponseWrapper(result)
        } catch (e: Exception) {
            println("Error: ${e.message}")
            ResponseWrapper(e = e)
        }
    }

    override suspend fun upvote(id: String): ResponseWrapper<UpDownVoteResponse> {
        return try {
            val result = client.post<UpDownVoteResponse> {
                url("${HttpRoutes.UPVOTE_PREFIX}$id${HttpRoutes.UPVOTE_POSTFIX}")
                contentType(ContentType.Application.Json)
            }
            ResponseWrapper(result)
        } catch (e: Exception) {
            println("Error: ${e.message}")
            ResponseWrapper(e = e)
        }
    }

    override suspend fun downVote(id: String): ResponseWrapper<UpDownVoteResponse> {
        return try {
            val result = client.post<UpDownVoteResponse> {
                url("${HttpRoutes.DOWNVOTE_PREFIX}$id${HttpRoutes.DOWNVOTE_POSTFIX}")
                contentType(ContentType.Application.Json)
            }
            ResponseWrapper(result)
        } catch (e: Exception) {
            println("Error: ${e.message}")
            ResponseWrapper(e = e)
        }
    }
}
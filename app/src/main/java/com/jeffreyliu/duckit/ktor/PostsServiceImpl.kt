package com.jeffreyliu.duckit.ktor

import com.jeffreyliu.duckit.constant.PREF_KEY_TOKEN
import com.jeffreyliu.duckit.model.*
import com.pixplicity.easyprefs.library.Prefs
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
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ClientRequestException) {
            // 4xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ServerResponseException) {
            // 5xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: Exception) {
            println("Error: ${e.message}")
            null
        }
    }

    override suspend fun createPost(postRequest: DuckPostRequest): Boolean {
        return try {
            client.post<String?> {
                url(HttpRoutes.NEW_POST)
                contentType(ContentType.Application.Json)
                body = postRequest
                header("Authorization", "Bearer ${Prefs.getString(PREF_KEY_TOKEN)}")
            }
            true
        } catch (e: RedirectResponseException) {
            // 3xx - responses
            println("Error: ${e.response.status.description}")
            false
        } catch (e: ClientRequestException) {
            // 4xx - responses
            println("Error: ${e.response.status.description}")
            false
        } catch (e: ServerResponseException) {
            // 5xx - responses
            println("Error: ${e.response.status.description}")
            false
        } catch (e: Exception) {
            println("Error: ${e.message}")
            false
        }
    }

    override suspend fun signIn(request: SignInUpRequestBody): Pair<SignInUpResponse?, Exception?> {
        return try {
            val result = client.post<SignInUpResponse> {
                url(HttpRoutes.SIGN_IN)
                contentType(ContentType.Application.Json)
                body = request
            }
            Pair(result, null)
        } catch (e: Exception) {
            println("Error: ${e.message}")
            Pair(null, e)
        }
    }

    override suspend fun signUp(request: SignInUpRequestBody): Pair<SignInUpResponse?, Exception?> {
        return try {
            val result = client.post<SignInUpResponse> {
                url(HttpRoutes.SIGN_UP)
                contentType(ContentType.Application.Json)
                body = request
            }
            Pair(result, null)
        } catch (e: Exception) {
            println("Error: ${e.message}")
            Pair(null, e)
        }
    }

    override suspend fun upvote(id: String): UpDownVoteResponse? {
        return try {
            client.post<UpDownVoteResponse> {
                url("${HttpRoutes.UPVOTE_PREFIX}$id${HttpRoutes.UPVOTE_POSTFIX}")
                contentType(ContentType.Application.Json)
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
            null
        }
    }

    override suspend fun downVote(id: String): UpDownVoteResponse? {
        return try {
            client.post<UpDownVoteResponse> {
                url("${HttpRoutes.DOWNVOTE_PREFIX}$id${HttpRoutes.DOWNVOTE_POSTFIX}")
                contentType(ContentType.Application.Json)
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
            null
        }
    }
}
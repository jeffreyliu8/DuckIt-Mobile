package com.jeffreyliu.duckit.ktor

import com.jeffreyliu.duckit.model.*
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*

interface PostsService {

    suspend fun getPosts(): ResponseWrapper<DuckPosts>
    suspend fun createPost(postRequest: DuckPostRequest): ResponseWrapper<Nothing>
    suspend fun signIn(request: SignInUpRequestBody): ResponseWrapper<SignInUpResponse>
    suspend fun signUp(request: SignInUpRequestBody): ResponseWrapper<SignInUpResponse>
    suspend fun upvote(id: String): ResponseWrapper<UpDownVoteResponse>
    suspend fun downVote(id: String): ResponseWrapper<UpDownVoteResponse>

    companion object {
        fun create(): PostsService {
            return PostsServiceImpl(
                client = HttpClient(Android) {
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                    install(JsonFeature) {
                        serializer = KotlinxSerializer()
                    }
                }
            )
        }
    }
}
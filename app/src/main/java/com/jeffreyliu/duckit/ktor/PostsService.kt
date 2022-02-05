package com.jeffreyliu.duckit.ktor

import com.jeffreyliu.duckit.model.*
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*

interface PostsService {

    suspend fun getPosts(): DuckPosts?
    suspend fun createPost(postRequest: DuckPostRequest): String?
    suspend fun signIn(request: SignInUpRequestBody): Pair<SignInUpResponse?, Exception?>
    suspend fun signUp(request: SignInUpRequestBody): Pair<SignInUpResponse?, Exception?>
    suspend fun upvote(id: String): UpDownVoteResponse?
    suspend fun downVote(id: String): UpDownVoteResponse?

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
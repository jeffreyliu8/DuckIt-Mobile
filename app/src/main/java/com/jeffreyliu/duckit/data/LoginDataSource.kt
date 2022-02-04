package com.jeffreyliu.duckit.data

import com.jeffreyliu.duckit.ktor.PostsService
import com.jeffreyliu.duckit.model.LoggedInUser
import com.jeffreyliu.duckit.model.SignInUpRequestBody
import com.pixplicity.easyprefs.library.Prefs
import io.ktor.client.features.*
import java.io.IOException


/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    private val service = PostsService.create()

    suspend fun loginOrSignUp(
        email: String,
        password: String,
        isSignUp: Boolean
    ): Result<LoggedInUser> {
        val result = if (isSignUp) {
            service.signUp(SignInUpRequestBody(email = email, password = password))
        } else {
            service.signIn(SignInUpRequestBody(email = email, password = password))
        }
        val response = result.first
        val e = result.second
        if (e != null) {
            var code = -1
            val errorMsg = when (e) {
                is RedirectResponseException -> {
                    // 3xx - responses
                    code = e.response.status.value
                    e.response.status.description
                }
                is ClientRequestException -> {
                    // 4xx - responses
                    code = e.response.status.value
                    e.response.status.description
                }
                is ServerResponseException -> {
                    // 5xx - responses
                    code = e.response.status.value
                    e.response.status.description
                }
                else -> {
                    e.localizedMessage
                }
            }
            return Result.Error(
                exception = e,
                errorMsg = errorMsg,
                errorCode = code,
                timestamp = System.currentTimeMillis()
            )
        } else if (response != null) {
            return Result.Success(LoggedInUser(response.token))
        }
        val errMsg = "Error logging in or sign up"
        return Result.Error(
            IOException(errMsg, e),
            errMsg,
            null,
            System.currentTimeMillis()
        )
    }

    fun logout() {
        Prefs.clear()
    }
}
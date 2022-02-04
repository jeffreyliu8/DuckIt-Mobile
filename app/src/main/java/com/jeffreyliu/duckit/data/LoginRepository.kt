package com.jeffreyliu.duckit.data

import com.jeffreyliu.duckit.constant.PREF_KEY_TOKEN
import com.jeffreyliu.duckit.model.LoggedInUser
import com.pixplicity.easyprefs.library.Prefs

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(private val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = LoggedInUser(Prefs.getString(PREF_KEY_TOKEN))
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    suspend fun loginOrSignUp(
        username: String,
        password: String,
        isSignUp: Boolean
    ): Result<LoggedInUser> {
        val result = dataSource.loginOrSignUp(username, password, isSignUp)
        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }
        return result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        Prefs.putString(PREF_KEY_TOKEN, loggedInUser.token)
    }
}
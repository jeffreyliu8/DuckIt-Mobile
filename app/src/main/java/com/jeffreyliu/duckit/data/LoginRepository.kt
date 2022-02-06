package com.jeffreyliu.duckit.data

import android.util.Base64
import com.jeffreyliu.duckit.constant.PREF_KEY_TOKEN
import com.jeffreyliu.duckit.constant.PREF_KEY_TOKEN_IV
import com.jeffreyliu.duckit.model.LoggedInUser
import com.pixplicity.easyprefs.library.Prefs
import com.jeffreyliu.encryptlib.AndroidKeyStoreSymmetricEncryptor

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(private val dataSource: LoginDataSource) {

    private companion object {
        private const val keyStoreAlias = "my alias"
    }

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        val tokenPref = Prefs.getString(PREF_KEY_TOKEN)
        val ivPref = Prefs.getString(PREF_KEY_TOKEN_IV)

        if (ivPref.isNotBlank()) {
            val encryptedByteArray = Base64.decode(tokenPref, Base64.DEFAULT)
            val iv = Base64.decode(ivPref, Base64.DEFAULT)

            val se2 = AndroidKeyStoreSymmetricEncryptor()
            val decryptedByteArray =
                se2.decrypt(keyStoreAlias, encryptedByteArray!!, iv)

            val decryptedString = decryptedByteArray?.decodeToString()
            decryptedString?.let {
                user = LoggedInUser(it)
            }
        } else {
            val token = Prefs.getString(PREF_KEY_TOKEN)
            if (token.isNotBlank()) {
                this.user = LoggedInUser(Prefs.getString(PREF_KEY_TOKEN))
            }
        }
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
        val pin = loggedInUser.token
        val se = AndroidKeyStoreSymmetricEncryptor()
        val isKeyGenerated = se.generateKey(keyStoreAlias)

        if (isKeyGenerated) {
            val encryptedByteArray = se.encrypt(keyStoreAlias, pin.toByteArray())
            val encryptedString = Base64.encodeToString(encryptedByteArray, Base64.DEFAULT)
            val iv = se.getIV()
            val ivString = Base64.encodeToString(iv, Base64.DEFAULT)
            Prefs.putString(PREF_KEY_TOKEN, encryptedString)
            Prefs.putString(PREF_KEY_TOKEN_IV, ivString)
        } else {
            Prefs.putString(PREF_KEY_TOKEN, loggedInUser.token)
        }
    }
}
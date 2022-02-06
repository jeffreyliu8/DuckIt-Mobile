package com.jeffreyliu.duckit.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeffreyliu.duckit.R
import com.jeffreyliu.duckit.data.LoginRepository
import com.jeffreyliu.duckit.data.Result
import com.jeffreyliu.duckit.model.LoginFormState
import com.jeffreyliu.duckit.model.LoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun loginOrSignUp(email: String, password: String, isSignUp: Boolean = false) {
        viewModelScope.launch {
            val result = loginRepository.loginOrSignUp(email, password, isSignUp)

            if (result is Result.Success) {
                _loginResult.value = LoginResult(success = true)
            } else if (result is Result.Error && result.errorCode != null) {
                if (!isSignUp && (result.errorCode == 403 || result.errorCode == 404)) {
                    _loginResult.value = LoginResult(error = R.string.login_failed_403_404)
                } else if (isSignUp && result.errorCode == 409) {
                    _loginResult.value = LoginResult(error = R.string.sign_up_failed_409)
                } else {
                    _loginResult.value = LoginResult(error = R.string.login_failed)
                }
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}
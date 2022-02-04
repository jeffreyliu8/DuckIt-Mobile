package com.jeffreyliu.duckit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeffreyliu.duckit.data.LoginDataSource
import com.jeffreyliu.duckit.data.LoginRepository
import com.jeffreyliu.duckit.data.Result
import com.jeffreyliu.duckit.ktor.PostsService
import com.jeffreyliu.duckit.model.DuckPost
import com.jeffreyliu.duckit.model.DuckPostLoggedInWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.IOException

class MainViewModel : ViewModel() {

    private val service = PostsService.create()
    private val dataSource = LoginDataSource()
    private val loginRepository = LoginRepository(dataSource)

    private val _uiState = MutableStateFlow<Result<List<DuckPost>>>(Result.DoNothing)
    private val _loggedInState = MutableStateFlow(loginRepository.isLoggedIn)
    val loggedInState: StateFlow<Boolean> = _loggedInState

    val combinedFlow: Flow<Result<List<DuckPostLoggedInWrapper>>>
        get() = _uiState.combine(_loggedInState) { r, isLoggedIn ->
            when (r) {
                is Result.DoNothing -> {
                    Result.DoNothing
                }
                is Result.Loading -> {
                    Result.Loading
                }
                is Result.Success -> {
                    val list = r.data.map { DuckPostLoggedInWrapper(it, isLoggedIn) }
                    Result.Success(list)
                }
                is Result.Error -> {
                    Result.Error(
                        exception = r.exception,
                        errorMsg = r.errorMsg,
                        errorCode = r.errorCode,
                        timestamp = r.timestamp,
                    )
                }
            }
        }


    fun getPosts() {
        viewModelScope.launch {
            _uiState.value = Result.Loading
            val posts = service.getPosts()
            posts?.let {
                _uiState.value = Result.Success(it.posts)
            } ?: kotlin.run {
                _uiState.value = Result.Error(
                    exception = IOException("Error loading posts"),
                    errorMsg = "Please check internet",
                    errorCode = null,
                    System.currentTimeMillis()
                )
            }
        }
    }

    fun logout() {
        _loggedInState.value = false
        loginRepository.logout()
    }

    fun upVote(post: DuckPost) {

    }

    fun downVote(post: DuckPost) {

    }
}
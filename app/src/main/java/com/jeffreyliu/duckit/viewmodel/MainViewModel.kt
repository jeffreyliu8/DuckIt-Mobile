package com.jeffreyliu.duckit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeffreyliu.duckit.data.LoginDataSource
import com.jeffreyliu.duckit.data.LoginRepository
import com.jeffreyliu.duckit.data.Result
import com.jeffreyliu.duckit.ktor.PostsService
import com.jeffreyliu.duckit.model.DuckPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class MainViewModel : ViewModel() {
    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow<Result<List<DuckPost>>>(Result.DoNothing)

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<Result<List<DuckPost>>> = _uiState


    private val service = PostsService.create()
    private val dataSource = LoginDataSource()
    private val loginRepository = LoginRepository(dataSource)


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

    fun isUserLoggedIn(): Boolean {
        return loginRepository.isLoggedIn
    }

    fun logout() {
        loginRepository.logout()
    }
}
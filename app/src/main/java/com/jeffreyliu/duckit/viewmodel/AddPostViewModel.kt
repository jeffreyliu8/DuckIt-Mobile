package com.jeffreyliu.duckit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeffreyliu.duckit.data.Result
import com.jeffreyliu.duckit.ktor.PostsService
import com.jeffreyliu.duckit.model.DuckPostRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class AddPostViewModel : ViewModel() {

    private val service = PostsService.create()

    private val _uiState = MutableStateFlow<Result<Boolean>>(Result.DoNothing)
    val uiState: StateFlow<Result<Boolean>> = _uiState

    fun addPost(headLine: String, url: String) {
        viewModelScope.launch {
            _uiState.value = Result.Loading
            val result = service.createPost(DuckPostRequest(headLine, url))
            if (result) {
                _uiState.value = Result.Success(true)
            } else {
                val msg = "error occurred"
                _uiState.value = Result.Error(
                    IOException(msg),
                    errorMsg = msg,
                    errorCode = null,
                    timestamp = System.currentTimeMillis()
                )
            }
        }
    }
}
package com.jeffreyliu.duckit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeffreyliu.duckit.ktor.PostsService
import com.jeffreyliu.duckit.model.DuckPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {


    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow<SetUiState>(SetUiState.DoNothing)

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<SetUiState> = _uiState


    private val service = PostsService.create()

    fun getPosts() {
        viewModelScope.launch {
            _uiState.value = SetUiState.Loading
            val posts = service.getPosts()
            posts?.let {
                _uiState.value = SetUiState.Success(it.posts)
            } ?: kotlin.run {
                _uiState.value = SetUiState.Error(
                    exception = null,
                    errorMsg = "Please check internet",
                    System.currentTimeMillis()
                )
            }
        }
    }

    sealed class SetUiState {
        object DoNothing : SetUiState()
        object Loading : SetUiState()
        data class Success(val posts: List<DuckPost>) : SetUiState()
        data class Error(val exception: Throwable?, val errorMsg: String, val timestamp: Long) :
            SetUiState()
    }
}
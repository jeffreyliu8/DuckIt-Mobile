package com.jeffreyliu.duckit.data

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out T : Any> {
    object DoNothing : Result<Nothing>()
    object Loading : Result<Nothing>()
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(
        val exception: Exception,
        val errorMsg: String,
        val errorCode: Int?,
        val timestamp: Long
    ) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception][errorMsg=$errorMsg][errCode=$errorCode][time=$timestamp]"
            is DoNothing -> "DoNothing"
            is Loading -> "Loading"
        }
    }
}
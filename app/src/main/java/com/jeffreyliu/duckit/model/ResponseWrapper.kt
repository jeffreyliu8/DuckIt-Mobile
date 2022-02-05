package com.jeffreyliu.duckit.model

data class ResponseWrapper<T>(
    val response: T? = null,
    val e: Exception? = null,
)
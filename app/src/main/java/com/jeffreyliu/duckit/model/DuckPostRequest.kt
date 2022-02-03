package com.jeffreyliu.duckit.model

import kotlinx.serialization.Serializable

@Serializable
data class DuckPostRequest (
    val headline: String,
    val image: Int,
)
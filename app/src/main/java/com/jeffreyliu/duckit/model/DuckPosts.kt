package com.jeffreyliu.duckit.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DuckPosts (
    @SerialName("Posts")
    val posts: List<DuckPost>,
)
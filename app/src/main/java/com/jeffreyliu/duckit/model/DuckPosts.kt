package com.jeffreyliu.duckit.model

import kotlinx.serialization.SerialName


data class DuckPosts (
    @SerialName("Posts")
    val posts: List<DuckPost>,
)
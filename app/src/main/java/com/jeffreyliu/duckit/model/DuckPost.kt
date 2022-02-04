package com.jeffreyliu.duckit.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DuckPost (
    val id: String,
    val headline: String,
    val image: String,
    @SerialName("upvotes")
    val upVotes: Int,
    val author: String,
)
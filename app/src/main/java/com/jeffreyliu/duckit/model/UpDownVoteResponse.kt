package com.jeffreyliu.duckit.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpDownVoteResponse(
    @SerialName("upvotes")
    val upVotes: Int,
)
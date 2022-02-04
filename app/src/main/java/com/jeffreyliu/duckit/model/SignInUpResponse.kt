package com.jeffreyliu.duckit.model

import kotlinx.serialization.Serializable

@Serializable
data class SignInUpResponse(
    val token: String,
)
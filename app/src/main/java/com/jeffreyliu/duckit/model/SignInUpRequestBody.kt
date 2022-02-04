package com.jeffreyliu.duckit.model

import kotlinx.serialization.Serializable

@Serializable
data class SignInUpRequestBody(
    val email: String,
    val password: String,
)
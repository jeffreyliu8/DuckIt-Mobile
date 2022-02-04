package com.jeffreyliu.duckit.model

data class DuckPostLoggedInWrapper(
    val post: DuckPost,
    val loggedIn: Boolean,
)
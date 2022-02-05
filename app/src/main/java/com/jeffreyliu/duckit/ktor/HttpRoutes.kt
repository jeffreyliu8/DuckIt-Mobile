package com.jeffreyliu.duckit.ktor

object HttpRoutes {

    private const val BASE_URL = "https://nametag-duckit-2.uc.r.appspot.com"
    const val GET_POSTS = "$BASE_URL/posts"
    const val NEW_POST = "$BASE_URL/posts"
    const val SIGN_IN = "$BASE_URL/signin"
    const val SIGN_UP = "$BASE_URL/signup"

    const val UPVOTE_PREFIX = "$BASE_URL/posts/"
    const val DOWNVOTE_PREFIX = "$BASE_URL/posts/"

    const val UPVOTE_POSTFIX = "/upvote"
    const val DOWNVOTE_POSTFIX = "/downvote"
}
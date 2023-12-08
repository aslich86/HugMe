package com.dicoding.picodiploma.hugme.data.pref

data class UserModel(
    val token: String,
    var userId: String,
    var name: String,
    val isLogin: Boolean = false
)
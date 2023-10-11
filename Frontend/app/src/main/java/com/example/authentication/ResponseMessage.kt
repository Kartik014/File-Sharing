package com.example.authentication

data class ResponseMessage(val message: String)

data class userNames(val userArray: List<String>)

data class UserName(val name: String)

data class userDetails(val status: String, val userArray: UserArray)

data class UserArray(
    val __v: Int,
    val email: String,
    val id: Long,
    val name: String,
)

data class uploadFileClass(
    val name: String,
    val fileName: String,
    val extension: String,
    val fileData: String,
)
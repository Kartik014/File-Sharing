package com.example.authentication

data class ResponseMessage(val message: String, val name: String)

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

data class fileNames(val fileArray: List<String>)

data class fileDownloadBody(val name: String, val id: Long, val fileName: String)

data class downloadResponse(val status: String, val data: FileResponse)
data class FileResponse(val fileData: String, val __v: Int)
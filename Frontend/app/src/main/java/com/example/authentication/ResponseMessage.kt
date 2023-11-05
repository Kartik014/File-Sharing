package com.example.authentication

data class ResponseMessage(val message: String, val name: String)

data class userNames(val userArray: List<String>)

data class UserName(val name: String, val receiverID: Long)

data class userDetails(val status: String, val userArray: UserArray)

data class UserArray(
    val __v: Int,
    val email: String,
    val id: Long,
    val name: String,
    val connections: List<Long>
)

data class uploadFileClass(
    val name: String,
    val fileName: String,
    val extension: String,
    val fileData: String,
    val receiverID: Long,
)

data class fileNames(val fileArray: List<String>)

data class fileDownloadBody(
    val name: String,
    val senderID: Long,
    val fileName: String,
    val receiverID: Long,
)

data class downloadResponse(val status: String, val data: FileResponse)
data class FileResponse(val fileData: String, val __v: Int)

data class requestConnect(
    val senderID: Long,
    val receiverID: Long,
    val requestSenderName: String,
    val connectionStatus: String,
)

data class getConnectingUserList(val receiverID: Long)
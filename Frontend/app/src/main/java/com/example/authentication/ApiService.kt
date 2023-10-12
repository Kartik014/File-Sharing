package com.example.authentication

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("logIn/")
    fun authenticateUser(@Body request: LogInAuthentication): Call<ResponseMessage>

    @POST("signUp/")
    fun registerUser(@Body request: UserRegistration): Call<ResponseMessage>

    @GET("getUser/")
    fun getAllUsers(): Call<userNames>

    @POST("getUserDetails/")
    fun getUserDetails(@Body request: UserName): Call<userDetails>

    @POST("UploadFile/")
    fun uploadFile(@Body request: uploadFileClass): Call<ResponseMessage>

    @POST("getFiles/")
    fun getFileDetails(@Body request: UserName): Call<fileNames>

    @POST("downloadFile/")
    fun downloadFile(@Body request: fileDownloadBody): Call<downloadResponse>
}
package com.example.authentication

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private val client = OkHttpClient.Builder()
    .connectTimeout(90, java.util.concurrent.TimeUnit.SECONDS) // Set connection timeout to 30 seconds
    .readTimeout(90, java.util.concurrent.TimeUnit.SECONDS)
    .writeTimeout(90, java.util.concurrent.TimeUnit.SECONDS)// Set read timeout to 30 seconds
    .build()

object RetrofitBuilder {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.12.1.155:3000/api/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(ApiService::class.java)
}
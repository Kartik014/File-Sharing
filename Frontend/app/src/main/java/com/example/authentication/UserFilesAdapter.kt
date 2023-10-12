package com.example.authentication

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class UserFilesAdapter(val context: Context, val userFilesList: List<String>, val name: String, val id: Long) : RecyclerView.Adapter<UserFilesAdapter.userFileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userFileViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_files_display, parent, false)
        return userFileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: userFileViewHolder, position: Int) {

        holder.fileName.text = userFilesList[position]
        val fileName = userFilesList[position]

        holder.fileName.setOnClickListener {
            val fileDownloadBody = fileDownloadBody(name, id, fileName)

            RetrofitBuilder.api.downloadFile(fileDownloadBody).enqueue(object: Callback<downloadResponse>{
                override fun onResponse(
                    call: Call<downloadResponse>,
                    response: Response<downloadResponse>,
                ) {
                    if(response.code() == 200) {
                        val responseBody = response.body()
                        if(responseBody != null) {
                            val fileData = responseBody.data.fileData
                            val decodedBitmap = decodeBase64ToBitmap(fileData)
                            if(decodedBitmap != null) {
                                holder.imageDisplay.setImageBitmap(decodedBitmap)
                                saveBitmapToDownloads(fileName, decodedBitmap)
                            } else {
                                Log.d("DETAILS", "fileDATA: null")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<downloadResponse>, t: Throwable) {
                    Log.d("ERROR", "${t.message}")
                }

            })
        }
    }

    override fun getItemCount(): Int {

        return userFilesList.size
    }

    class userFileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val fileName: TextView = itemView.findViewById(R.id.usersFiles)
        val imageDisplay: ImageView = itemView.findViewById(R.id.dislpayImage)
    }

    private fun decodeBase64ToBitmap(base64Image: String): Bitmap? {
        try {
            val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
            val inputStream = ByteArrayInputStream(imageBytes)
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun saveBitmapToDownloads(fileName: String, bitmap: Bitmap) {
        try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)

                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
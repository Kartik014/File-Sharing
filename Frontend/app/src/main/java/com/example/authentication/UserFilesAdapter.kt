package com.example.authentication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class UserFilesAdapter(val context: Context, val userFilesList: List<String>, val name: String, val id: Long, val receiverID: Long ) : RecyclerView.Adapter<UserFilesAdapter.userFileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userFileViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_files_display, parent, false)
        return userFileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: userFileViewHolder, position: Int) {
        holder.fileName.text = userFilesList[position]
        val fileName = userFilesList[position]

        holder.fileName.setOnClickListener {
            holder.dowloadBar.visibility = View.VISIBLE
            holder.dowloadBar.progress = 0

            val fileDownloadBody = fileDownloadBody(name, receiverID, fileName, id)

            GlobalScope.launch(Dispatchers.IO) {
                RetrofitBuilder.api.downloadFile(fileDownloadBody).enqueue(object : Callback<downloadResponse> {
                    override fun onResponse(call: Call<downloadResponse>, response: Response<downloadResponse> ) {
                        if (response.code() == 200) {
                            val responseBody = response.body()
                            if (responseBody != null) {
                                val fileData = responseBody.data.fileData
                                val decodedBytes = Base64.decode(fileData, Base64.DEFAULT)
                                val contentLength = response.headers().get("Content-Length")?.toLong()
                                if (contentLength != null) {
                                    holder.dowloadBar.max = 100
                                    val inputStream = ByteArrayInputStream(decodedBytes)
                                    val bufferSize = 1024
                                    val buffer = ByteArray(bufferSize)
                                    var totalBytesRead: Long = 0
                                    val contentLengthInt = contentLength.toInt()
                                    val byteArrayOutputStream = ByteArrayOutputStream()

                                    while (true) {
                                        val bytesRead = inputStream.read(buffer)
                                        if (bytesRead == -1) {
                                            break
                                        }
                                        totalBytesRead += bytesRead
                                        val progress = ((totalBytesRead * 100 / contentLengthInt).toInt())
                                        holder.dowloadBar.progress = progress
                                        byteArrayOutputStream.write(buffer, 0, bytesRead)
                                    }

                                    val byteArray = byteArrayOutputStream.toByteArray()
                                    val decodedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                                    if (decodedBitmap != null) {
                                        holder.imageDisplay.setImageBitmap(decodedBitmap)
                                        saveBitmapToDownloads(fileName, decodedBitmap)
                                    } else {
                                        Log.d("DETAILS", "fileDATA: null")
                                    }
                                    holder.dowloadBar.visibility = View.GONE
                                } else {
                                    Log.d("DETAILS", "Content-Length header not found")
                                    holder.dowloadBar.visibility = View.GONE
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<downloadResponse>, t: Throwable) {
                        Log.d("ERROR", "${t.message}")
                        holder.dowloadBar.visibility = View.GONE
                    }
                })
            }
        }
    }

    override fun getItemCount(): Int {

        return userFilesList.size
    }

    class userFileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val fileName: TextView = itemView.findViewById(R.id.usersFiles)
        val imageDisplay: ImageView = itemView.findViewById(R.id.dislpayImage)
        val dowloadBar: ProgressBar = itemView.findViewById(R.id.downloadBar)
    }

    private fun saveBitmapToDownloads(fileName: String, bitmap: Bitmap) {
        try {
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
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
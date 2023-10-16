package com.example.authentication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.authentication.databinding.ActivityUserDetalisBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDetalisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetalisBinding
    private var name: String? =null
    private var email: String? =null
    private var id: String? =null
    private var senderID: Long? =0
    private var requestSenderName: String? =null
    private lateinit var userFilesAdapter: UserFilesAdapter
    private lateinit var fileList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetalisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        name = intent.getStringExtra("name")
        email = intent.getStringExtra("email")
        id = intent.getStringExtra("id")
        senderID = intent.getLongExtra("senderID", 0)
        requestSenderName = intent.getStringExtra("requestSenderName")

        binding.name.text = name.toString()
        binding.email.text = email.toString()
        binding.id.text = id.toString()

        val userName = UserName(name.toString(), senderID!!.toLong())

        binding.connect.setOnClickListener {
            val requestConnect = requestConnect(senderID!!.toLong(), id!!.toLong(), requestSenderName.toString(), "")
            RetrofitBuilder.api.requestConnection(requestConnect).enqueue(object: Callback<ResponseMessage>{
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>,
                ) {
                    if(response.code() == 200) {
                        val responseBody = response.body()
                        Toast.makeText(this@UserDetalisActivity,responseBody!!.message,Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    Log.d("ERROR", "${t.message}")
                }
            })
        }

        binding.sendFiles.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        binding.showFiles.setOnClickListener {
            Log.d("RESPONSE","senderID: ${senderID.toString()}")
            RetrofitBuilder.api.getFileDetails(userName).enqueue(object: Callback<fileNames>{
                override fun onResponse(call: Call<fileNames>, response: Response<fileNames>) {
                    if(response.code() == 200) {
                        val responseBody = response.body()
                        if(responseBody != null){
                            fileList = responseBody?.fileArray ?: emptyList()
                        }
                        userFilesAdapter = UserFilesAdapter(this@UserDetalisActivity,fileList, name.toString(), senderID!!.toLong(), id!!.toLong())
                        binding.userFilesContainer.adapter = userFilesAdapter
                        userFilesAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<fileNames>, t: Throwable) {
                    Log.d("ERROR", "${t.message}")
                }

            })
        }
        binding.userFilesContainer.layoutManager = LinearLayoutManager(this)
    }

    private val pickImageLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                val base64Image = convertImageToBase64(selectedImageUri)
                val fileName = getFileName(selectedImageUri)
                val fileExtension = getFileExtension(selectedImageUri)

                val uploadFileData = uploadFileClass(requestSenderName.toString(), fileName, fileExtension, base64Image, id!!.toLong())
                RetrofitBuilder.api.uploadFile(uploadFileData)
                    .enqueue(object : Callback<ResponseMessage> {
                        override fun onResponse(
                            call: Call<ResponseMessage>,
                            response: Response<ResponseMessage>,
                        ) {
                            if (response.code() == 200) {
                                Log.d("DETAILS", "DATA: ${response.body()!!.message}")
                            }
                        }

                        override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                            Log.d("ERROR", "ERROR: ${t.message}")
                        }

                    })
            }
        }
    }

    private fun convertImageToBase64(imageUri: Uri): String {
        val inputStream = contentResolver.openInputStream(imageUri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()

        if (bytes != null) {
            val base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)
            return base64Image
        }

        return ""
    }

    private fun getFileExtension(uri: Uri): String {
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ?: ""
    }

    private fun getFileName(uri: Uri): String {
        var result = ""
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val nameColumnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameColumnIndex != -1 && it.moveToFirst()) {
                    result = it.getString(nameColumnIndex)
                }
            }
        }
        if (result.isEmpty()) {
            result = uri.lastPathSegment ?: ""
        }
        Log.d("RESULT", "${result}")
        return result
    }

}
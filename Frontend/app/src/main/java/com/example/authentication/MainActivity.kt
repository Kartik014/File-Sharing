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
import com.example.authentication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userNameAdapter: userNameAdapter
    private lateinit var userList: List<String>
    private lateinit var userName: String
    private var id: Long = 0

    private val pickImageLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                val base64Image = convertImageToBase64(selectedImageUri)
                val fileName = getFileName(selectedImageUri)
                val fileExtension = getFileExtension(selectedImageUri)

                val uploadFileData = uploadFileClass(userName, fileName, fileExtension, base64Image)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userList = ArrayList()
        userName = intent.getStringExtra("userName").toString()

        val requestDetails = UserName(userName)

        RetrofitBuilder.api.getUserDetails(requestDetails).enqueue(object :Callback<userDetails>{
            override fun onResponse(call: Call<userDetails>, response: Response<userDetails>) {
                if(response.code() == 200){
                    Log.d("DETAILS", "${response.body()!!.userArray}")
                    id = response.body()!!.userArray.id
                }
            }

            override fun onFailure(call: Call<userDetails>, t: Throwable) {
                Log.d("ERROR", "${t.message}")
            }

        })

        binding.fetchData.setOnClickListener {
            RetrofitBuilder.api.getAllUsers().enqueue(object : Callback<userNames> {
                override fun onResponse(
                    call: Call<userNames>,
                    response: Response<userNames>,
                ) {
                    if (response.code() == 200) {
                        val responseBody = response.body()
                        Log.d("Kartik", "Response Body: ${responseBody!!.userArray}")
                        userList = responseBody!!.userArray
                        Log.d("kartik", "userList: ${userList}")
                    }
                    userNameAdapter = userNameAdapter(this@MainActivity, userList, id, userName)
                    binding.displayFetchedData.adapter = userNameAdapter
                    userNameAdapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<userNames>, t: Throwable) {
                    Log.d("ERROR", "${t.message}")
                    Toast.makeText(this@MainActivity, "Error in fetching data", Toast.LENGTH_LONG).show()
                }

            })
        }
        binding.displayFetchedData.layoutManager = LinearLayoutManager(this)

        binding.uploadFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        binding.requestButton.setOnClickListener {
            val intent = Intent(this@MainActivity, connectionRequest::class.java)
            intent.putExtra("receiverID", id)
            startActivity(intent)
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
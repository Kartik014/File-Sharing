package com.example.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
    private lateinit var userFilesAdapter: UserFilesAdapter
    private lateinit var fileList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetalisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        name = intent.getStringExtra("name")
        email = intent.getStringExtra("email")
        id = intent.getStringExtra("id")

        binding.name.text = name.toString()
        binding.email.text = email.toString()
        binding.id.text = id.toString()

        val userName = UserName(name.toString())

        binding.showFiles.setOnClickListener {
            RetrofitBuilder.api.getFileDetails(userName).enqueue(object: Callback<fileNames>{
                override fun onResponse(call: Call<fileNames>, response: Response<fileNames>) {
                    if(response.code() == 200) {
                        val responseBody = response.body()
                        if(responseBody != null){
                            fileList = responseBody!!.fileArray
                        }
                        userFilesAdapter = UserFilesAdapter(this@UserDetalisActivity,fileList, name.toString(), id!!.toLong())
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
}
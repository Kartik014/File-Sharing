package com.example.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userList = ArrayList()
        userName = intent.getStringExtra("userName").toString()

        val requestDetails = UserName(userName, 0)

        binding.progressBar.visibility = View.VISIBLE

        RetrofitBuilder.api.getUserDetails(requestDetails).enqueue(object : Callback<userDetails> {
            override fun onResponse(call: Call<userDetails>, response: Response<userDetails>) {
                if (response.code() == 200) {
                    id = response.body()!!.userArray.id

                    fetchUserNames()
                }
                binding.progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<userDetails>, t: Throwable) {
                Log.d("ERROR", "${t.message}")
                binding.progressBar.visibility = View.GONE
            }

        })

        binding.displayFetchedData.layoutManager = LinearLayoutManager(this)

        binding.requestButton.setOnClickListener {
            val intent = Intent(this@MainActivity, connectionRequest::class.java)
            intent.putExtra("receiverID", id)
            startActivity(intent)
        }
    }

    private fun fetchUserNames() {
        binding.progressBar.visibility = View.VISIBLE
        RetrofitBuilder.api.getAllUsers().enqueue(object : Callback<userNames> {
            override fun onResponse(
                call: Call<userNames>,
                response: Response<userNames>,
            ) {
                if (response.code() == 200) {
                    val responseBody = response.body()
                    Log.d("Kartik", "Response Body: ${responseBody!!.userArray}")
                    userList = responseBody!!.userArray
                }
                userNameAdapter = userNameAdapter(this@MainActivity, userList, id, userName)
                binding.displayFetchedData.adapter = userNameAdapter
                userNameAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<userNames>, t: Throwable) {
                Log.d("ERROR", "${t.message}")
                Toast.makeText(this@MainActivity, "Error in fetching data", Toast.LENGTH_LONG)
                    .show()
            }

        })
    }
}
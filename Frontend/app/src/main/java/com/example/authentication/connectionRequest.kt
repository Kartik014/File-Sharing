package com.example.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.authentication.databinding.ActivityConnectionRequestBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class connectionRequest : AppCompatActivity() {

    private lateinit var binding: ActivityConnectionRequestBinding
    private var receiverID: Long = 0
    private lateinit var connectionAdapter: connectionAdapter
    private lateinit var requestedUserlist: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectionRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        receiverID = intent.getLongExtra("receiverID", 0)
        Log.d("RESPONSE", "${intent.getLongExtra(" receiverID ", 0)}")
        requestedUserlist = ArrayList()
        Log.d("RESPONSE", "$receiverID")
        val getConnectingUserList = getConnectingUserList(receiverID)

        binding.progressBar.visibility = View.VISIBLE

        RetrofitBuilder.api.getConnectingNames(getConnectingUserList)
            .enqueue(object : Callback<userNames> {
                override fun onResponse(call: Call<userNames>, response: Response<userNames>) {
                    if (response.code() == 200) {
                        requestedUserlist = response.body()!!.userArray.toMutableList()
                        if(requestedUserlist.isNullOrEmpty()) {
                            binding.progressBar.visibility = View.GONE
                            binding.textView.visibility = View.VISIBLE
                        } else {
                        Log.d("RESPONSE", "$requestedUserlist")
                        connectionAdapter = connectionAdapter(this@connectionRequest, receiverID, requestedUserlist)
                        binding.connectionRequest.adapter = connectionAdapter
                        binding.progressBar.visibility = View.GONE
                        connectionAdapter.notifyDataSetChanged()
                        }
                    } else {
                        binding.progressBar.visibility = View.GONE
                        Log.d("RESPONSE", "$response.body()!!.userArray")
                    }
                }

                override fun onFailure(call: Call<userNames>, t: Throwable) {
                    Log.d("ERROR", "${t.message}")
                }

            })

        binding.connectionRequest.layoutManager = LinearLayoutManager(this)
    }
}
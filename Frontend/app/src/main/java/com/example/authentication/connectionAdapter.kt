package com.example.authentication

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class connectionAdapter(val context: Context,val receiverID: Long, val requestedUserlist: MutableList<String>): RecyclerView.Adapter<connectionAdapter.connectionRequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): connectionRequestViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.requested_connection_display, parent, false)
        return connectionRequestViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        Log.d("RESPONSE","requestedUserlist: $requestedUserlist")
        Log.d("RESPONSE","SIZE: $requestedUserlist.size")
        return requestedUserlist.size
    }

    override fun onBindViewHolder(holder: connectionRequestViewHolder, position: Int) {
        holder.requestedUsername.text = requestedUserlist[position]
        val name = requestedUserlist[position]
        var senderID: Long = 0
        val requestDetails = UserName(name)
        RetrofitBuilder.api.getUserDetails(requestDetails).enqueue(object :
            Callback<userDetails> {
            override fun onResponse(call: Call<userDetails>, response: Response<userDetails>) {
                if(response.code() == 200){
                    Log.d("DETAILS", "${response.body()!!.userArray}")
                    senderID = response.body()!!.userArray.id
                    Log.d("RESPONSE","ID: $receiverID")
                }
            }

            override fun onFailure(call: Call<userDetails>, t: Throwable) {
                Log.d("ERROR", "${t.message}")
            }

        })
        holder.acceptingButton.setOnClickListener {
            val requestConnect = requestConnect(senderID, receiverID, "", "Accepted")
            RetrofitBuilder.api.responseConnection(requestConnect).enqueue(object: Callback<ResponseMessage>{
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>,
                ) {
                    if (response.isSuccessful) {
                        val responseMessage = response.body()
                        if (responseMessage != null) {
                            Log.d("RESPONSE", responseMessage.message)

                            requestedUserlist.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, requestedUserlist.size)
                        } else {
                            Log.d("ERROR", "Response body is null")
                        }
                    } else {
                        Log.d("ERROR", "Response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    Log.d("ERROR", "${t.message}")
                }

            })
        }

        holder.rejectingButton.setOnClickListener {
            val requestConnect = requestConnect(senderID, receiverID, "", "Rejected")
            RetrofitBuilder.api.responseConnection(requestConnect).enqueue(object: Callback<ResponseMessage>{
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>,
                ) {
                    if (response.isSuccessful) {
                        val responseMessage = response.body()
                        if (responseMessage != null) {
                            Log.d("RESPONSE", responseMessage.message)
                            requestedUserlist.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, requestedUserlist.size)
                        } else {
                            Log.d("ERROR", "Response body is null")
                        }
                    } else {
                        Log.d("ERROR", "Response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    Log.d("ERROR", "${t.message}")
                }

            })
        }
    }

    class connectionRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val requestedUsername: TextView = itemView.findViewById(R.id.UserName)
        val acceptingButton: Button = itemView.findViewById(R.id.acceptButton)
        val rejectingButton: Button = itemView.findViewById(R.id.rejectButton)
    }

}
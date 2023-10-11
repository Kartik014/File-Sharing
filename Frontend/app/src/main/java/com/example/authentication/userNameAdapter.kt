package com.example.authentication

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class userNameAdapter(val context: Context, private var userList: List<String>) : RecyclerView.Adapter<userNameAdapter.userNameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userNameViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_name_display, parent, false)
        return userNameViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: userNameViewHolder, position: Int) {

        holder.userNameText.text = userList[position]
        val name: String = userList[position]

        val requestDetails = UserName(name)

        holder.itemView.setOnClickListener {

            RetrofitBuilder.api.getUserDetails(requestDetails).enqueue(object :Callback<userDetails>{
                override fun onResponse(call: Call<userDetails>, response: Response<userDetails>) {
                    if(response.code() == 200){
                        Log.d("DETAILS", "${response.body()!!.userArray}")
                        val intent = Intent(context, UserDetalisActivity::class.java)

                        intent.putExtra("name", response.body()!!.userArray.name)
                        intent.putExtra("email", response.body()!!.userArray.email)
                        intent.putExtra("id", response.body()!!.userArray.id.toString())

                        context.startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<userDetails>, t: Throwable) {
                    Log.d("ERROR", "${t.message}")
                }

            })
        }
    }

    override fun getItemCount(): Int {

        return userList.size
    }

    class userNameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val userNameText: TextView = itemView.findViewById(R.id.nameDisplay)
    }

}

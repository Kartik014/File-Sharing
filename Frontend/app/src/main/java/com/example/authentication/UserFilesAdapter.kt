package com.example.authentication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserFilesAdapter(val userFilesList: List<String>) : RecyclerView.Adapter<UserFilesAdapter.userFileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userFileViewHolder {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.user_files_display, parent, false)
        return userFileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: userFileViewHolder, position: Int) {

        holder.fileName.text = userFilesList[position]
    }

    override fun getItemCount(): Int {

        return userFilesList.size
    }

    class userFileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val fileName: TextView = itemView.findViewById(R.id.usersFiles)
    }

}
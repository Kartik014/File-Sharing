package com.example.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.authentication.databinding.ActivityUserDetalisBinding

class UserDetalisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetalisBinding
    private var name: String? =null
    private var email: String? =null
    private var id: String? =null

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
    }
}
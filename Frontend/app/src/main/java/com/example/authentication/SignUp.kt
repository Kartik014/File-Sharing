package com.example.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.authentication.databinding.ActivitySignUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUp.setOnClickListener {
            val name = binding.name.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            val userRegister = UserRegistration(name, email, password)

            RetrofitBuilder.api.registerUser(userRegister).enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>,
                ) {
                    if(response.code() == 200) {
                        Toast.makeText(this@SignUp, response.body()!!.message, Toast.LENGTH_LONG).show()
                        Log.d("MESSAGE", response.body()!!.message)
                        val intent = Intent(this@SignUp, LogIn::class.java)
                        startActivity(intent)
                    } else {
                        Log.d("ERROR", response.body()!!.message)
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    Log.d("ERROR", "${call}")
                }

            })
        }
    }
}
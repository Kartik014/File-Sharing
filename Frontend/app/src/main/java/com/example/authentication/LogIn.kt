package com.example.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.authentication.databinding.ActivityLogInBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogIn : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logIn.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            val authRequest = LogInAuthentication(email, password)

            RetrofitBuilder.api.authenticateUser(authRequest).enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>,
                ) {
                    if(response.code() == 200) {
                        Toast.makeText(this@LogIn, response.body()!!.message, Toast.LENGTH_LONG).show()
                        val intent = Intent(this@LogIn, MainActivity::class.java)
                        intent.putExtra("userName",response.body()!!.name)
                        startActivity(intent)
                    } else {
                        Log.d("MESSAGE", response.body()!!.message)
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    Log.d("ERROR", "${t.message}")
                }

            })
        }

        binding.signUpRedirector.setOnClickListener {
            val intent = Intent(this@LogIn, SignUp::class.java)
            startActivity(intent)
        }
    }
}
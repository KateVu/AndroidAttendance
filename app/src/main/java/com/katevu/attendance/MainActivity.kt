package com.katevu.attendance

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.katevu.attendance.databinding.ActivityLoginBinding
import com.katevu.attendance.ui.login.LoginViewModel

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSignin.setOnClickListener{login()}
    }

    private fun login() {
        val userName = binding.username.text.toString()
        val password = binding.password.text.toString()
        Log.d(TAG, "Username: ${userName}")
        Log.d(TAG, "Password: ${password}")
        viewModel.getPost(userName, password)

    }
}
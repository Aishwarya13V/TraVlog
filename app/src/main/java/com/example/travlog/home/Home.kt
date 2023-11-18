package com.example.travlog.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.travlog.login.LoginViewModel

@Composable
fun Home(){
    val loginViewModel = LoginViewModel()
    val email = loginViewModel.currentUser?.email
    Text(text = "This is Home Screen. welcome $email")
}
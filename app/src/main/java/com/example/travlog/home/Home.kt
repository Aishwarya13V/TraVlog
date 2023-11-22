package com.example.travlog.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.travlog.login.LoginViewModel
import com.example.travlog.ui.theme.TraVlogTheme

@Composable
fun Home(){
    val loginViewModel = LoginViewModel()
    val email = loginViewModel.currentUser?.email
    Text(text = "This is Home Screen. welcome $email")
}

@Preview(showSystemUi = true)
@Composable
fun PrevSignUpScreen() {
    TraVlogTheme {
        Home()
    }
}
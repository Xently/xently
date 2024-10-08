package com.kwanzatukule.features.authentication.presentation.signup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SignUpScreen(component: SignUpComponent, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Text("Sign Up")
        }
    }
}

package com.kwanzatukule.features.authentication.presentation.resetpassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.ke.xently.libraries.ui.core.XentlyPreview
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme


@Composable
fun ResetPasswordScreen(component: ResetPasswordComponent, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            Text("Reset Password")
        }
    }
}

@XentlyPreview
@Composable
private fun ResetPasswordScreenPreview() {
    KwanzaTukuleTheme {
        ResetPasswordScreen(
            component = ResetPasswordComponent.Fake,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

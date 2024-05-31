package com.kwanzatukule.features.authentication.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.authentication.presentation.AuthenticationNavigationGraphComponent.Child
import com.kwanzatukule.features.authentication.presentation.resetpassword.ResetPasswordScreen
import com.kwanzatukule.features.authentication.presentation.signin.SignInScreen
import com.kwanzatukule.features.authentication.presentation.signup.SignUpScreen

@Composable
fun AuthenticationNavigationGraph(
    component: AuthenticationNavigationGraphComponent,
    modifier: Modifier = Modifier,
) {
    val childStack by component.childStack.subscribeAsState()
    Children(
        modifier = modifier,
        stack = childStack,
        animation = stackAnimation(),
        content = {
            when (val instance = it.instance) {
                is Child.ResetPassword -> ResetPasswordScreen(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize(),
                )

                is Child.SignIn -> SignInScreen(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize(),
                )

                is Child.SignUp -> SignUpScreen(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        },
    )
}

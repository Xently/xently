package com.kwanzatukule.features.authentication.presentation.signup

import com.arkivanov.decompose.ComponentContext
import com.kwanzatukule.features.authentication.data.UserRepository

class SignUpComponent(
    context: ComponentContext,
    private val repository: UserRepository,
    private val onBackPress: () -> Unit,
    private val onSignIn: () -> Unit,
) : ComponentContext by context

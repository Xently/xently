package com.kwanzatukule.features.authentication.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.authentication.data.UserRepository
import com.kwanzatukule.features.authentication.presentation.resetpassword.DefaultResetPasswordComponent
import com.kwanzatukule.features.authentication.presentation.resetpassword.ResetPasswordComponent
import com.kwanzatukule.features.authentication.presentation.signin.SignInComponent
import com.kwanzatukule.features.authentication.presentation.signin.SignInComponentImpl
import com.kwanzatukule.features.authentication.presentation.signup.SignUpComponent
import kotlinx.serialization.Serializable

class AuthenticationNavigationGraphComponent(
    context: ComponentContext,
    private val repository: UserRepository,
    private val onBackPress: () -> Unit,
) : ComponentContext by context {
    private val navigation = StackNavigation<Configuration>()
    val childStack: Value<ChildStack<Configuration, Child>> = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.SignIn,
        handleBackButton = true,
        childFactory = ::createChild,
    )

    private fun createChild(config: Configuration, context: ComponentContext): Child {
        return when (config) {
            Configuration.SignIn -> Child.SignIn(
                component = SignInComponentImpl(
                    context = context,
                    repository = repository,
                    component = object : SignInComponent {
                        override fun handleBackPress() {
                            onBackPress()
                        }

                        override fun handleSignUp() {
                            navigation.pushNew(Configuration.SignUp)
                        }

                        override fun handleForgotPassword() {
                            navigation.pushNew(Configuration.ResetPassword)
                        }
                    },
                ),
            )

            Configuration.SignUp -> Child.SignUp(
                component = SignUpComponent(
                    context = context,
                    repository = repository,
                    onBackPress = { navigation.pop() },
                    // The sign-up page should only be accessible through the login screen,
                    // therefore going back to the login screen should be possible through
                    // a back press.
                    onSignIn = { navigation.pop() },
                ),
            )

            Configuration.ResetPassword -> Child.ResetPassword(
                component = DefaultResetPasswordComponent(
                    context = context,
                    repository = repository,
                    component = object : ResetPasswordComponent {
                        override fun handleBackPress() {
                            navigation.pop()
                        }

                        override fun handleSignIn() {
                            // The reset password page should only be accessible through the
                            // login screen, therefore going back to the login screen should
                            // be possible through a back press.
                            navigation.pop()
                        }
                    },
                ),
            )
        }
    }

    sealed class Child {
        data class SignIn(val component: SignInComponent) : Child()
        data class SignUp(val component: SignUpComponent) : Child()
        data class ResetPassword(val component: ResetPasswordComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object SignIn : Configuration()

        @Serializable
        data object SignUp : Configuration()

        @Serializable
        data object ResetPassword : Configuration()
    }
}

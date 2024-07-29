package com.example.project_sns.ui.util

sealed interface CheckSignUp {
    data object SignUpSuccess: CheckSignUp
    data class SignUpFail(
        val message: String
    ): CheckSignUp

}

sealed interface CheckLogin {
    data class LoginSuccess(
        val message: String
    ): CheckLogin
    data class LoginFail(
        val message: String
    ): CheckLogin
}

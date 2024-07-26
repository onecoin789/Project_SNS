package com.example.project_sns.ui.util

sealed interface CheckSignUp {
    data class SignUpSuccess(
        val message: String
    ): CheckSignUp

    data class SignUpFail(
        val message: String
    ): CheckSignUp
}
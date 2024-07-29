package com.example.project_sns.ui.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.LogInUseCase
import com.example.project_sns.ui.util.CheckLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val logInUseCase: LogInUseCase
) : ViewModel() {

    private val _loginEvent = Channel<CheckLogin> { }
    val loginEvent = _loginEvent.receiveAsFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val loginData = logInUseCase(email = email, password = password)
            if (loginData.isSuccess) {
                _loginEvent.send(CheckLogin.LoginSuccess("환영합니다! ${email}님\uD83D\uDC4D"))
            } else {
                _loginEvent.send(CheckLogin.LoginFail("이메일 및 비밀번호를 확인해주세요."))
            }
        }
    }
}
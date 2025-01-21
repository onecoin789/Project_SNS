package com.example.project_sns.ui.view.login

import android.util.Printer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.CheckLoginUseCase
import com.example.project_sns.domain.usecase.GetLoginSessionUseCase
import com.example.project_sns.domain.usecase.LogInUseCase
import com.example.project_sns.domain.usecase.LoginWithKakaoUseCase
import com.example.project_sns.ui.util.CheckLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val logInUseCase: LogInUseCase,
    private val loginWithKakaoUseCase: LoginWithKakaoUseCase
) : ViewModel() {

    private val _loginEvent = Channel<CheckLogin> { }
    val loginEvent = _loginEvent.receiveAsFlow()

    private val _kakaoLoginEvent = Channel<CheckLogin> { }
    val kakaoLoginEvent = _kakaoLoginEvent.receiveAsFlow()



    fun login(email: String, password: String) {
        viewModelScope.launch {
            logInUseCase(email = email, password = password).collect { loginResult ->
                if (loginResult == "success") {
                    _loginEvent.send(CheckLogin.LoginSuccess("환영합니다! ${email}님\uD83D\uDC4D"))
                } else if (loginResult == "fail") {
                    _loginEvent.send(CheckLogin.LoginFail("이메일 및 비밀번호를 확인해주세요."))
                }
            }
        }
    }

    fun kakaoLogin(accessToken: String) {
        viewModelScope.launch {
            val loginData = loginWithKakaoUseCase(accessToken)
            if (loginData.isSuccess) {
                _kakaoLoginEvent.send(CheckLogin.LoginSuccess("환영합니다!\uD83D\uDC4D"))
            } else {
                _kakaoLoginEvent.send(CheckLogin.LoginFail("로그인에 오류가 생겼습니다. 잠시후 다시 시도해주세요."))
            }
        }
    }
}
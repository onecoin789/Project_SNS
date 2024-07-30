package com.example.project_sns.ui.view.signup

import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.SignUpUseCase
import com.example.project_sns.ui.util.CheckSignUp
import com.example.project_sns.ui.view.signup.model.FirebaseUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _signUpEvent = Channel<CheckSignUp> { }
    val signUpEvent = _signUpEvent.receiveAsFlow()

    fun signUp(email: String, password: String, data: FirebaseUserData) {
        viewModelScope.launch {
            val userData = signUpUseCase(email = email, password = password, data = data)
            if (userData.isSuccess) {
                _signUpEvent.send(CheckSignUp.SignUpSuccess)
            } else {
                _signUpEvent.send(CheckSignUp.SignUpFail("이미 회원가입 되어있습니다."))
            }
        }
    }

    // 이름 유효성 검사
    fun checkName(name: String, item: TextView): Boolean {
        val nameText = name.trim()
        val namePattern = Pattern.matches("^[ㄱ-ㅣ가-힣a-zA-Z\\s]+$", nameText)
        if (namePattern) {
            item.visibility = View.INVISIBLE
            return true
        } else {
            item.visibility = View.VISIBLE
            return false
        }
    }

    // 이메일 유효성 검사
    fun checkEmail(id: String, item: TextView): Boolean {
        val emailText = id.trim()
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()
        if (emailPattern) {
            item.visibility = View.INVISIBLE
            return true
        } else {
            item.visibility = View.VISIBLE
            return false
        }
    }

    // 비밀번호 유효성 검사 (8~20자 영문 + 숫자)
    fun checkPw(pw: String, item: TextView): Boolean {
        val pwText = pw.trim()
        val pwPattern = Pattern.matches("^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{8,20}$", pwText)
        if (pwPattern) {
            item.visibility = View.INVISIBLE
            return true
        } else {
            item.visibility = View.VISIBLE
            return false
        }
    }

    fun checkConfirmPw(pw: String, confirmPw: String, item: TextView): Boolean {
        val pwText = pw.trim()
        val confirmPwText = confirmPw.trim()
        if (pwText == confirmPwText) {
            item.visibility = View.INVISIBLE
            return true
        } else {
            item.visibility = View.VISIBLE
            return false
        }
    }

    private fun nullCheck(text: String): Boolean {
        return text.isEmpty()
    }

    fun checkSignUp(
        name: String,
        email: String,
        password: String,
        confirmPw: String,
        nameCheck: TextView,
        emailCheck: TextView,
        passwordCheck: TextView,
        confirmCheck: TextView,
        data: FirebaseUserData
    ) {

        if (nullCheck(name) || nullCheck(email) || nullCheck(password) || nullCheck(confirmPw)) {
            viewModelScope.launch {
                _signUpEvent.send(CheckSignUp.SignUpFail("모든 필드를 입력하세요."))
            }
            return
        }

        if (!checkName(name, nameCheck)) {
            viewModelScope.launch {
                _signUpEvent.send(CheckSignUp.SignUpFail("유효하지 않은 이름입니다."))
            }
            return
        }

        if (!checkEmail(email, emailCheck)) {
            viewModelScope.launch {
                _signUpEvent.send(CheckSignUp.SignUpFail("유효하지 않은 이메일입니다."))
            }
            return
        }

        if (!checkPw(password, passwordCheck)) {
            viewModelScope.launch {
                _signUpEvent.send(CheckSignUp.SignUpFail("비밀번호는 8~20자 영문과 숫자 조합이어야 합니다."))
            }
            return
        }

        if (!checkConfirmPw(password, confirmPw, confirmCheck)) {
            viewModelScope.launch {
                _signUpEvent.send(CheckSignUp.SignUpFail("비밀번호가 일치하지 않습니다."))
            }
            return
        }
        else {
            signUp(email, password, data)
        }
    }
}

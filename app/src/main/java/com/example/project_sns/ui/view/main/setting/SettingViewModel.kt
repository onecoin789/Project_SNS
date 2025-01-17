package com.example.project_sns.ui.view.main.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.databinding.FragmentCancelAccountBinding
import com.example.project_sns.domain.usecase.CancelAccountUseCase
import com.example.project_sns.domain.usecase.LogoutUseCase
import com.example.project_sns.ui.CurrentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val cancelAccountUseCase: CancelAccountUseCase
): ViewModel() {


    private val _cancelAccountResult = MutableLiveData<Boolean>()
    val cancelAccountResult: LiveData<Boolean> get() = _cancelAccountResult

    fun logout() {
        viewModelScope.launch {
            logoutUseCase
            CurrentUser.resetData()
        }
    }

    fun cancelAccount(uid: String, confirmEmail: String) {
        viewModelScope.launch {
            cancelAccountUseCase(uid = uid, confirmEmail = confirmEmail).collect { result ->
                _cancelAccountResult.value = result
            }
        }
    }
}
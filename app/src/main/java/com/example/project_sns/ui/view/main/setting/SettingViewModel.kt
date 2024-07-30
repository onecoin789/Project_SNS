package com.example.project_sns.ui.view.main.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.LogoutUseCase
import com.example.project_sns.ui.CurrentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
): ViewModel() {

    fun logout() {
        viewModelScope.launch {
            logoutUseCase
            CurrentUser.resetData()
        }
    }
}
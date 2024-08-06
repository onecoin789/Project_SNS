package com.example.project_sns.ui.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.GetCurrentUserDataUseCase
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.mapper.toModel
import com.example.project_sns.ui.CurrentUserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentUserDataUseCase: GetCurrentUserDataUseCase
): ViewModel() {

    private val _currentUserData = MutableStateFlow<CurrentUserModel?>(null)
    val currentUserData: StateFlow<CurrentUserModel?> get() = _currentUserData



    fun getCurrentUserData() {
        try {
            viewModelScope.launch {
                getCurrentUserDataUseCase().collect { userData ->
                    if (userData != null) {
                        _currentUserData.value = userData.toModel()
                        CurrentUser.userData = userData.toModel()
                    }
                }
            }
        } catch (e: Exception) {
            throw NullPointerException("${e}")
        }
    }


}
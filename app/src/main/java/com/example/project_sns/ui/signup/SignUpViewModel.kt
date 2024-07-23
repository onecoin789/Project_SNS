package com.example.project_sns.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {
    private val _userId = MutableLiveData<String>()
    val userId : LiveData<String> = _userId

    fun setUserId(id : String) {
        _userId.value = id
    }



}
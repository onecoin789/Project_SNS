package com.example.project_sns.ui

import com.example.project_sns.ui.model.CurrentUserModel

object CurrentUser {
    var userData: CurrentUserModel? = null

    fun resetData() {
        userData = null
    }

}
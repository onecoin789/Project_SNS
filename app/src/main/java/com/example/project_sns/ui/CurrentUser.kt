package com.example.project_sns.ui

import com.example.project_sns.ui.view.model.PostDataModel
import com.example.project_sns.ui.view.model.ReCommentDataModel
import com.example.project_sns.ui.view.model.UserDataModel

object CurrentUser {
    var userData: UserDataModel? = null

    fun resetData() {
        userData = null
    }
}

object CurrentPost {
    var postData: PostDataModel? = null

    var reCommentList: List<ReCommentDataModel>? = null
}
package com.example.project_sns.ui

import com.example.project_sns.ui.model.PostDataModel
import com.example.project_sns.ui.model.ReCommentDataModel
import com.example.project_sns.ui.model.ReCommentModel
import com.example.project_sns.ui.model.UserDataModel

object CurrentUser {
    var userData: UserDataModel? = null

    fun resetData() {
        userData = null
    }
}

object CurrentPost {
    var postData: PostDataModel? = null

    var reCommentList: List<ReCommentModel>? = null
}
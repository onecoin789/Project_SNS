package com.example.project_sns.ui

import com.example.project_sns.ui.view.model.CommentDataModel
import com.example.project_sns.ui.view.model.PostDataModel

object CurrentUser {
    var userData: CurrentUserModel? = null

    fun resetData() {
        userData = null
    }
}

object CurrentPost {
    var postData: PostDataModel? = null

    var commentData: List<CommentDataModel>? = null
}
package com.example.project_sns.ui

import com.example.project_sns.ui.view.model.CommentDataModel
import com.example.project_sns.ui.view.model.PostDataModel
import com.example.project_sns.ui.view.model.ReCommentDataModel

object CurrentUser {
    var userData: CurrentUserModel? = null

    fun resetData() {
        userData = null
    }
}

object CurrentPost {
    var postData: PostDataModel? = null

}
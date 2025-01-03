package com.example.project_sns.domain

enum class SearchViewType(val searchType: String) {
    USER_SEARCH("name"),
    POST_SEARCH("postText")
}
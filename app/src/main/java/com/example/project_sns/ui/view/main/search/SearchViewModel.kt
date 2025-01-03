package com.example.project_sns.ui.view.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.GetPostSearchResultUseCase
import com.example.project_sns.domain.usecase.GetUserSearchResultUseCase
import com.example.project_sns.ui.mapper.toPostListModel
import com.example.project_sns.ui.mapper.toUserDataListModel
import com.example.project_sns.ui.model.PostDataModel
import com.example.project_sns.ui.model.UserDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getUserSearchResultUseCase: GetUserSearchResultUseCase,
    private val getPostSearchResultUseCase: GetPostSearchResultUseCase
): ViewModel() {


    private val _userSearchResult = MutableLiveData<List<UserDataModel>>()
    val userSearchResult: LiveData<List<UserDataModel>> get() = _userSearchResult

    private val _postSearchResult = MutableLiveData<List<PostDataModel>>()
    val postSearchResult: LiveData<List<PostDataModel>> get() = _postSearchResult

    private val _query = MutableLiveData<String?>()
    val query: LiveData<String?> get() = _query


    fun clearSearchData() {
        _userSearchResult.value = emptyList()
        _postSearchResult.value = emptyList()
        _query.value = null
    }


    fun clearUserSearchResult() {
        _userSearchResult.value = emptyList()
    }

    fun clearPostSearchResult() {
        _postSearchResult.value = emptyList()
    }

    fun getQuery(query: String) {
        viewModelScope.launch {
            _query.value = query
        }
    }

    fun getUserSearchResult(query: String) {
        viewModelScope.launch {
            getUserSearchResultUseCase(query).collect { userSearchResult ->
                val userDataModel = userSearchResult.toUserDataListModel()
                _userSearchResult.value = userDataModel
            }
        }
    }

    fun getPostSearchResult(query: String) {
        viewModelScope.launch {
            getPostSearchResultUseCase(query).collect { postSearchResult ->
                val postDataModel = postSearchResult.toPostListModel()
                _postSearchResult.value = postDataModel
            }
        }
    }
}
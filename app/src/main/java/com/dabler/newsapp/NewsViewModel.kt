package com.dabler.newsapp

import androidx.lifecycle.*
import com.dabler.newsapp.network.ApiHelper
import com.dabler.newsapp.network.Article
import com.dabler.newsapp.network.RetrofitBuilder

class NewsViewModel : ViewModel() {

    var articles: LiveData<List<Article>> = liveData {
        val data = ApiHelper(RetrofitBuilder.apiService).getHeadlines().articles ?: listOf()
        emit(data)
    }
}
package com.dabler.newsapp.network

class ApiHelper(private val apiService: ApiService) {

    suspend fun getHeadlines() = apiService.getHeadlines()
}
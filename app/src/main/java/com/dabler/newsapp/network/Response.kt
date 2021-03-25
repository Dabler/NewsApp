package com.dabler.newsapp.network

data class Article(val source: Source?, val author: String?, val title: String?, val description: String?, val url: String?, val urlToImage: String?, val publishedAt: String?, val content: String?)

data class Base(val status: String?, val totalResults: Number?, val articles: List<Article>?)

data class Source(val id: String?, val name: String?)

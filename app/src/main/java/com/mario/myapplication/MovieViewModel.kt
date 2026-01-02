package com.mario.myapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class MovieViewModel : ViewModel() {
    private val apiKey = "0d23caaa3479b02511e1af2047fb4744"
    private val json = Json { ignoreUnknownKeys = true }
    
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val tmdbService = retrofit.create(TmdbService::class.java)

    var trendingMovies by mutableStateOf<List<TmdbMovie>>(emptyList())
    var popularMovies by mutableStateOf<List<TmdbMovie>>(emptyList())
    var topRatedMovies by mutableStateOf<List<TmdbMovie>>(emptyList())
    var featuredMovie by mutableStateOf<TmdbMovie?>(null)
    var isLoading by mutableStateOf(true)

    init {
        fetchMovies()
    }

    private fun fetchMovies() {
        viewModelScope.launch {
            try {
                val trending = tmdbService.getTrendingMovies(apiKey).results
                val popular = tmdbService.getPopularMovies(apiKey).results
                val topRated = tmdbService.getTopRatedMovies(apiKey).results

                trendingMovies = trending
                popularMovies = popular
                topRatedMovies = topRated
                featuredMovie = trending.randomOrNull()
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
                // Handle error
            }
        }
    }
}

package com.mario.myapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
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
    var searchResults by mutableStateOf<List<TmdbMovie>>(emptyList())
    var featuredMovie by mutableStateOf<TmdbMovie?>(null)
    
    var selectedMovie by mutableStateOf<TmdbMovie?>(null)
    var tvDetails by mutableStateOf<TvDetails?>(null)
    var episodes by mutableStateOf<List<TvEpisode>>(emptyList())
    var selectedSeason by mutableIntStateOf(1)
    
    var searchQuery by mutableStateOf("")
    var isSearching by mutableStateOf(false)
    var isSearchLoading by mutableStateOf(false)
    var isLoading by mutableStateOf(true)

    private var searchJob: Job? = null

    init {
        fetchMovies()
    }

    private fun fetchMovies() {
        viewModelScope.launch {
            try {
                val trending = tmdbService.getTrendingAll(apiKey).results
                val popular = tmdbService.getPopularMovies(apiKey).results
                val topRated = tmdbService.getTopRatedMovies(apiKey).results

                trendingMovies = trending
                popularMovies = popular
                topRatedMovies = topRated
                featuredMovie = trending.randomOrNull()
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        searchJob?.cancel()
        
        if (query.isBlank()) {
            searchResults = emptyList()
            isSearching = false
            isSearchLoading = false
            return
        }
        
        isSearching = true
        isSearchLoading = true
        
        searchJob = viewModelScope.launch {
            delay(600) // Debounce to save API calls
            try {
                val response = tmdbService.searchMulti(apiKey, query)
                searchResults = response.results
                    .filter { it.posterPath != null } // Only show things with posters
                    .sortedByDescending { it.popularity } // Keep popular on top
            } catch (e: Exception) {
                // Fail silently or log
            } finally {
                isSearchLoading = false
            }
        }
    }
    
    fun clearSearch() {
        searchQuery = ""
        searchResults = emptyList()
        isSearching = false
        isSearchLoading = false
    }

    fun selectMovie(movie: TmdbMovie?) {
        selectedMovie = movie
        tvDetails = null
        episodes = emptyList()
        selectedSeason = 1
        if (movie != null) {
            val isTv = movie.mediaType == "tv" || movie.firstAirDate != null
            if (isTv) {
                fetchTvDetails(movie.id)
            }
        }
    }

    private fun fetchTvDetails(tvId: Int) {
        viewModelScope.launch {
            try {
                val details = tmdbService.getTvDetails(tvId, apiKey)
                tvDetails = details
                if (details.numberOfSeasons > 0) {
                    fetchSeasonEpisodes(tvId, 1)
                }
            } catch (e: Exception) { }
        }
    }

    fun onSeasonChange(seasonNumber: Int) {
        selectedSeason = seasonNumber
        selectedMovie?.let {
            fetchSeasonEpisodes(it.id, seasonNumber)
        }
    }

    private fun fetchSeasonEpisodes(tvId: Int, seasonNumber: Int) {
        viewModelScope.launch {
            try {
                val seasonResponse = tmdbService.getTvSeason(tvId, seasonNumber, apiKey)
                episodes = seasonResponse.episodes
            } catch (e: Exception) {
                episodes = emptyList()
            }
        }
    }
}

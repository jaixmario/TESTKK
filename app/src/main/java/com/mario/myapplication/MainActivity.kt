package com.mario.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mario.myapplication.ui.theme.DarkBackground
import com.mario.myapplication.ui.theme.MyApplicationTheme
import com.mario.myapplication.ui.theme.PrimaryRed
import com.mario.myapplication.ui.theme.TextPrimary
import com.mario.myapplication.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                OTTApp()
            }
        }
    }
}

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
private const val BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/original"

@Composable
fun OTTApp(viewModel: MovieViewModel = viewModel()) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryRed)
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(
                        query = viewModel.searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange,
                        onClear = viewModel::clearSearch
                    )
                    
                    if (viewModel.isSearching) {
                        SearchResultsView(
                            movies = viewModel.searchResults,
                            isLoading = viewModel.isSearchLoading,
                            onMovieClick = { viewModel.selectMovie(it) }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 32.dp)
                        ) {
                            viewModel.featuredMovie?.let {
                                item { 
                                    FeaturedBanner(
                                        movie = it, 
                                        onDetailsClick = { viewModel.selectMovie(it) }
                                    ) 
                                }
                            }
                            item { 
                                MovieSection(
                                    title = "Trending Now", 
                                    movies = viewModel.trendingMovies,
                                    onMovieClick = { viewModel.selectMovie(it) }
                                ) 
                            }
                            item { 
                                MovieSection(
                                    title = "Popular Movies", 
                                    movies = viewModel.popularMovies,
                                    onMovieClick = { viewModel.selectMovie(it) }
                                ) 
                            }
                            item { 
                                MovieSection(
                                    title = "Top Rated", 
                                    movies = viewModel.topRatedMovies,
                                    onMovieClick = { viewModel.selectMovie(it) }
                                ) 
                            }
                        }
                    }
                }

                // Details Screen Overlay
                AnimatedVisibility(
                    visible = viewModel.selectedMovie != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    viewModel.selectedMovie?.let { movie ->
                        MovieDetailsScreen(
                            movie = movie,
                            tvDetails = viewModel.tvDetails,
                            episodes = viewModel.episodes,
                            selectedSeason = viewModel.selectedSeason,
                            onSeasonChange = viewModel::onSeasonChange,
                            onBack = { viewModel.selectMovie(null) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AURAFLIX",
                color = PrimaryRed,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp)),
            placeholder = { Text("Search movies & shows...", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClear) {
                        Icon(Icons.Default.Close, null, tint = TextSecondary)
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.1f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                disabledContainerColor = Color.White.copy(alpha = 0.1f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = PrimaryRed,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )
    }
}

@Composable
fun SearchResultsView(
    movies: List<TmdbMovie>,
    isLoading: Boolean,
    onMovieClick: (TmdbMovie) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Search Results",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            if (isLoading) {
                Spacer(modifier = Modifier.width(16.dp))
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = PrimaryRed)
            }
        }
        
        if (movies.isEmpty() && !isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No results found", color = TextSecondary)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(movies) { movie ->
                    MovieCard(movie, isGrid = true, onClick = { onMovieClick(movie) })
                }
            }
        }
    }
}

@Composable
fun FeaturedBanner(movie: TmdbMovie, onDetailsClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(550.dp)
    ) {
        AsyncImage(
            model = BACKDROP_BASE_URL + (movie.backdropPath ?: movie.posterPath),
            contentDescription = movie.title ?: movie.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            DarkBackground.copy(alpha = 0.5f),
                            DarkBackground
                        ),
                        startY = 400f
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(PrimaryRed, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("TRENDING", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                val date = movie.releaseDate ?: movie.firstAirDate ?: "N/A"
                val year = date.take(4).takeIf { it.isNotBlank() } ?: "N/A"
                Text(
                    text = "$year • Rating: ${String.format("%.1f", movie.voteAverage)}",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = movie.title ?: movie.name ?: "Unknown",
                color = TextPrimary,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 48.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = movie.overview ?: "",
                color = TextSecondary,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(48.dp).weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Play", color = Color.Black, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onDetailsClick,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(48.dp).weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                ) {
                    Icon(Icons.Default.Info, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Details", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MovieSection(
    title: String, 
    movies: List<TmdbMovie>,
    onMovieClick: (TmdbMovie) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 20.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "See All",
                color = PrimaryRed,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(movies) { movie ->
                MovieCard(movie, onClick = { onMovieClick(movie) })
            }
        }
    }
}

@Composable
fun MovieCard(
    movie: TmdbMovie, 
    isGrid: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(if (isGrid) 180.dp else 160.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isGrid) 260.dp else 240.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            AsyncImage(
                model = IMAGE_BASE_URL + movie.posterPath,
                contentDescription = movie.title ?: movie.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Star, null, tint = Color.Yellow, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format("%.1f", movie.voteAverage),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = movie.title ?: movie.name ?: "Unknown",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        val date = movie.releaseDate ?: movie.firstAirDate ?: "N/A"
        val year = date.take(4).takeIf { it.isNotBlank() } ?: "N/A"
        Text(
            text = year,
            color = TextSecondary,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

@Composable
fun MovieDetailsScreen(
    movie: TmdbMovie, 
    tvDetails: TvDetails?,
    episodes: List<TvEpisode>,
    selectedSeason: Int,
    onSeasonChange: (Int) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Backdrop
            Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                AsyncImage(
                    model = BACKDROP_BASE_URL + (movie.backdropPath ?: movie.posterPath),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, DarkBackground),
                                startY = 100f
                            )
                        )
                )
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
            }

            // Info
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = movie.title ?: movie.name ?: "Unknown",
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color.Yellow, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", movie.voteAverage),
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    val date = movie.releaseDate ?: movie.firstAirDate ?: "N/A"
                    Text(
                        text = date.take(4).takeIf { it.isNotBlank() } ?: "N/A",
                        color = TextSecondary
                    )
                    
                    // Show Type (Movie/TV)
                    Spacer(modifier = Modifier.width(16.dp))
                    val type = if (movie.title != null) "MOVIE" else "TV SHOW"
                    Text(
                        text = type,
                        color = PrimaryRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // TV Episode/Season info
                if (tvDetails != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        InfoBadge(text = "${tvDetails.numberOfSeasons} Seasons")
                        InfoBadge(text = "${tvDetails.numberOfEpisodes} Episodes")
                        InfoBadge(text = tvDetails.status)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = tvDetails.genres.joinToString(" • ") { it.name },
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    
                    // Season Selector
                    if (tvDetails.numberOfSeasons > 1) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Select Season",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items((1..tvDetails.numberOfSeasons).toList()) { seasonNum ->
                                FilterChip(
                                    selected = selectedSeason == seasonNum,
                                    onClick = { onSeasonChange(seasonNum) },
                                    label = { Text("Season $seasonNum") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = Color.White.copy(alpha = 0.1f),
                                        labelColor = TextSecondary,
                                        selectedContainerColor = PrimaryRed,
                                        selectedLabelColor = Color.White
                                    ),
                                    border = null
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Watch Now", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Storyline",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = movie.overview ?: "",
                    color = TextSecondary,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
                
                // Episodes List
                if (episodes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Episodes (Season $selectedSeason)",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    episodes.forEach { episode ->
                        EpisodeItem(episode)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun EpisodeItem(episode: TvEpisode) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(140.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
        ) {
            AsyncImage(
                model = IMAGE_BASE_URL + episode.stillPath,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(32.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
            Text(
                text = "${episode.episodeNumber}. ${episode.name ?: "Unknown"}",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${episode.runtime ?: "N/A"} min • ${episode.airDate ?: ""}",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = episode.overview ?: "",
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun InfoBadge(text: String) {
    Surface(
        color = Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

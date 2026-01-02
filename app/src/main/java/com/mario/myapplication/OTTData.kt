package com.mario.myapplication

data class Movie(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val category: String,
    val rating: String,
    val year: String,
    val quality: String = "HD",
    val isNew: Boolean = false,
    val description: String = ""
)

object MovieRepository {
    val featuredMovie = Movie(
        1,
        "Interstellar",
        "https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=2094&auto=format&fit=crop",
        "Sci-Fi",
        "8.7",
        "2014",
        "4K",
        false,
        "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival."
    )

    val trendingMovies = listOf(
        Movie(2, "Inception", "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?q=80&w=2070&auto=format&fit=crop", "Action", "8.8", "2010", "4K", true),
        Movie(3, "The Dark Knight", "https://images.unsplash.com/photo-1478720568477-152d9b164e26?q=80&w=2070&auto=format&fit=crop", "Action", "9.0", "2008", "HD"),
        Movie(4, "Pulp Fiction", "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?q=80&w=2070&auto=format&fit=crop", "Crime", "8.9", "1994", "HD", true),
        Movie(5, "The Matrix", "https://images.unsplash.com/photo-1626814026160-2237a95fc5a0?q=80&w=2070&auto=format&fit=crop", "Sci-Fi", "8.7", "1999", "4K")
    )

    val continueWatching = listOf(
        Movie(6, "Stranger Things", "https://images.unsplash.com/photo-1533928298208-27ff66555d8d?q=80&w=2070&auto=format&fit=crop", "Sci-Fi", "8.7", "2016", "4K", true),
        Movie(7, "The Witcher", "https://images.unsplash.com/photo-1514539079130-25950c84af65?q=80&w=2069&auto=format&fit=crop", "Fantasy", "8.1", "2019", "HD"),
        Movie(8, "The Boys", "https://images.unsplash.com/photo-1509248961158-e54f6934749c?q=80&w=2074&auto=format&fit=crop", "Action", "8.7", "2019", "4K", true)
    )
    
    val recommended = listOf(
        Movie(9, "Breaking Bad", "https://images.unsplash.com/photo-1585951237318-9ea5e175b891?q=80&w=2070&auto=format&fit=crop", "Drama", "9.5", "2008", "4K"),
        Movie(10, "Chernobyl", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=2069&auto=format&fit=crop", "Drama", "9.4", "2019", "HD"),
        Movie(11, "The Crown", "https://images.unsplash.com/photo-1542204172-55af3a716418?q=80&w=1974&auto=format&fit=crop", "History", "8.6", "2016", "4K")
    )
}

package com.kybers.xtream.data.model

data class XtreamContent(
    val channels: List<Channel> = emptyList(),
    val movies: List<Movie> = emptyList(),
    val series: List<Series> = emptyList(),
    val cacheTimestamp: Long = System.currentTimeMillis()
) {
    fun isExpired(): Boolean {
        val twelveHours = 12 * 60 * 60 * 1000L // 12 horas en milisegundos
        return System.currentTimeMillis() - cacheTimestamp > twelveHours
    }
}

data class Channel(
    val id: String,
    val name: String,
    val streamUrl: String,
    val category: String,
    val logo: String? = null
)

data class Movie(
    val id: String,
    val name: String,
    val streamUrl: String,
    val category: String,
    val cover: String? = null,
    val tmdbId: String? = null,
    val year: String? = null,
    val plot: String? = null
)

data class Series(
    val id: String,
    val name: String,
    val category: String,
    val cover: String? = null,
    val tmdbId: String? = null,
    val year: String? = null,
    val plot: String? = null,
    val episodes: List<Episode> = emptyList()
)

data class Episode(
    val id: String,
    val title: String,
    val streamUrl: String,
    val seasonNumber: Int,
    val episodeNumber: Int
)
package com.kybers.xtream.data.api.model

import com.google.gson.annotations.SerializedName

// Authentication response
data class PlayerApiResponse(
    @SerializedName("user_info") val userInfo: UserInfo?,
    @SerializedName("server_info") val serverInfo: ServerInfo?
)

data class UserInfo(
    @SerializedName("username") val username: String?,
    @SerializedName("password") val password: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("auth") val auth: Int?,
    @SerializedName("status") val status: String?,
    @SerializedName("exp_date") val expDate: String?,
    @SerializedName("is_trial") val isTrial: String?,
    @SerializedName("active_cons") val activeCons: String?,
    @SerializedName("created_at") val createdAt: String?
)

data class ServerInfo(
    @SerializedName("url") val url: String?,
    @SerializedName("port") val port: String?,
    @SerializedName("https_port") val httpsPort: String?,
    @SerializedName("server_protocol") val serverProtocol: String?,
    @SerializedName("rtmp_port") val rtmpPort: String?,
    @SerializedName("timezone") val timezone: String?,
    @SerializedName("timestamp_now") val timestampNow: Long?
)

// Live TV Categories
data class LiveCategory(
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("parent_id") val parentId: Int?
)

// Live TV Streams
data class LiveStream(
    @SerializedName("num") val num: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("stream_type") val streamType: String?,
    @SerializedName("stream_id") val streamId: Int?,
    @SerializedName("stream_icon") val streamIcon: String?,
    @SerializedName("epg_channel_id") val epgChannelId: String?,
    @SerializedName("added") val added: String?,
    @SerializedName("is_adult") val isAdult: String?,
    @SerializedName("category_name") val categoryName: String?,
    @SerializedName("category_id") val categoryId: String?,
    @SerializedName("tv_archive") val tvArchive: Int?,
    @SerializedName("direct_source") val directSource: String?,
    @SerializedName("tv_archive_duration") val tvArchiveDuration: String?
)

// VOD Categories
data class VodCategory(
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("parent_id") val parentId: Int?
)

// VOD Streams (Movies)
data class VodStream(
    @SerializedName("num") val num: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("stream_type") val streamType: String?,
    @SerializedName("stream_id") val streamId: Int?,
    @SerializedName("stream_icon") val streamIcon: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("rating_5based") val rating5based: Double?,
    @SerializedName("added") val added: String?,
    @SerializedName("category_name") val categoryName: String?,
    @SerializedName("category_id") val categoryId: String?,
    @SerializedName("container_extension") val containerExtension: String?,
    @SerializedName("custom_sid") val customSid: String?,
    @SerializedName("direct_source") val directSource: String?
)

// VOD Info (Movie details)
data class VodInfo(
    @SerializedName("info") val info: MovieInfo?,
    @SerializedName("movie_data") val movieData: MovieData?
)

data class MovieInfo(
    @SerializedName("tmdb_id") val tmdbId: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("o_name") val originalName: String?,
    @SerializedName("cover_big") val coverBig: String?,
    @SerializedName("movie_image") val movieImage: String?,
    @SerializedName("releasedate") val releaseDate: String?,
    @SerializedName("episode_run_time") val episodeRunTime: String?,
    @SerializedName("youtube_trailer") val youtubeTrailer: String?,
    @SerializedName("director") val director: String?,
    @SerializedName("actors") val actors: String?,
    @SerializedName("cast") val cast: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("plot") val plot: String?,
    @SerializedName("age") val age: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("genre") val genre: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("duration") val duration: String?,
    @SerializedName("video") val video: VideoInfo?,
    @SerializedName("audio") val audio: AudioInfo?
)

data class VideoInfo(
    @SerializedName("index") val index: Int?,
    @SerializedName("codec_name") val codecName: String?,
    @SerializedName("codec_long_name") val codecLongName: String?,
    @SerializedName("profile") val profile: String?,
    @SerializedName("codec_type") val codecType: String?,
    @SerializedName("codec_tag_string") val codecTagString: String?,
    @SerializedName("codec_tag") val codecTag: String?,
    @SerializedName("width") val width: Int?,
    @SerializedName("height") val height: Int?,
    @SerializedName("coded_width") val codedWidth: Int?,
    @SerializedName("coded_height") val codedHeight: Int?
)

data class AudioInfo(
    @SerializedName("index") val index: Int?,
    @SerializedName("codec_name") val codecName: String?,
    @SerializedName("codec_long_name") val codecLongName: String?,
    @SerializedName("profile") val profile: String?,
    @SerializedName("codec_type") val codecType: String?,
    @SerializedName("codec_tag_string") val codecTagString: String?,
    @SerializedName("codec_tag") val codecTag: String?
)

data class MovieData(
    @SerializedName("stream_id") val streamId: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("added") val added: String?,
    @SerializedName("category_id") val categoryId: String?,
    @SerializedName("container_extension") val containerExtension: String?,
    @SerializedName("custom_sid") val customSid: String?,
    @SerializedName("direct_source") val directSource: String?
)

// Series Categories
data class SeriesCategory(
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("parent_id") val parentId: Int?
)

// Series Streams
data class SeriesStream(
    @SerializedName("num") val num: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("series_id") val seriesId: Int?,
    @SerializedName("cover") val cover: String?,
    @SerializedName("plot") val plot: String?,
    @SerializedName("cast") val cast: String?,
    @SerializedName("director") val director: String?,
    @SerializedName("genre") val genre: String?,
    @SerializedName("releaseDate") val releaseDate: String?,
    @SerializedName("last_modified") val lastModified: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("rating_5based") val rating5based: Double?,
    @SerializedName("backdrop_path") val backdropPath: List<String>?,
    @SerializedName("youtube_trailer") val youtubeTrailer: String?,
    @SerializedName("episode_run_time") val episodeRunTime: String?,
    @SerializedName("category_id") val categoryId: String?,
    @SerializedName("category_ids") val categoryIds: List<Int>?
)

// Series Info (Series details with episodes)
data class SeriesInfo(
    @SerializedName("info") val info: SeriesInfoData?,
    @SerializedName("seasons") val seasons: List<SeriesSeason>?
)

data class SeriesInfoData(
    @SerializedName("name") val name: String?,
    @SerializedName("cover") val cover: String?,
    @SerializedName("plot") val plot: String?,
    @SerializedName("cast") val cast: String?,
    @SerializedName("director") val director: String?,
    @SerializedName("genre") val genre: String?,
    @SerializedName("releaseDate") val releaseDate: String?,
    @SerializedName("last_modified") val lastModified: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("rating_5based") val rating5based: Double?,
    @SerializedName("backdrop_path") val backdropPath: List<String>?,
    @SerializedName("youtube_trailer") val youtubeTrailer: String?,
    @SerializedName("episode_run_time") val episodeRunTime: String?,
    @SerializedName("category_id") val categoryId: String?
)

data class SeriesSeason(
    @SerializedName("season_number") val seasonNumber: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("episode_count") val episodeCount: Int?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("air_date") val airDate: String?,
    @SerializedName("cover") val cover: String?,
    @SerializedName("cover_big") val coverBig: String?
)

// Episodes for a specific season
data class SeriesEpisodes(
    @SerializedName("episodes") val episodes: Map<String, List<SeriesEpisode>>?
)

data class SeriesEpisode(
    @SerializedName("id") val id: String?,
    @SerializedName("episode_num") val episodeNum: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("container_extension") val containerExtension: String?,
    @SerializedName("info") val info: EpisodeInfo?,
    @SerializedName("custom_sid") val customSid: String?,
    @SerializedName("added") val added: String?,
    @SerializedName("season") val season: Int?,
    @SerializedName("direct_source") val directSource: String?
)

data class EpisodeInfo(
    @SerializedName("tmdb_id") val tmdbId: String?,
    @SerializedName("releasedate") val releaseDate: String?,
    @SerializedName("plot") val plot: String?,
    @SerializedName("duration_secs") val durationSecs: String?,
    @SerializedName("duration") val duration: String?,
    @SerializedName("movie_image") val movieImage: String?,
    @SerializedName("rating") val rating: String?
)
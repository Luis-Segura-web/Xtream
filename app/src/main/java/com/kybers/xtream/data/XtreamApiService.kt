package com.kybers.xtream.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface XtreamApiService {
    
    @GET("player_api.php")
    suspend fun authenticate(
        @Query("username") username: String,
        @Query("password") password: String
    ): AuthResponse
    
    @GET("player_api.php")
    suspend fun getChannels(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_live_streams"
    ): List<Any>
    
    @GET("player_api.php")
    suspend fun getMovies(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_vod_streams"
    ): List<Any>
    
    @GET("player_api.php")
    suspend fun getSeries(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_series"
    ): List<Any>
    
    companion object {
        fun create(baseUrl: String): XtreamApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            
            return retrofit.create(XtreamApiService::class.java)
        }
    }
}

data class AuthResponse(
    val user_info: UserInfo?,
    val server_info: ServerInfo?
)

data class UserInfo(
    val username: String,
    val password: String,
    val message: String?,
    val auth: Int,
    val status: String,
    val exp_date: String?,
    val is_trial: String?,
    val active_cons: String?,
    val created_at: String?,
    val max_connections: String?
)

data class ServerInfo(
    val url: String,
    val port: String,
    val https_port: String?,
    val server_protocol: String,
    val rtmp_port: String?,
    val timezone: String?
)
package com.chillcoding.ilove.network

import com.chillcoding.ilove.model.Score
import retrofit2.Call
import retrofit2.http.GET


/**
 * Created by macha on 24/11/2017.
 */
interface GameService {
    @GET("/api/scores")
    fun listScores(): Call<List<Score>>
}
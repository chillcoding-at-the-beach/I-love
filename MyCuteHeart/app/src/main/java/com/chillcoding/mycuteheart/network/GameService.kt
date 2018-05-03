package com.chillcoding.mycuteheart.network

import com.chillcoding.mycuteheart.model.Score
import retrofit2.Call
import retrofit2.http.GET


/**
 * Created by macha on 24/11/2017.
 */
interface GameService {
    @GET("/api/scores")
    fun listScores(): Call<List<Score>>
}
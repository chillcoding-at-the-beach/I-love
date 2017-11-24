package com.chillcoding.mycuteheart.view.fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chillcoding.mycuteheart.R
import com.chillcoding.mycuteheart.model.Score
import com.chillcoding.mycuteheart.network.GameService
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


/**
 * Created by macha on 02/08/2017.
 */
class TopScoresFragment : Fragment(), AnkoLogger {

    private val url = "http://192.168.0.11:8990/"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view = inflater?.inflate(R.layout.fragment_top_scores, container, false)
        info("IN TOP SCORE FRAGMENT!")
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        val service = retrofit.create(GameService::class.java)

        val scoreRequest = service.listScores()

        scoreRequest.enqueue(object : Callback<List<Score>> {
            override fun onResponse(call: Call<List<Score>>, response: Response<List<Score>>) {
                val allScore = response.body()
                if (allScore != null) {
                    info("HERE is ALL SCORE FROM LOCAL SERVER:")
                    for (s in allScore)
                        info(" one score : ${s.pseudo} : ${s.score} ")
                }
            }
            override fun onFailure(call: Call<List<Score>>, t: Throwable) {
                error("KO")
            }
        })
        return view!!
    }
}

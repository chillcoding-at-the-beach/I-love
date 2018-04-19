package com.chillcoding.mycuteheart.view.fragment

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chillcoding.mycuteheart.R
import com.chillcoding.mycuteheart.extension.hasConnectivity
import com.chillcoding.mycuteheart.model.Score
import com.chillcoding.mycuteheart.network.GameService
import com.chillcoding.mycuteheart.view.adapter.ScoreListAdapter
import kotlinx.android.synthetic.main.fragment_top_scores.*
import org.jetbrains.anko.*
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

    var allTopScores = listOf<Score>(Score("Marina", 51032), Score("Macha", 4973), Score("Jean-Michel", 3542), Score("Carole", 3333), Score("Zozo", 2203), Score("Lola", 2199), Score("Léo", 999), Score("Matéo", 998), Score("Léa", 997), Score("Joe", 995))

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view = inflater?.inflate(R.layout.fragment_top_scores, container, false)


        if (activity.hasConnectivity()) {
            val retrofit = Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()

            val service = retrofit.create(GameService::class.java)

            val scoreRequest = service.listScores()

            scoreRequest.enqueue(object : Callback<List<Score>> {
                override fun onResponse(call: Call<List<Score>>, response: Response<List<Score>>) {
                    allTopScores = response.body()!!
                    if (allTopScores != null) {
                        info("HERE is ALL SCORE FROM LOCAL SERVER:")
                        for (s in allTopScores)
                            info(" one point : ${s.pseudo} : ${s.point} ")
                    }
                }

                override fun onFailure(call: Call<List<Score>>, t: Throwable) {
                    longToast(R.string.server_error_text)
                }
            })
        } else
            alert(R.string.text_no_internet_in_top_scores) {
                yesButton { }
            }.show()
        return view!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        topScoreRecycler.layoutManager = LinearLayoutManager(activity)
        topScoreRecycler.adapter = ScoreListAdapter(allTopScores)
    }
}

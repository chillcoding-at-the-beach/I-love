package com.chillcoding.ilove.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chillcoding.ilove.R
import com.chillcoding.ilove.extension.hasConnectivity
import com.chillcoding.ilove.model.Score
import com.chillcoding.ilove.model.User
import com.chillcoding.ilove.network.GameService
import com.chillcoding.ilove.view.adapter.ScoreListAdapter
import kotlinx.android.synthetic.main.fragment_top_scores.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.alert
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


/**
 * Created by macha on 02/08/2017.
 */


class TopScoresFragment : Fragment(), AnkoLogger {

    private val url = "https://i-love-api.herokuapp.com/"

    var allTopScores = listOf<Score>(Score(User("Marina"), 436), Score(User("Macha"), 278), Score(User("Jean-Michel"), 251), Score(User("Caroline"), 245), Score(User("Zoé"), 233), Score(User("Lola"), 221), Score(User("Léo"), 197), Score(User("Matéo"), 185), Score(User("Léa"), 179), Score(User("Joe"), 164))

    private var adapter = ScoreListAdapter(allTopScores)

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
                    if (response.body() != null) {
                        allTopScores = response.body()!!
                        updateUI()
                    }
                }

                override fun onFailure(call: Call<List<Score>>, t: Throwable) {
                    if (activity != null)
                        longToast(R.string.server_error_text)
                }
            })
        } else
            alert(R.string.text_no_internet_in_top_scores) {
                yesButton { }
            }.show()
        return view!!
    }

    private fun updateUI() {
        adapter.setScores(allTopScores)
        adapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        topScoreRecycler.layoutManager = LinearLayoutManager(activity)
        topScoreRecycler.adapter = adapter
    }
}

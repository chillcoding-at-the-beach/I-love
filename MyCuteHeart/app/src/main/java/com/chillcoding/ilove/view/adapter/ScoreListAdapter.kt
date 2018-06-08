package com.chillcoding.ilove.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.chillcoding.ilove.App
import com.chillcoding.ilove.R
import com.chillcoding.ilove.extension.inflate
import com.chillcoding.ilove.model.Score
import kotlinx.android.synthetic.main.item_score.view.*
import java.util.*

/**
 * Created by macha on 19/04/2018.
 */
class ScoreListAdapter(var items: List<Score>) : RecyclerView.Adapter<ScoreListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_score))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindScore(items[position])
    }

    fun setScores(scores: List<Score>) {
        items = scores
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val random = Random()
        fun bindScore(score: Score) {
            with(itemView) {
                scoreUserImg.setColorFilter(App.sColors[random.nextInt(App.sColors.size)])
                scoreRanking.text = getRanking(adapterPosition)
                scorePoint.text = score.point.toString()
                scorePseudo.text = score.user.username
            }
        }

        private fun getRanking(rank: Int): String {
            when (rank) {
                0 -> return "1st"
                1 -> return "2nd"
                2 -> return "3rd"
                else -> return "${rank + 1}th"
            }
        }
    }
}
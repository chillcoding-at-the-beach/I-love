package com.chillcoding.mycuteheart.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.chillcoding.mycuteheart.App
import com.chillcoding.mycuteheart.R
import com.chillcoding.mycuteheart.extension.inflate
import com.chillcoding.mycuteheart.model.Score
import kotlinx.android.synthetic.main.item_score.view.*
import java.util.*

/**
 * Created by macha on 19/04/2018.
 */
class ScoreListAdapter(val items: List<Score>) : RecyclerView.Adapter<ScoreListAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_score))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindScore(items[position])
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val random= Random()
        fun bindScore(score: Score) {
            with(itemView) {
                scoreUserImg.setColorFilter(App.sColors[random.nextInt(App.sColors.size)])
                scoreRanking.text = getRanking(adapterPosition)
                scorePoint.text = score.point.toString()
                scorePseudo.text = score.pseudo
            }
        }

        private fun getRanking(rank: Int): String {
            when (rank) {
                0 -> return "1st"
                1 -> return "2nd"
                2 -> return "3rd"
                else -> return "${rank+1}th"
            }
        }
    }
}
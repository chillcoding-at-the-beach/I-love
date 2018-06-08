package com.chillcoding.ilove.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.chillcoding.ilove.App
import com.chillcoding.ilove.R
import com.chillcoding.ilove.extension.inflate
import com.chillcoding.ilove.model.Award
import com.chillcoding.ilove.view.activity.AwardDetailActivity
import kotlinx.android.synthetic.main.item_award.view.*
import org.jetbrains.anko.startActivity

/**
 * Created by macha on 21/09/2017.
 */
class AwardListAdapter(val items: Array<Award>) : RecyclerView.Adapter<AwardListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_award))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMyAwards(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindMyAwards(award: Award) {
            with(award) {
                if (score != 0) {
                    itemView.awardEmpty.visibility = View.INVISIBLE
                    itemView.award.visibility = View.VISIBLE
                    itemView.awardName.text = "${view.context.getString(R.string.word_best_score)}: $score"
                    itemView.awardImg.setImageResource(img)
                    itemView.awardInfo.text = "${view.context.getString(R.string.word_award)}: $name"
                    itemView.awardPlayIcon.setColorFilter(App.sColors[4])
                }
                when {
                    mode > 0 -> {
                        itemView.setOnClickListener { itemView.context.startActivity<AwardDetailActivity>(App.AWARD_MODE to mode) }
                        itemView.awardLoveIcon.setColorFilter(App.sColors[2])
                    }
                    mode > 1 -> itemView.awardDownIcon.setColorFilter(App.sColors[3])
                    mode > 2 -> itemView.awardIcon.setColorFilter(App.sColors[5])
                }
            }
        }
    }

}

package com.chillcoding.mycuteheart.view.adapter

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chillcoding.mycuteheart.App
import com.chillcoding.mycuteheart.R
import com.chillcoding.mycuteheart.extension.inflate
import com.chillcoding.mycuteheart.model.Award
import kotlinx.android.synthetic.main.item_awards.view.*

/**
 * Created by macha on 21/09/2017.
 */
class AwardListAdapter(val items: Array<Award>) : RecyclerView.Adapter<AwardListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_awards))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMyAwards(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindMyAwards(myAwards: Award) {
            with(myAwards) {
                if (mode > 0) {
                    itemView.awardEmpty.visibility = View.INVISIBLE
                    itemView.award.visibility = View.VISIBLE
                    itemView.awardImg.setImageResource(img)
                    itemView.awardName.text = "${view.context.getString(R.string.word_best_score)}: $score"
                    itemView.awardInfo.text = "${view.context.getString(R.string.word_mode)}: $name"
                    if (mode > 1)
                        itemView.awardPlayIcon.setColorFilter(App.sColors[4])
                    if (mode > 2)
                        itemView.awardLoveIcon.setColorFilter(App.sColors[2])
                    if (mode > 3)
                        itemView.awardDownIcon.setColorFilter(App.sColors[3])
                    if (mode > 4)
                        itemView.awardIcon.setColorFilter(App.sColors[5])

                }
            }
        }
    }

}

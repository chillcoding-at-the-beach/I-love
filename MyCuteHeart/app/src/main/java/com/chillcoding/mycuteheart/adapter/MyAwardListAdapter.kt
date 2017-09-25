package com.chillcoding.mycuteheart.adapter

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chillcoding.mycuteheart.MyApp
import com.chillcoding.mycuteheart.R
import com.chillcoding.mycuteheart.model.MyAward
import kotlinx.android.synthetic.main.item_awards.view.*

/**
 * Created by macha on 21/09/2017.
 */
class MyAwardListAdapter(val items: Array<MyAward>) : RecyclerView.Adapter<MyAwardListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_awards))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMyAwards(items[position])
    }

    override fun getItemCount(): Int = items.size


    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindMyAwards(myAwards: MyAward) {
            with(myAwards) {
                if (mode > 0) {
                    itemView.awardEmpty.visibility = View.INVISIBLE
                    itemView.award.visibility = View.VISIBLE
                    itemView.awardImg.setImageResource(img)
                    itemView.awardName.text = "${view.context.getString(R.string.word_best_score)}: $score"
                    itemView.awardInfo.text = "${view.context.getString(R.string.word_mode)}: $name"
                    if (mode > 1)
                        itemView.awardPlayIcon.setColorFilter(MyApp.sColors[4])
                    if (mode > 2)
                        itemView.awardLoveIcon.setColorFilter(MyApp.sColors[2])
                    if (mode > 3)
                        itemView.awardDownIcon.setColorFilter(MyApp.sColors[3])
                    if (mode > 4)
                        itemView.awardIcon.setColorFilter(MyApp.sColors[5])

                }
            }
        }
    }

}

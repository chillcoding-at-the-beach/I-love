package com.chillcoding.ilove.view.adapter

import androidx.emoji.text.EmojiCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.chillcoding.ilove.App
import com.chillcoding.ilove.R
import com.chillcoding.ilove.event.AwardsEvent
import com.chillcoding.ilove.extension.inflate
import com.chillcoding.ilove.model.Award
import com.chillcoding.ilove.view.activity.PurchaseActivity
import com.eightbitlab.rxbus.Bus
import kotlinx.android.synthetic.main.item_award.view.*
import org.jetbrains.anko.startActivity

/**
 * Created by macha on 21/09/2017.
 */
class AwardListAdapter(val items: Array<Award>, val isAwards: Boolean) : RecyclerView.Adapter<AwardListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_award), isAwards)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMyAwards(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(val view: View, val isPremium: Boolean) : RecyclerView.ViewHolder(view) {

        fun bindMyAwards(award: Award) {
            with(award) {
                if (level > -1) {
                    itemView.awardEmpty.visibility = View.INVISIBLE
                    itemView.award.visibility = View.VISIBLE
                    val awardsText = EmojiCompat.get().process("${view.context.getString(R.string.word_award)} ${App.awardsTitle[level]}")
                    itemView.awardName.text = awardsText
                    if (score != 0)
                        itemView.awardInfo.text = "${view.context.getString(R.string.word_best_score)}: $score"
                    else
                        itemView.awardInfo.text = "${view.context.resources.getStringArray(R.array.text_love)[4]}"
                    itemView.awardImg.setImageResource(img)
                }

                if (level > -1) {
                    itemView.setOnClickListener { Bus.send(AwardsEvent(level)) }
                    itemView.awardPlayIcon.setColorFilter(App.sColors[1])
                }
                if (level > 0)
                    itemView.awardLoveIcon.setColorFilter(App.sColors[7])
                if (level > 1) {
                    if (!isPremium) {
                        itemView.awardInfo.text = "${view.context.getString(R.string.get_premium_text)}"
                        itemView.awardImg.setImageResource(R.drawable.ic_menu_awards)
                        itemView.setOnClickListener { itemView.context.startActivity<PurchaseActivity>() }
                    }
                    itemView.awardDownIcon.setColorFilter(App.sColors[10])
                }
                if (level > 2)
                    itemView.awardIcon.setColorFilter(App.sColors[5])
            }
        }
    }
}

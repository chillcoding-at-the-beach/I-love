package com.chillcoding.ilove.view.fragment

import androidx.fragment.app.Fragment
import android.os.Bundle
import androidx.emoji.text.EmojiCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chillcoding.ilove.App
import com.chillcoding.ilove.App.Companion.AWARD_LIST_SIZE
import com.chillcoding.ilove.R
import com.chillcoding.ilove.extension.DelegatesExt
import com.chillcoding.ilove.model.Award
import com.chillcoding.ilove.view.adapter.AwardListAdapter
import kotlinx.android.synthetic.main.fragment_awards.*

/**
 * Created by macha on 21/09/2017.
 */
class AwardsFragment : Fragment() {

    private var items = Array<Award>(AWARD_LIST_SIZE + 1, { Award() })

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater?.inflate(R.layout.fragment_awards, container, false)
        val bestScore: Int by DelegatesExt.preference(activity, App.PREF_BEST_SCORE, 0)
        val awardLevel: Int by DelegatesExt.preference(activity, App.PREF_AWARD_LEVEL, -1)

        for (k in 0..(awardLevel - 1)) {
            val awardTitle = EmojiCompat.get().process("${App.awardsTitle[k]}")
            items[k] = Award(App.sAwardImg[k], awardTitle, 0, k)
        }
        if (awardLevel > -1) {
            val awardTitle = EmojiCompat.get().process("${App.awardsTitle[awardLevel]}")
            items[awardLevel] = Award(App.sAwardImg[awardLevel], awardTitle, bestScore, awardLevel)
        }
        if (awardLevel > 1)
            items[1].score = bestScore
        return view!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        val isPremium: Boolean by DelegatesExt.preference(activity, App.PREF_PREMIUM, false)
        val isAwards: Boolean by DelegatesExt.preference(activity, App.PREF_UNLIMITED_AWARDS, false)
        awardsList.layoutManager = LinearLayoutManager(activity)
        awardsList.adapter = AwardListAdapter(items, isPremium || isAwards)
    }
}

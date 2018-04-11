package com.chillcoding.mycuteheart.view.fragment

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chillcoding.mycuteheart.App
import com.chillcoding.mycuteheart.R
import com.chillcoding.mycuteheart.view.adapter.AwardListAdapter
import com.chillcoding.mycuteheart.extension.DelegatesExt
import com.chillcoding.mycuteheart.model.Award
import kotlinx.android.synthetic.main.fragment_my_awards.*

/**
 * Created by macha on 21/09/2017.
 */
class AwardsFragment : Fragment() {

    companion object {
        val awardListSize = 7
    }

    private var items = Array<Award>(awardListSize, { Award() })

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view = inflater?.inflate(R.layout.fragment_awards, container, false)
        val bestScore: Int by DelegatesExt.preference(activity, App.PREF_BEST_SCORE, 0)
        val awardLevel: Int by DelegatesExt.preference(activity, App.PREF_AWARD_LEVEL, 1)

        for (k in 0..(awardLevel - 2)) {
            items[k] = Award(R.drawable.ic_menu_awards, resources.getStringArray(R.array.word_mode)[k], k * App.SCORE_PER_AWARD, k + 1)
        }
        items[awardLevel - 1] = Award(R.drawable.ic_menu_awards, resources.getStringArray(R.array.word_mode)[awardLevel - 1], bestScore, awardLevel)
        return view!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        awardsList.layoutManager = LinearLayoutManager(activity)
        awardsList.adapter = AwardListAdapter(items)
    }
}

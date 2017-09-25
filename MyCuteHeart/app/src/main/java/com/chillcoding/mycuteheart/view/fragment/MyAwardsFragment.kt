package com.chillcoding.mycuteheart.view.fragment

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chillcoding.mycuteheart.MyApp
import com.chillcoding.mycuteheart.R
import com.chillcoding.mycuteheart.adapter.MyAwardListAdapter
import com.chillcoding.mycuteheart.extension.DelegatesExt
import com.chillcoding.mycuteheart.model.MyAward
import kotlinx.android.synthetic.main.fragment_my_awards.*

/**
 * Created by macha on 21/09/2017.
 */
class MyAwardsFragment : Fragment() {

    companion object {
        val awardListSize = 7
    }

    private var items = Array<MyAward>(awardListSize, { MyAward() })

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view = inflater?.inflate(R.layout.fragment_my_awards, container, false)
        val bestScore: Int by DelegatesExt.preference(activity, MyApp.PREF_BEST_SCORE, 0)
        val awardLevel: Int by DelegatesExt.preference(activity, MyApp.PREF_AWARD_LEVEL, 1)

        for (k in 0..(awardLevel - 2)) {
            items[k] = MyAward(R.drawable.ic_menu_awards, resources.getStringArray(R.array.word_mode)[k], k * MyApp.SCORE_PER_AWARD, k + 1)
        }
        items[awardLevel - 1] = MyAward(R.drawable.ic_menu_awards, resources.getStringArray(R.array.word_mode)[awardLevel - 1], bestScore, awardLevel)
        return view!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        awardsList.layoutManager = LinearLayoutManager(activity)
        awardsList.adapter = MyAwardListAdapter(items)
    }
}

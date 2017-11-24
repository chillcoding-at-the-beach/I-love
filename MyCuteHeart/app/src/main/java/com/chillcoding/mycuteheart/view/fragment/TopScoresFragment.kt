package com.chillcoding.mycuteheart.view.fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chillcoding.mycuteheart.R
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


/**
 * Created by macha on 02/08/2017.
 */
class TopScoresFragment : Fragment(), AnkoLogger {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view = inflater?.inflate(R.layout.fragment_top_scores, container, false)
        info("IN TOP SCORE FRAGMENT!")
        return view!!
    }
}

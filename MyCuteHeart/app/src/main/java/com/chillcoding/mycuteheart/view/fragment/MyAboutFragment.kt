package com.chillcoding.mycuteheart.view.fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chillcoding.mycuteheart.R

/**
 * Created by macha on 02/08/2017.
 */
class MyAboutFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view = inflater?.inflate(R.layout.fragment_my_about, container, false)
        return view!!
    }
}

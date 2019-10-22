package com.chillcoding.ilove.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chillcoding.ilove.App
import com.chillcoding.ilove.R
import com.chillcoding.ilove.extension.DelegatesExt
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * Created by macha on 07/09/2017.
 */
class SettingsFragment : Fragment() {

    var isSound: Boolean by DelegatesExt.preference(this as Fragment, App.PREF_SOUND, true)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view = inflater?.inflate(R.layout.fragment_settings, container, false)
        return view!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsSoundSwitch.isChecked = isSound
    }

    override fun onPause() {
        super.onPause()
        isSound = settingsSoundSwitch.isChecked
    }
}

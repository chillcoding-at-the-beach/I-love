package com.chillcoding.ilove.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chillcoding.ilove.App
import com.chillcoding.ilove.R
import kotlinx.android.synthetic.main.fragment_award_details.*

class AwardDetailsFragment : Fragment() {

    var mCurrentPosition = -1

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(App.STATE_AWARD_POSITION)
        }

        var view = inflater?.inflate(R.layout.fragment_award_details, container, false)

        return view!!
    }

    override fun onStart() {
        super.onStart()
        if (arguments != null) {
            updateAwardView(arguments.getInt(App.STATE_AWARD_POSITION))
        } else if (mCurrentPosition != -1) {
            updateAwardView(mCurrentPosition)
        }
    }

    fun updateAwardView(position: Int) {
        awardDetailsText.text = resources.getStringArray(R.array.text_award)[position]
        mCurrentPosition = position
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putInt(App.STATE_AWARD_POSITION, mCurrentPosition)
    }
}
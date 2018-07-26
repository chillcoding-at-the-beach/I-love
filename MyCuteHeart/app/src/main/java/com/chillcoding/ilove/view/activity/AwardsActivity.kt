package com.chillcoding.ilove.view.activity

import android.os.Bundle
import android.support.text.emoji.EmojiCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.chillcoding.ilove.App
import com.chillcoding.ilove.R
import com.chillcoding.ilove.event.AwardsEvent
import com.chillcoding.ilove.view.fragment.AwardDetailsFragment
import com.chillcoding.ilove.view.fragment.AwardsFragment
import com.eightbitlab.rxbus.Bus
import com.eightbitlab.rxbus.registerInBus
import kotlinx.android.synthetic.main.activity_awards.*
import kotlinx.android.synthetic.main.fragment_award_details.*

class AwardsActivity : AppCompatActivity() {

    var isStarred = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_awards)
        setSupportActionBar(awardsToolbar)

        supportActionBar!!.title = getString(R.string.menu_awards)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        setUpFab()

        if (awardsContainer != null) {
            supportFragmentManager.beginTransaction().add(R.id.awardsContainer, AwardsFragment() as Fragment).commit()
        }
    }

    public override fun onStart() {
        super.onStart()
        Bus.observe<AwardsEvent>()
                .subscribe { showAward(it.position) }
                .registerInBus(this)
    }

    public override fun onStop() {
        super.onStop()
        Bus.unregister(this)
    }

    fun showAward(position: Int) {
        val awardTitle = EmojiCompat.get().process("${getString(R.string.word_award)} ${App.awardsTitle[position]} ")
        supportActionBar!!.title = awardTitle
        if (awardDetailsFragment != null) {
            (awardDetailsFragment as AwardDetailsFragment).updateAwardView(position)
            awardDetailsText.setCompoundDrawablesWithIntrinsicBounds(0, App.sAwardImg[position], 0, 0)
        } else {
            val awardDetailsFragment: Fragment = AwardDetailsFragment()
            val arg = Bundle()
            arg.putInt(App.STATE_AWARD_POSITION, position)
            awardDetailsFragment.arguments = arg
            supportFragmentManager.beginTransaction().replace(R.id.awardDetailsContainer, awardDetailsFragment).addToBackStack(null).commit()
            awardsNestedScroll.visibility = View.VISIBLE
            awardsContainer.visibility = View.GONE
            awardFab.visibility = View.VISIBLE
            awardDetailsImg.visibility = View.VISIBLE
            awardDetailsImg.setImageResource(App.sAwardImg[position])
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (awardsContainer != null) {
            awardDetailsImg.visibility = View.GONE
            awardFab.visibility = View.GONE
            awardsNestedScroll.visibility = View.GONE
            supportActionBar!!.title = getString(R.string.menu_awards)
            awardsContainer.visibility = View.VISIBLE
        }
        onBackPressed()
        return true
    }

    fun setUpFab() {
        awardFab.setOnClickListener {
            if (isStarred)
                awardFab.setImageResource(R.drawable.ic_life)
            else
                awardFab.setImageResource(R.drawable.ic_love_border)
            isStarred = !isStarred
        }
    }
}
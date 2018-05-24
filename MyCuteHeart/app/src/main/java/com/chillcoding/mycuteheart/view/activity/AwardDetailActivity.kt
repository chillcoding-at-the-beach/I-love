package com.chillcoding.mycuteheart.view.activity

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import com.chillcoding.mycuteheart.App
import com.chillcoding.mycuteheart.R
import kotlinx.android.synthetic.main.activity_award_detail.*
import kotlinx.android.synthetic.main.content_award_detail.*

class AwardDetailActivity : AppCompatActivity() {

    var isStarred = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_award_detail)
        setSupportActionBar(toolbar)

        val mode = intent.getIntExtra(App.AWARD_MODE, 0)

        supportActionBar!!.title = resources.getStringArray(R.array.word_mode)[mode]

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        fab.setOnClickListener {
            if (isStarred)
                fab.setImageResource(R.drawable.ic_life)
            else
                fab.setImageResource(R.drawable.ic_love_border)
            isStarred = !isStarred
        }

        awardDetailImg.setImageResource(R.drawable.ic_award)
        awardDetailText.text = resources.getStringArray(R.array.text_award)[mode - 1]

        val tf: Typeface? = ResourcesCompat.getFont(this.applicationContext, R.font.shadows_into_light)
        collapsingToolbar.setCollapsedTitleTypeface(tf);
        collapsingToolbar.setExpandedTitleTypeface(tf);
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

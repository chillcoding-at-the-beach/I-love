package com.chillcoding.ilove.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chillcoding.ilove.App
import com.chillcoding.ilove.R
import com.chillcoding.ilove.extension.DelegatesExt
import kotlinx.android.synthetic.main.fragment_about.*
import org.jetbrains.anko.support.v4.browse
import org.jetbrains.anko.support.v4.share
import org.jetbrains.anko.support.v4.toast

/**
 * Created by macha on 02/08/2017.
 */
class AboutFragment : Fragment() {

    var isPremium: Boolean by DelegatesExt.preference(this, App.PREF_PREMIUM, false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view = inflater?.inflate(R.layout.fragment_about, container, false)
        return view!!
    }

    private var nb: Int = 8

    private var i: Int = 0

    private var like: Boolean = true

    private var beach: Boolean = true

    private var star: Boolean = true

    private var thumbUp: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        aboutChillcodingButton.setOnClickListener { browse("https://www.chillcoding.com/") }
        aboutAndroidImg.setOnClickListener {
            when (nb) {
                in 1..7 -> toast("${nb--}")
                0 -> browse("https://gitlab.com/chillcoding-at-the-beach/I-love")
                else -> {
                    toast(":p")
                    nb--
                }
            }
        }
        aboutMaterialDesignImg.setOnClickListener {
            when (i++ % 3) {
                3 -> aboutMaterialDesignImg.setImageResource(R.drawable.ic_material_design_red)
                2 -> aboutMaterialDesignImg.setImageResource(R.drawable.ic_material_design_yellow)
                1 -> aboutMaterialDesignImg.setImageResource(R.drawable.ic_material_design_blue)
                else -> aboutMaterialDesignImg.setImageResource(R.drawable.ic_material_design)
            }
        }
        with(aboutChillcodingIcon) {
            setOnClickListener {
                if (like)
                    setImageResource(R.drawable.ic_menu_love)
                else
                    setImageResource(R.drawable.ic_love_border)
                like = !like
            }
        }
        with(aboutChillcodingBeachIcon) {
            setOnClickListener {
                if (beach)
                    setImageResource(R.drawable.ic_sun)
                else
                    setImageResource(R.drawable.ic_beach_access)
                beach = !beach
            }
        }
        with(aboutChillcodingStarIcon) {
            setOnClickListener {
                if (star)
                    setImageResource(R.drawable.ic_star)
                else
                    setImageResource(R.drawable.ic_star_border)
                star = !star
                if (!like && !beach) {
                    isPremium = !isPremium
                    toast("cool")
                }
            }
        }
        with(facebookLikeIcon) {
            setOnClickListener {
                if (thumbUp)
                    setImageResource(R.drawable.ic_thumb_down)
                else
                    setImageResource(R.drawable.ic_thumb_up)
                thumbUp = !thumbUp
            }
        }
        socialButton.setOnClickListener { browse("https://www.chillcoding.com/app/ilove/") }

        facebookImg.setOnClickListener { browse("https://www.facebook.com/ilovekotlin/") }

        instagramImg.setOnClickListener { browse("https://www.instagram.com/ilovekotlin/") }

        twitterImg.setOnClickListener { browse("https://twitter.com/ILovekotlin/") }

        aboutShare.setOnClickListener { share("${getString(R.string.text_share_app)} ${getString(R.string.url_app)}", getString(R.string.app_name)) }

        aboutUpdate.setOnClickListener { browse("${getString(R.string.url_app)}") }
    }
}


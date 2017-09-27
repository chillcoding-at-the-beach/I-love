package com.chillcoding.mycuteheart.view.fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chillcoding.mycuteheart.R
import kotlinx.android.synthetic.main.fragment_my_about.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.toast

/**
 * Created by macha on 02/08/2017.
 */
class MyAboutFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view = inflater?.inflate(R.layout.fragment_my_about, container, false)
        return view!!
    }

    private var nb: Int = 8

    private var i: Int = 0

    private var like: Boolean = true

    private var beach: Boolean = true

    private var star: Boolean = true

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
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
                2 -> aboutMaterialDesignImg.setImageResource(R.drawable.ic_material_design_blue)
                1 -> aboutMaterialDesignImg.setImageResource(R.drawable.ic_material_design_red)
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
            with(aboutChillcodingBeachIcon) {
                setOnClickListener {
                    if (beach)
                        setImageResource(R.drawable.ic_sun)
                    else
                        setImageResource(R.drawable.ic_beach_access)
                    beach = !beach
                }
                with(aboutChillcodingStarIcon) {
                    setOnClickListener {
                        if (star)
                            setImageResource(R.drawable.ic_star)
                        else
                            setImageResource(R.drawable.ic_star_border)
                        star = !star

                    }
                }
            }
        }
    }
}


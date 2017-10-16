package com.chillcoding.mycuteheart.extension

import android.annotation.SuppressLint
import android.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import kotlin.reflect.KProperty


object DelegatesExt {
    fun <T> preference(context: Context, name: String,
                       default: T) = Preference(context, name, default)

    fun <T> preference(fragment: Fragment, name: String,
                       default: T) = Preference(fragment, name, default)
}

class Preference<T>(private var context: Context?, private val name: String,
                    private val default: T) {

    var myFragment: Fragment? = null

    constructor(fragment: Fragment, name: String, default: T) : this(null, name, default) {
        myFragment = fragment
    }

    private val prefs: SharedPreferences by lazy {
        if (myFragment == null)
            context!!.getSharedPreferences("default", Context.MODE_PRIVATE)
        else
            myFragment!!.activity.getSharedPreferences("default", Context.MODE_PRIVATE)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = findPreference(name, default)

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(name, value)
    }

    @Suppress("UNCHECKED_CAST")
    private fun findPreference(name: String, default: T): T = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }
        res as T
    }

    @SuppressLint("CommitPrefEdits")
    private fun putPreference(name: String, value: T) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type can't be saved into Preferences")
        }.apply()
    }
}

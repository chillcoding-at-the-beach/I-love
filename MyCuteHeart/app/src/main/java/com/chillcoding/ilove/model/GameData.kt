package com.chillcoding.ilove.model

import android.os.Parcel
import android.os.Parcelable
import com.chillcoding.ilove.App
import com.chillcoding.ilove.extension.DelegatesExt

/**
 * Created by macha on 21/07/2017.
 */
class GameData : Parcelable {

    var score = 0
    var level = 1
    var nbLife = 3
    var awardLevel: Int by DelegatesExt.preference(App.instance, App.PREF_AWARD_LEVEL, -1)
    var awardUnlocked = false

    constructor()

    constructor(parcel: Parcel) {
        score = parcel.readInt()
        level = parcel.readInt()
        nbLife = parcel.readInt()
        if (parcel.readByte().toInt() != 0)
            awardUnlocked = true

    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(score)
        dest?.writeInt(level)
        dest?.writeInt(nbLife)
        dest?.writeInt(awardLevel)
        var valToWrite = 0
        if (awardUnlocked)
            valToWrite = 1
        dest?.writeByte(valToWrite.toByte())
    }

    override fun describeContents(): Int {
        return 0
    }

    val CREATOR: Parcelable.Creator<GameData> = object : Parcelable.Creator<GameData> {
        override fun createFromParcel(`in`: Parcel): GameData {
            return GameData(`in`)
        }

        override fun newArray(size: Int): Array<GameData?> {
            return arrayOfNulls<GameData>(size)
        }
    }

}
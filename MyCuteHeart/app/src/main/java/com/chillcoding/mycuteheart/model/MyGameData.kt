package com.chillcoding.mycuteheart.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by macha on 21/07/2017.
 */
class MyGameData : Parcelable {

    var mScore = 0
    var mLevel = 0
    var mSpeed = 1

    constructor()

    constructor(parcel: Parcel) {
        mScore = parcel.readInt()
        mLevel = parcel.readInt()
        if (mLevel < 5)
            mSpeed = Math.pow(2.0, (mLevel - 1).toDouble()).toInt()
        else
            mSpeed = 8
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(mScore)
        dest?.writeInt(mLevel)
    }

    override fun describeContents(): Int {
        return 0
    }

    val CREATOR: Parcelable.Creator<MyGameData> = object : Parcelable.Creator<MyGameData> {
        override fun createFromParcel(`in`: Parcel): MyGameData {
            return MyGameData(`in`)
        }

        override fun newArray(size: Int): Array<MyGameData?> {
            return arrayOfNulls<MyGameData>(size)
        }
    }

}
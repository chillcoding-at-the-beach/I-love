package com.chillcoding.mycuteheart.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.chillcoding.mycuteheart.MyApp
import org.jetbrains.anko.db.*

/**
 * Created by macha on 01/12/2017.
 */
class ScoreDbHelper(ctx: Context = MyApp.instance) : ManagedSQLiteOpenHelper(ctx,
        ScoreDbHelper.DB_NAME, null, ScoreDbHelper.DB_VERSION) {

    companion object {
        val DB_NAME = "score.db"
        val DB_VERSION = 1
        val instance by lazy { ScoreDbHelper() }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(ScoreTable.NAME, true,
                ScoreTable.ID to INTEGER + PRIMARY_KEY,
                ScoreTable.PSEUDO to TEXT,
                ScoreTable.SCORE to INTEGER
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(ScoreTable.NAME, true)
        onCreate(db)
    }
}

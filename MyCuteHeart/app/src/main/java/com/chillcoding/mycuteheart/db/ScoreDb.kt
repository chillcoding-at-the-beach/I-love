package com.chillcoding.mycuteheart.db

import com.chillcoding.mycuteheart.model.Score
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select

/**
 * Created by macha on 01/12/2017.
 */
class ScoreDb(private val scoreDbHelper: ScoreDbHelper = ScoreDbHelper.instance) {


    fun requestScores() = scoreDbHelper.use {
        select(ScoreTable.NAME,
                ScoreTable.PSEUDO, ScoreTable.SCORE)
                .parseList(classParser<Score>())
    }

    private fun saveScore(pseudo: String, score: Int) = scoreDbHelper.use {
        insert(ScoreTable.NAME,
                ScoreTable.PSEUDO to pseudo,
                ScoreTable.SCORE to score)
    }

    fun saveScores(scoreList: List<Score>) {
        for (s in scoreList)
            saveScore(s.pseudo, s.score)
    }
}


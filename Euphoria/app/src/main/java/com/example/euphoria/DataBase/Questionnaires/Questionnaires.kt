package com.example.euphoria.DataBase.Questionnaires

import android.arch.persistence.room.*
import java.io.Serializable

@Entity(tableName = "QUESTIONNAIRES")
data class Questionnaires (
    @ColumnInfo(name = "TITLE") var title: String?,
    @ColumnInfo(name = "STATE") var state: Int = 0,
    @ColumnInfo(name = "TOTAL") var total: Int = 0
): Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UID")
    var uid: Long = 0
}


@Dao
interface QuestionnairesDao {
    @Query("SELECT * FROM QUESTIONNAIRES")
    fun getAll(): List<Questionnaires>

    @Insert
    fun insert(questionnaires: Questionnaires): Long
}
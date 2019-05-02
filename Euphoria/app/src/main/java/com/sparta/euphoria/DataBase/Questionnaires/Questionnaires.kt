package com.sparta.euphoria.DataBase.Questionnaires

import android.arch.persistence.room.*
import java.io.Serializable

@Entity(tableName = "QUESTIONNAIRES")
data class Questionnaires (
    @ColumnInfo(name = "CUSTOMER_ID") var customerId: Int = 0,
    @ColumnInfo(name = "QINDEX") var index: Int = 0,
    @ColumnInfo(name = "STATE") var state: Int = 0,
    @ColumnInfo(name = "TITLE") var title: String?,
    @ColumnInfo(name = "TOTAL") var total: Int = 0
): Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UID")
    var uid: Long = 0
}


@Dao
interface QuestionnairesDao {
    @Insert
    fun insert(questionnaires: Questionnaires): Long

    @Query("SELECT * FROM QUESTIONNAIRES WHERE QINDEX = :index AND CUSTOMER_ID = :customerId")
    fun getQuestionnaire(index: Int, customerId: Int) : Questionnaires

    @Query("SELECT * FROM QUESTIONNAIRES WHERE CUSTOMER_ID = :customerId")
    fun getQuestionnaires(customerId: Int): List<Questionnaires>
}
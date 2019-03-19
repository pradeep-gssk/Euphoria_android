package com.example.euphoria.DataBase.Questionnaires

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE

@Entity(tableName = "QUESTIONNAIRE",
    foreignKeys = [ForeignKey(
        entity = Questionnaires::class,
        parentColumns = ["UID"],
        childColumns = ["QUESTIONNAIRES_ID"],
        onDelete = CASCADE)])

data class Questionnaire (
    @ColumnInfo(name = "ANSWER") var answer: String?,
    @ColumnInfo(name = "DETAILS") var details: String?,
    @ColumnInfo(name = "ELEMENT") var element: String?,
    @ColumnInfo(name = "QUESTION") var question: String?,
    @ColumnInfo(name = "OPTION_TYPE") var optionType: Int = 0,
    @ColumnInfo(name = "SUB_OPTION_TYPE") var subOptionType: Int = 0,
    @ColumnInfo(name = "QUESTIONNAIRES_ID") var questionnairesId: Long = 0
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UID")
    var uid: Long = 0
}

@Dao
interface QuestionnaireDao {
    @Query("SELECT * FROM QUESTIONNAIRE WHERE QUESTIONNAIRES_ID = :questionnairesId")
    fun getChildQuestionnaire(questionnairesId: Long): List<Questionnaire>

    @Insert
    fun insert(entity: Questionnaire): Long

    @Query("UPDATE QUESTIONNAIRE SET ANSWER = :answer WHERE UID = :uid")
    fun updateAnswer(answer: String?, uid: Long)

    @Query("UPDATE QUESTIONNAIRE SET DETAILS = :detail WHERE UID = :uid")
    fun updateDetail(detail: String?, uid: Long)
}
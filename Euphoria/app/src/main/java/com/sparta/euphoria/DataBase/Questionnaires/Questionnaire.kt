package com.sparta.euphoria.DataBase.Questionnaires

import android.arch.persistence.room.*

@Entity(tableName = "QUESTIONNAIRE",
    foreignKeys = [ForeignKey(
        entity = Questionnaires::class,
        parentColumns = ["UID"],
        childColumns = ["QUESTIONNAIRES_ID"],
        onDelete = ForeignKey.CASCADE
    )])

data class Questionnaire (
    @ColumnInfo(name = "ANSWER") var answer: String?,
    @ColumnInfo(name = "COLOR_INDEX") var colorIndex: Int = 0,
    @ColumnInfo(name = "DETAILS") var details: String?,
    @ColumnInfo(name = "ELEMENT") var element: String?,
    @ColumnInfo(name = "OPTION_TYPE") var optionType: Int = 0,
    @ColumnInfo(name = "QUESTION") var question: String?,
    @ColumnInfo(name = "SELECTION_TYPE") var selectionType: Int = 0,
    @ColumnInfo(name = "SUB_OPTION_TYPE") var subOptionType: Int = 0,
    @ColumnInfo(name = "QUESTIONNAIRES_ID") var questionnairesId: Long = 0
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UID")
    var uid: Long = 0
}

@Dao
interface QuestionnaireDao {
    @Insert
    fun insert(entity: Questionnaire): Long

    @Query("SELECT * FROM QUESTIONNAIRE WHERE ((((OPTION_TYPE = 0) OR (OPTION_TYPE = 1)) AND (ANSWER is null)) OR ((OPTION_TYPE = 2) AND (ANSWER is null) AND (DETAILS is null))) AND (QUESTIONNAIRES_ID = :questionnairesId)")
    fun getAnsweredQuestionnaireList(questionnairesId: Long): List<Questionnaire>

    @Query("SELECT * FROM QUESTIONNAIRE WHERE QUESTIONNAIRES_ID = :questionnairesId")
    fun getQuestionnaireList(questionnairesId: Long): List<Questionnaire>

//
//    @Query("UPDATE QUESTIONNAIRE SET ANSWER = :answer WHERE UID = :uid")
//    fun updateAnswer(answer: String?, uid: Long)
//
//    @Query("UPDATE QUESTIONNAIRE SET DETAILS = :detail WHERE UID = :uid")
//    fun updateDetail(detail: String?, uid: Long)
}
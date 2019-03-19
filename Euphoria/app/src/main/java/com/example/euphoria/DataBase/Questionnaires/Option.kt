package com.example.euphoria.DataBase.Questionnaires

import android.arch.persistence.room.*

@Entity(tableName = "OPTION",
    foreignKeys = [ForeignKey(
        entity = Questionnaire::class,
        parentColumns = ["UID"],
        childColumns = ["QUESTIONNAIRE_ID"],
        onDelete = ForeignKey.CASCADE
    )])

data class Option (
    @ColumnInfo(name = "OPTION") var option: String?,
    @ColumnInfo(name = "QUESTIONNAIRE_ID") var questionnaireId: Long = 0
    ) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UID")
    var uid: Long = 0
}

@Dao
interface OptionDao {
    @Query("SELECT * FROM OPTION WHERE QUESTIONNAIRE_ID = :questionnaireId")
    fun getChildOptions(questionnaireId: Long): List<Option>

    @Insert
    fun insertAll(entity: ArrayList<Option>)
}
package com.sparta.euphoria.DataBase

import android.arch.persistence.room.*
import java.io.Serializable

@Entity(tableName = "EXERCISES")
data class Exercises (
    @ColumnInfo(name = "ELEMENT") var element: String?,
    @ColumnInfo(name = "EXERCISE") var exercise: String?,
    @ColumnInfo(name = "THUMBNAIL") var thumbnail: String?,
    @ColumnInfo(name = "VIDEO_DESCRIPTION") var videoDescription: String?,
    @ColumnInfo(name = "VIDEO_NAME") var videoName: String?,
    @ColumnInfo(name = "VIDEO_PATH") var videoPath: String?
): Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UID")
    var uid: Long = 0
}


@Dao
interface ExercisesDao {
//    @Query("SELECT * FROM EXERCISES WHERE ELEMENT = :element AND EXERCISE = :type")
//    fun getExercisesForElementAndType(element: String, type: String): List<Exercises>
//
//    @Query("SELECT * FROM EXERCISES WHERE ELEMENT = :element")
//    fun getExerciseForElement(element: String): List<Exercises>
//
//    @Query("SELECT DISTINCT EXERCISE FROM EXERCISES WHERE ELEMENT = :element")
//    fun getUniqueExercisesForElement(element: String): List<String>
//
//    @Insert
//    fun insertAll(entity: ArrayList<Exercises>)
}
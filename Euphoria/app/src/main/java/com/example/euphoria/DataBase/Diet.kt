package com.example.euphoria.DataBase

import android.arch.persistence.room.*
import java.io.Serializable

@Entity(tableName = "DIET")
data class Diet (
    @ColumnInfo(name = "CHANNELS") var channels: String?,
    @ColumnInfo(name = "DIET") var diet: String?,
    @ColumnInfo(name = "EFFECT") var effect: String?,
    @ColumnInfo(name = "FLAVOUR") var flavour: String?,
    @ColumnInfo(name = "NAME") var name: String?,
    @ColumnInfo(name = "NATURE") var nature: String?,
    @ColumnInfo(name = "ORGAN") var organ: String?,
    @ColumnInfo(name = "ELEMENT") var element: String?
): Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UID")
    var uid: Long = 0
}

@Dao
interface DietDao {
    @Query("SELECT * FROM DIET WHERE ELEMENT = :element AND DIET = :type")
    fun getDietsForElementAndType(element: String, type: String): List<Diet>

    @Query("SELECT * FROM DIET WHERE ELEMENT = :element")
    fun getDietForElement(element: String): List<Diet>

    @Query("SELECT DISTINCT DIET FROM DIET WHERE ELEMENT = :element")
    fun getUniqueDietsForElement(element: String): List<String>

    @Insert
    fun insertAll(entity: ArrayList<Diet>)
}
package com.sparta.euphoria.DataBase

import android.arch.persistence.room.*
import java.io.Serializable

@Entity(tableName = "HISTORY")
data class History (
    @ColumnInfo(name = "CUSTOMER_ID") var customerId: Int = 0,
    @ColumnInfo(name = "DATE") var date: Long,
    @ColumnInfo(name = "FILE_NAME") var fileName: String?,
    @ColumnInfo(name = "IMAGE_TYPE") var imageType: String?
): Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UID")
    var uid: Long = 0
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM HISTORY WHERE IMAGE_TYPE = :imageType AND CUSTOMER_ID = :customerId")
    fun getHistoryForImageType(imageType: String, customerId: Int): List<History>

    @Insert
    fun insert(entity: History)

    @Delete
    fun delete(entity: History)
}
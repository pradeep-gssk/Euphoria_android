package com.example.euphoria.DataBase.Timer

import android.arch.persistence.room.*
import java.io.Serializable

@Entity(tableName = "SESSION")
data class Session (
    @ColumnInfo(name = "NAME") var name: String?,
    @ColumnInfo(name = "TIME") var time: Long = 0
): Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UID")
    var uid: Long = 0
}

@Dao
interface SessionDao {
    @Query("SELECT * FROM SESSION")
    fun getSessions(): List<Session>

    @Insert
    fun insert(entity: Session): Long
}
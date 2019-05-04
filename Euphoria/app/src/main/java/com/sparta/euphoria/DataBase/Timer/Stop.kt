package com.sparta.euphoria.DataBase.Timer

import android.arch.persistence.room.*
import java.io.Serializable

@Entity(tableName = "STOP",
    foreignKeys = [ForeignKey(
        entity = Session::class,
        parentColumns = ["UID"],
        childColumns = ["SESSION_ID"],
        onDelete = ForeignKey.CASCADE
    )])

data class Stop (
    @ColumnInfo(name = "RESOURCE") var resource: String?,
    @ColumnInfo(name = "SOUND_NAME") var sound: String?,
    @ColumnInfo(name = "TIME") var time: Long = 0,
    @ColumnInfo(name = "TYPE") var type: String?,
    @ColumnInfo(name = "SESSION_ID") var sessionId: Long = 0
): Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UID")
    var uid: Long = 0
}

@Dao
interface StopDao {
    @Insert
    fun insertAll(entities: ArrayList<Stop>)

    @Query("SELECT * FROM STOP WHERE SESSION_ID = :sessionId")
    fun getStop(sessionId: Long): List<Stop>
}
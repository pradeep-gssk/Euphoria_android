package com.example.euphoria.DataBase.Timer

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE

@Entity(tableName = "STOP",
    foreignKeys = [ForeignKey(
        entity = Session::class,
        parentColumns = ["UID"],
        childColumns = ["SESSION_ID"],
        onDelete = CASCADE)])

data class Stop (
    @ColumnInfo(name = "RESOURCE") var resource: String?,
    @ColumnInfo(name = "SOUND_NAME") var sound: String?,
    @ColumnInfo(name = "TIME") var time: Long = 0,
    @ColumnInfo(name = "TYPE") var type: String?,
    @ColumnInfo(name = "SESSION_ID") var sessionId: Long = 0
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UID")
    var uid: Long = 0
}

@Dao
interface StopDao {
    @Query("SELECT * FROM STOP WHERE SESSION_ID = :sessionId")
    fun getStop(sessionId: Long): List<Stop>

    @Insert
    fun insertAll(entities: ArrayList<Stop>)
}
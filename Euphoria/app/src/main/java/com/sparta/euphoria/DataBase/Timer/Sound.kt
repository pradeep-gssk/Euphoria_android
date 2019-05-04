package com.sparta.euphoria.DataBase.Timer

import android.arch.persistence.room.*
import java.io.Serializable

@Entity(tableName = "SOUND")
data class Sound (
    @ColumnInfo(name = "NAME") var name: String?,
    @ColumnInfo(name = "RESOURCE") var resource: String?,
    @ColumnInfo(name = "TYPE") var type: String?
): Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UID")
    var uid: Long = 0
}

@Dao
interface SoundDao {
    @Insert
    fun insertAll(entity: ArrayList<Sound>)

    @Query("SELECT * FROM SOUND")
    fun getSounds(): List<Sound>
}
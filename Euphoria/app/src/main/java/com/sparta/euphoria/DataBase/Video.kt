package com.sparta.euphoria.DataBase

import android.arch.persistence.room.*
import java.io.Serializable

@Entity(tableName = "VIDEO")
data class Video (
    @ColumnInfo(name = "THUMBNAIL") var thumbnail: String?,
    @ColumnInfo(name = "TITLE") var title: String?,
    @ColumnInfo(name = "VIDEO_DESCRIPTION") var videoDescription: String?,
    @ColumnInfo(name = "VIDEO_NAME") var videoName: String?,
    @ColumnInfo(name = "VIDEO_PATH") var videoPath: String?
): Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UID")
    var uid: Long = 0
}

@Dao
interface VideoDao {
    @Insert
    fun insertAll(entity: ArrayList<Video>)

    @Query("SELECT DISTINCT TITLE FROM VIDEO")
    fun getUniqueVideoTitle(): List<String>

    @Query("SELECT * FROM VIDEO WHERE TITLE = :title")
    fun getVideosForTitle(title: String): List<Video>
}
package com.sparta.euphoria.DataBase

import android.arch.persistence.room.*
import java.io.Serializable

@Entity(tableName = "USER")
data class User (
    @ColumnInfo(name = "CUSTOMER_ID") var customerId: Int = 0
): Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UID")
    var uid: Long = 0
}

@Dao
interface UserDao {
    @Insert
    fun insert(entity: User)

    @Query("SELECT * FROM USER WHERE CUSTOMER_ID = :customerId")
    fun getUser(customerId: Int) : User?
}
package com.example.euphoria.Models

import com.example.euphoria.DataBase.Timer.Sound
import java.io.Serializable

data class EUStop(val index: Int, var sound: Sound?, var timeInterval: Long): Serializable
data class EUSession(val name: String, val timeInterval: Long, var stops: ArrayList<EUStop>): Serializable

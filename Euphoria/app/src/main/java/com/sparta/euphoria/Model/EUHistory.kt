package com.sparta.euphoria.Model

import com.sparta.euphoria.DataBase.History
import java.io.Serializable

data class EUHistory(val history: History, val title: String): Serializable

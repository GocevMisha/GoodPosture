package ru.gocev.goodposture.models

import java.io.Serializable
import java.util.*

data class HistoryItem(val id: Long, val status: Status = Status.WAIT, val date: Date, val name: String){


    enum class Status() {
        OK,
        WAIT,
        ERROR
    }
}

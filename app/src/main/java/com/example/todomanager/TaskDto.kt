package com.example.kotlinhell

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.todomanager.Task
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
fun convertDate(dateToConvert: LocalDate): Date? {
    return Date.from(
        dateToConvert.atStartOfDay()
            .atZone(ZoneId.systemDefault())
            .toInstant()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
val dateFormatter = DateTimeFormatter.ISO_DATE

class TaskDto(todoItem: Task) {
    var objectId: String? = todoItem.objectId
    var title: String? = todoItem.title
    var description: String? = todoItem.description
    var done: Boolean = todoItem.done
    @RequiresApi(Build.VERSION_CODES.O)
    var deadline: Date? = convertDate(LocalDate.parse(todoItem.deadline, dateFormatter))
}
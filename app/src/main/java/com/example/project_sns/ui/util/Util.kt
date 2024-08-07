package com.example.project_sns.ui.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun dateFormat(time: LocalDateTime) : String {
    return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}

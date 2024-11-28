package com.example.project_sns.ui.util


import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun dateFormat(time: LocalDateTime) : String {
    return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}

fun chatDateFormat() : String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.KOREA)
    formatter.timeZone = TimeZone.getTimeZone("Asia/Seoul")
    return formatter.format(Date().time)
//    time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"))
}

fun stringToLocalTime(time: String): LocalDateTime {
    return LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"))
}

fun chatTimeFormat(time: LocalDateTime): String {
    return time.format(DateTimeFormatter.ofPattern("a hh:mm"))
}

fun chatListDateFormat(time: LocalDateTime): String {
    return time.format(DateTimeFormatter.ofPattern("a hh:mm"))
}


val charSet = ('0'..'9') + ('a'..'z') + ('A'..'Z')

val randomString = List(10) { charSet.random() }.joinToString("")
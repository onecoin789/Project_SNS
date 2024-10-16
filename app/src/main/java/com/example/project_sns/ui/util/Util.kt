package com.example.project_sns.ui.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun dateFormat(time: LocalDateTime) : String {
    return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}

fun chatDateFormat(time: LocalDateTime): String {
    return time.format(DateTimeFormatter.ofPattern("a hh:mm"))
}

val charSet = ('0'..'9') + ('a'..'z') + ('A'..'Z')

val randomString = List(10) { charSet.random() }.joinToString("")
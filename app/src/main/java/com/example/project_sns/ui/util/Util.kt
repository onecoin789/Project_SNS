package com.example.project_sns.ui.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun dateFormat(time: LocalDateTime) : String {
    return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}

fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
    val ft: FragmentTransaction = fragmentManager.beginTransaction()
    ft.detach(fragment).attach(fragment).commit()
}

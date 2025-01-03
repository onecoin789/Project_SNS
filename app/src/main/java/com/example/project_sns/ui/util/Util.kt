package com.example.project_sns.ui.util


import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun dateFormat(time: LocalDateTime) : String {
    return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}

fun chatDateFormat() : String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.KOREAN)
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

fun textWatcher(text: TextView, editText: EditText, viewPager: ViewPager2) {
    editText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            if (editText.text.isEmpty()) {
                viewPager.visibility = View.GONE
                text.visibility = View.VISIBLE
            } else {
                viewPager.visibility = View.VISIBLE
                text.visibility = View.GONE
            }
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (editText.text.isEmpty()) {
                viewPager.visibility = View.GONE
                text.visibility = View.VISIBLE
            } else {
                viewPager.visibility = View.VISIBLE
                text.visibility = View.GONE
            }
        }

        override fun afterTextChanged(s: Editable?) {
            if (editText.text.isEmpty()) {
                viewPager.visibility = View.GONE
                text.visibility = View.VISIBLE
            } else {
                viewPager.visibility = View.VISIBLE
                text.visibility = View.GONE
            }
        }

    })
}

fun deleteTextWatcher(editText: EditText, deleteButton: ImageView) {
    editText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            if (editText.text.isEmpty()) {
                deleteButton.visibility = View.GONE
            } else {
                deleteButton.visibility = View.VISIBLE
            }
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (editText.text.isEmpty()) {
                deleteButton.visibility = View.GONE
            } else {
                deleteButton.visibility = View.VISIBLE
            }
        }

        override fun afterTextChanged(s: Editable?) {
            if (editText.text.isEmpty()) {
                deleteButton.visibility = View.GONE
            } else {
                deleteButton.visibility = View.VISIBLE
            }
        }

    })
}


val charSet = ('0'..'9') + ('a'..'z') + ('A'..'Z')

val randomString = List(10) { charSet.random() }.joinToString("")
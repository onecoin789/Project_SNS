package com.example.project_sns.ui.util


import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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

fun postDateFormat() : String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREAN)
    formatter.timeZone = TimeZone.getTimeZone("Asia/Seoul")
    return formatter.format(Date().time)
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

fun sendToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun sharePost(url: String): Intent {
    val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
    intent.type = "text/plain"
    val intro = "친구에게 게시물 공유하기"
    val content = "친구가 게시물 링크를 공유했어요!"
    intent.putExtra(Intent.EXTRA_TEXT, "$url\n\n$content")
    return Intent.createChooser(intent, intro)
}


val charSet = ('0'..'9') + ('a'..'z') + ('A'..'Z')

val randomString = List(10) { charSet.random() }.joinToString("")
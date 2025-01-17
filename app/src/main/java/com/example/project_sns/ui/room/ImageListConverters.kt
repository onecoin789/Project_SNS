package com.example.project_sns.ui.room

import androidx.room.TypeConverter
import com.example.project_sns.ui.model.ImageDataModel
import com.google.gson.Gson

class ImageListConverters {
    @TypeConverter
    fun listToJson(value: List<ImageDataModel>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String): List<ImageDataModel>? {
        return Gson().fromJson(value, Array<ImageDataModel>::class.java)?.toList()
    }
}
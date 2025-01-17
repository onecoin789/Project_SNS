package com.example.project_sns.ui.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.project_sns.ui.model.MyPostListModel


@Database(entities = [MyPostListModel::class], exportSchema = false, version = 1)
abstract class MyPostListDatabase: RoomDatabase() {
    abstract fun getMyPostListDAO(): MyPostListDAO

    companion object {
        private var INSTANCE: MyPostListDatabase? = null

        fun getMyPostListDatabase(context: Context): MyPostListDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context, MyPostListDatabase::class.java, "MyPostList"
                    ).build()
                    INSTANCE = instance
                }
            }
            return INSTANCE!!
        }
    }
}
package com.example.project_sns.ui.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.project_sns.ui.model.MyPostListModel


@Dao
interface MyPostListDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(data: MyPostListModel)

    @Query("SELECT * FROM MyPostList")
    fun getMyPostListData(): LiveData<List<MyPostListModel>>

    @Delete
    fun deleteMyPostData(data: MyPostListModel)
}
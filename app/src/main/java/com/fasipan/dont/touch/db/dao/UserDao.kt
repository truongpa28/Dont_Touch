package com.fasipan.dont.touch.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fasipan.dont.touch.db.entity.AudioEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM audio")
    fun getAllAudio(): MutableList<AudioEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAllAudio(listData: ArrayList<AudioEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAudio(data: AudioEntity): Long

    @Query("delete from audio where id = :listData")
    fun deleteAudio(listData: Int)
}

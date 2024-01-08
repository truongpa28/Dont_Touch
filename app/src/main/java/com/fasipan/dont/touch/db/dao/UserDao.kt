package com.fasipan.dont.touch.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fasipan.dont.touch.db.entity.AudioEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM audio")
    fun getAllAudio(): LiveData<MutableList<AudioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAllAudio(listData: List<AudioEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAudio(data: AudioEntity): Long

    @Delete
    fun deleteAudio(listData: AudioEntity)
}

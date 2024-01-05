package com.fasipan.dont.touch.db

import android.content.Context
import com.fasipan.dont.touch.db.dao.UserDao
import com.fasipan.dont.touch.db.entity.AudioEntity

object LocalDataSource {

    fun init(context: Context) {
        userDao = AppDatabase.getInstance(context).userDao()
    }

    lateinit var userDao: UserDao

    fun getAllAudio() = userDao.getAllAudio()
    fun addAllAudio(listData: List<AudioEntity>) = userDao.addAllAudio(listData)
    fun addAudio(listData: AudioEntity) = userDao.addAudio(listData)
    fun deleteAudio(listData: Int) = userDao.deleteAudio(listData)

}
package com.fasipan.dont.touch.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fasipan.dont.touch.db.dao.UserDao
import com.fasipan.dont.touch.db.entity.AudioEntity

@Database(
    version = 1,
    entities = [
        AudioEntity::class
    ]
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object{
        @Volatile
        private  var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "don_touch_1"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE =instance
                return instance
            }
        }
    }

}

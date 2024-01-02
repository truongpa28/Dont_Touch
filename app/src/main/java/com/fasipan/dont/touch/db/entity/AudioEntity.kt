package com.fasipan.dont.touch.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasipan.dont.touch.R
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "audio")
data class AudioEntity @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "sound")
    var sound: String = "",

    @ColumnInfo(name = "icon")
    var icon: Int = R.drawable.avt_audio,

    @ColumnInfo(name = "nameInt")
    var nameInt: Int = R.string.app_name,

    @ColumnInfo(name = "nameString")
    var nameString: String = "",

    @ColumnInfo(name = "tab")
    var isDefault: Boolean = false
) : Parcelable {

    companion object {
        fun toMeme(jsonData: String): AudioEntity? {
            return Gson().fromJson(jsonData, AudioEntity::class.java)
        }
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}
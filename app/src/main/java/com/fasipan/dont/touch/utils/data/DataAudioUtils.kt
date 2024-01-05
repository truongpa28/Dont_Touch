package com.fasipan.dont.touch.utils.data

import android.content.Context
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.db.LocalDataSource
import com.fasipan.dont.touch.db.entity.AudioEntity

object DataAudioUtils {

    fun init(context: Context) {
        listDefault = listOf(
            AudioEntity(1, "", R.drawable.ic_mic_v1, 0, "", true),
            AudioEntity(2, getPath(context, R.raw.sound_police), R.drawable.avt_police, R.string.police, "", true),
            AudioEntity(3, getPath(context, R.raw.sound_doorbell), R.drawable.avt_door_bell, R.string.doorbell, "", true),
            AudioEntity(4, getPath(context, R.raw.dog_angry), R.drawable.avt_dog, R.string.dog, "", true),
            AudioEntity(5, getPath(context, R.raw.cat_06), R.drawable.avt_cat, R.string.cat, "", true),
            AudioEntity(6, getPath(context, R.raw.sound_alarm_clock), R.drawable.avt_clock, R.string.o_clock, "", true),
            AudioEntity(7, getPath(context, R.raw.sound_harp), R.drawable.avt_harp, R.string.harp, "", true),
            AudioEntity(8, getPath(context, R.raw.sound_laughing), R.drawable.avt_laught, R.string.laughing, "", true),
            AudioEntity(9, getPath(context, R.raw.sound_hello), R.drawable.avt_hello, R.string.hello, "", true),
            AudioEntity(10, getPath(context, R.raw.sound_rooster), R.drawable.avt_rooster, R.string.rooster, "", true),
            AudioEntity(11, getPath(context, R.raw.sound_sneeze), R.drawable.avt_sneeze, R.string.sneeze, "", true),
            AudioEntity(12, getPath(context, R.raw.sound_whistle), R.drawable.avt_whistle, R.string.whistle, "", true),
            AudioEntity(13, getPath(context, R.raw.sound_train), R.drawable.avt_train, R.string.train, "", true),
            AudioEntity(14, getPath(context, R.raw.sound_piano), R.drawable.avt_piano, R.string.piano, "", true),
            AudioEntity(15, getPath(context, R.raw.sound_wind_chimes), R.drawable.avt_wind_ch, R.string.wind_chime, "", true),
        )
    }

    var listDefault = listOf<AudioEntity>()


    fun getPath(context : Context, data: Int) : String {
        return "android.resource://${context.packageName}/${data}"
    }

}
package com.fasipan.dont.touch.utils.data


import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.model.SpeedFlashModel


object SpeedFlashUtils {

    const val SPEED_FLASH_DEFAULT = 850

    val listSpeedFlash = listOf<SpeedFlashModel>(
        SpeedFlashModel(R.drawable.ic_speed_flash_01, false),
        SpeedFlashModel(R.drawable.ic_speed_flash_02, false),
        SpeedFlashModel(R.drawable.ic_speed_flash_03, false),
        SpeedFlashModel(R.drawable.ic_speed_flash_04, false),
        SpeedFlashModel(R.drawable.ic_speed_flash_05, false),
        SpeedFlashModel(R.drawable.ic_speed_flash_06, false),
        SpeedFlashModel(R.drawable.ic_speed_flash_07, false),
        SpeedFlashModel(R.drawable.ic_speed_flash_08, false),
        SpeedFlashModel(R.drawable.ic_speed_flash_09, false),
        SpeedFlashModel(R.drawable.ic_speed_flash_10, false),
        SpeedFlashModel(R.drawable.ic_speed_flash_11, false),
        SpeedFlashModel(R.drawable.ic_speed_flash_12, false),
    )

    enum class SpeedFlash(val speed: Int) {
        Speed1(1100),
        Speed2(1000),
        Speed3(950),
        Speed4(850),
        Speed5(700),
        Speed6(650),
        Speed7(550),
        Speed8(450),
        Speed9(350),
        Speed10(300),
        Speed11(200),
        Speed12(150);

        companion object {
            fun getSpeedByPosition(position: Int): Long {
                return values().getOrNull(position)?.speed?.toLong() ?: SPEED_FLASH_DEFAULT.toLong()
            }
        }
    }

    fun getFlashDelayByType(index: Int): Long {
        return SpeedFlash.getSpeedByPosition(index)
    }

}
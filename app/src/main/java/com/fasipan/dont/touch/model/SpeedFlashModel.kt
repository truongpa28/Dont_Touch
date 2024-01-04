package com.fasipan.dont.touch.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SpeedFlashModel(
    val icon: Int,
    var isSelected: Boolean
) : Parcelable
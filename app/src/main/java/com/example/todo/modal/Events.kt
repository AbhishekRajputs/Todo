package com.example.todo.modal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Events(
    var eventName: String = "",
    var eventCategory: String = "",
    var eventDate: String = "",
    var eventTime: String = ""
) : Parcelable
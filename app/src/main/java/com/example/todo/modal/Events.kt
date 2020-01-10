package com.example.todo.modal

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "todo_events")
@Parcelize
data class Events(
    @ColumnInfo(name = "event_name") var eventName: String = "",
    @ColumnInfo(name = "event_category") var eventCategory: String = "",
    @ColumnInfo(name = "event_date") var eventDate: String = "",
    @PrimaryKey @ColumnInfo(name = "event_time") var eventTime: String = ""
) : Parcelable
package com.example.doroute.data.database.entities

import android.location.Location
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng


@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey
    val locationId:String,

    @ColumnInfo(name="taskId")
    var taskId: String,

    @ColumnInfo(name="latitude")
    var latitude: Double,

    @ColumnInfo(name="longitude")
    var longitude: Double,

    @ColumnInfo(name="name")
    var name: String,

    @ColumnInfo(name="address")
    var address: String
)
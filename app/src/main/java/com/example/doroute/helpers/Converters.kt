package com.example.doroute.helpers

import android.location.Location
import android.util.Log
import androidx.room.TypeConverter
import com.google.android.gms.location.Geofence
import java.util.*

class Converters {
    /*@TypeConverter
    fun locationToString(location: Location):String?{
        if (location==null)
            return null
        return "${location.latitude},${location.longitude}"
    }

    @TypeConverter
    fun stringToLocation(lat:String, lon:String):Location?{
        if (lat.isEmpty() || lon.isEmpty())
            return null

        return Location(lat)
    }
    */


    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}
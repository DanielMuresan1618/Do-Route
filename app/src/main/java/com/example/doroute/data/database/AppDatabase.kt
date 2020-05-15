package com.example.doroute.data.database
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.doroute.data.database.dao.TaskDAO
import com.example.doroute.data.database.dao.LocationDAO
import com.example.doroute.data.database.dao.StateDAO
import com.example.doroute.data.database.entities.LocationEntity
import com.example.doroute.data.database.entities.StateEntity
import com.example.doroute.data.database.entities.TaskEntity
import com.example.doroute.helpers.Converters


@Database(entities = [TaskEntity::class, LocationEntity::class, StateEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDAO
    abstract fun stateDao(): StateDAO
    abstract fun locationDao(): LocationDAO
}
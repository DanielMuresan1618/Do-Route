package com.example.doroute.data.database
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.doroute.data.database.dao.DAO
import com.example.doroute.data.database.entities.TaskEntity
import com.example.doroute.helpers.Converters


@Database(entities = [TaskEntity::class], version = 9, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): DAO
}
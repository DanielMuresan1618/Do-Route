package com.example.websentinel.data.database
import androidx.room.RoomDatabase
import androidx.room.Database
import com.example.websentinel.domain.TaskModel


@Database(entities = [TaskEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDAO
}
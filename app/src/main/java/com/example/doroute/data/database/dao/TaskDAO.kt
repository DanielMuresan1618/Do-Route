package com.example.doroute.data.database.dao
import androidx.room.*
import com.example.doroute.data.database.entities.TaskEntity


@Dao
interface TaskDAO: GenericDAO<TaskEntity> {
    @Query("SELECT * FROM tasks")
    fun getAll(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE locationId LIKE :id")
    fun get(id:String): TaskEntity
}
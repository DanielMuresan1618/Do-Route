package com.example.doroute.data.database.dao

import androidx.room.*
import com.example.doroute.data.database.entities.TaskEntity
import com.google.android.gms.tasks.Task

@Dao
interface DAO {
    //Simplified DAO

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY  status DESC, due_date DESC")
    fun getAllTasks(): List<TaskEntity>

    @Transaction
    @Query("SELECT * FROM tasks WHERE taskId LIKE :id")
    fun getTask(id:String): TaskEntity

    @Transaction
    @Query("SELECT * FROM tasks WHERE latitude LIKE :latitude AND longitude LIKE :longitude")
    fun getTaskByLocation(latitude:Double, longitude: Double): TaskEntity

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: TaskEntity)

    @Transaction
    @Delete
    fun deleteTask(task: TaskEntity)

    @Transaction
    @Update
    fun updateTask(task: TaskEntity)

}

package com.example.doroute.data.database
import androidx.room.*


@Dao
interface TaskDAO {
    @Query("SELECT * FROM tasks")
    fun getAll(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(taskEntity: TaskEntity)

    @Delete
    fun deleteTask(taskEntity: TaskEntity)

    @Update
    fun updateTask(taskEntity: TaskEntity)
}
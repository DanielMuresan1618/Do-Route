package com.example.websentinel.data.database
import androidx.room.*
import com.example.websentinel.domain.TaskModel


@Dao
interface TaskDAO {
    @Query("SELECT * FROM tasks")
    fun getAll(): List<TaskEntity>

    @Insert
    fun insertTask(taskEntity: TaskEntity)

    @Delete
    fun deleteTask(taskEntity: TaskEntity)

    @Update
    fun updateTask(taskEntity: TaskEntity)
}
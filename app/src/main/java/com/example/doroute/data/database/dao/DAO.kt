package com.example.doroute.data.database.dao

import androidx.room.*
import com.example.doroute.data.database.entities.FullTaskEntity
import com.example.doroute.data.database.entities.LocationEntity
import com.example.doroute.data.database.entities.StateEntity
import com.example.doroute.data.database.entities.TaskEntity

@Dao
interface DAO {
    //Simplified DAO

    @Transaction
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): List<FullTaskEntity>

    @Transaction
    @Query("SELECT * FROM tasks WHERE taskId LIKE :id")
    fun getTask(id:String): FullTaskEntity

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: TaskEntity, locationEntity: LocationEntity, stateEntity: StateEntity)

    @Transaction
    @Delete
    fun deleteTask(task: TaskEntity, locationEntity: LocationEntity, stateEntity: StateEntity)

    @Transaction
    @Update
    fun updateTask(task: TaskEntity, locationEntity: LocationEntity, stateEntity: StateEntity)

}

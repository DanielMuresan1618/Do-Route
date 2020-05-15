package com.example.doroute.data.database.dao

import androidx.room.*
import com.example.doroute.data.database.entities.StateEntity

@Dao
interface StateDAO : GenericDAO<StateEntity> {
    @Query("SELECT * FROM task_states")
    fun getAll(): List<StateEntity>

    @Query("SELECT * FROM task_states WHERE stateId=:id")
    fun get(id: String): StateEntity
}
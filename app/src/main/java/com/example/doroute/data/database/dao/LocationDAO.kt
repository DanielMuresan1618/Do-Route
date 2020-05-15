package com.example.doroute.data.database.dao

import androidx.room.*
import com.example.doroute.data.database.entities.LocationEntity
import com.example.doroute.data.database.entities.TaskEntity

@Dao
interface LocationDAO : GenericDAO<LocationEntity> {
    @Query("SELECT * FROM locations")
    fun getAll(): List<LocationEntity>

    @Query("SELECT * FROM locations WHERE locationId = :id")
    fun get(id: String): LocationEntity
}
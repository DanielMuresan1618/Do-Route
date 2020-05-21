package com.example.doroute.data.domain.stores

import android.util.Log
import com.example.doroute.data.database.AppDatabase
import com.example.doroute.data.database.entities.LocationEntity
import com.example.doroute.data.domain.Repository
import com.example.doroute.data.models.TaskLocation


class LocationDbStore(private val appDatabase: AppDatabase) : Repository<TaskLocation> {

    override fun getAll(): List<TaskLocation> {
        return appDatabase.locationDao().getAll().map{it.toDomainModel()}
    }

     override fun get(locationId:String): TaskLocation {
        Log.d(TAG, "retrieving places")
        return appDatabase.locationDao().get(locationId).toDomainModel()
    }

    override fun add(t: TaskLocation) {
        Log.d(TAG, "adding place")
        appDatabase.locationDao().insert(t.toDbModel())
    }

    override fun remove(t: TaskLocation) {
        Log.d(TAG, "removing place")
        appDatabase.locationDao().delete(t.toDbModel())
    }

    override fun update(t: TaskLocation) {
        Log.d(TAG, "updating place")
        appDatabase.locationDao().update(t.toDbModel())
    }

    private fun TaskLocation.toDbModel() =
        LocationEntity(locationId,latitude,longitude,name,address)

    private fun LocationEntity.toDomainModel() =
        TaskLocation(locationId, latitude, longitude, name, address)

    companion object{
        const val TAG = "LocationDbStore"
    }
}
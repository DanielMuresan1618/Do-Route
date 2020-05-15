package com.example.doroute.data.domain.stores

import android.util.Log
import com.example.doroute.data.database.AppDatabase
import com.example.doroute.data.database.entities.LocationEntity
import com.example.doroute.data.domain.Repository
import com.example.doroute.data.models.LocationModel


class LocationDbStore(private val appDatabase: AppDatabase) : Repository<LocationModel> {

    override fun getAll(): List<LocationModel> {
        return appDatabase.locationDao().getAll().map{it.toDomainModel()}
    }

     override fun get(locationId:String): LocationModel {
        Log.d(TAG, "retrieving places")
        return appDatabase.locationDao().get(locationId).toDomainModel()
    }

    override fun add(t: LocationModel) {
        Log.d(TAG, "adding place")
        appDatabase.locationDao().insert(t.toDbModel())
    }

    override fun remove(t: LocationModel) {
        Log.d(TAG, "removing place")
        appDatabase.locationDao().delete(t.toDbModel())
    }

    override fun update(t: LocationModel) {
        Log.d(TAG, "updating place")
        appDatabase.locationDao().update(t.toDbModel())
    }

    private fun LocationModel.toDbModel() =
        LocationEntity(locationId,latitude,longitude,name,address)

    private fun LocationEntity.toDomainModel() =
        LocationModel(locationId, latitude, longitude, name, address)

    companion object{
        const val TAG = "LocationDbStore"
    }
}
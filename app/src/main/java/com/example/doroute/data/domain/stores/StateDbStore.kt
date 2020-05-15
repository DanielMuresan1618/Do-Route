package com.example.doroute.data.domain.stores

import com.example.doroute.data.database.AppDatabase
import com.example.doroute.data.database.entities.StateEntity
import com.example.doroute.data.domain.Repository
import com.example.doroute.data.models.StateModel

class StateDbStore(private val appDatabase: AppDatabase) : Repository<StateModel> {

    override fun getAll(): List<StateModel> {
        return appDatabase.stateDao().getAll().map { it.toDomainModel() }
    }

    override fun get(id: String): StateModel {
        return appDatabase.stateDao().get(id).toDomainModel()
    }

    override fun add(t: StateModel) {
        appDatabase.stateDao().insert(t.toDbModel())
    }

    override fun remove(t: StateModel) {
        appDatabase.stateDao().delete(t.toDbModel())
    }

    override fun update(t: StateModel) {
        appDatabase.stateDao().update(t.toDbModel())
    }

    private fun StateModel.toDbModel() = StateEntity(stateId,name)
    private fun StateEntity.toDomainModel() = StateModel(stateId,name)

    companion object{
        const val TAG = "StateDbStore"
    }
}
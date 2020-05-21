package com.example.doroute.data.domain.stores

import com.example.doroute.data.database.AppDatabase
import com.example.doroute.data.database.entities.StateEntity
import com.example.doroute.data.domain.Repository
import com.example.doroute.data.models.TaskState

class StateDbStore(private val appDatabase: AppDatabase) : Repository<TaskState> {

    override fun getAll(): List<TaskState> {
        return appDatabase.stateDao().getAll().map { it.toDomainModel() }
    }

    override fun get(id: String): TaskState {
        return appDatabase.stateDao().get(id).toDomainModel()
    }

    override fun add(t: TaskState) {
        appDatabase.stateDao().insert(t.toDbModel())
    }

    override fun remove(t: TaskState) {
        appDatabase.stateDao().delete(t.toDbModel())
    }

    override fun update(t: TaskState) {
        appDatabase.stateDao().update(t.toDbModel())
    }

    private fun TaskState.toDbModel() = StateEntity(stateId,name)
    private fun StateEntity.toDomainModel() = TaskState(stateId,name)

    companion object{
        const val TAG = "StateDbStore"
    }
}
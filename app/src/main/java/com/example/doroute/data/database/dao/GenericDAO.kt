package com.example.doroute.data.database.dao

import androidx.room.*

@Dao
interface GenericDAO<T> {
    //can't use generic queries

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(t: T)

    @Delete
    fun delete(t: T)

    @Update
    fun update(t: T)
}

package com.example.doroute.data.database
import android.content.Context
import androidx.room.Room

object RoomDatabase { //singleton; only returns an AppDatabase
    //I used fallbackToDestructiveMigration() to delete the previous db to avoid unnecessarily complex migration
    private var appDatabase: AppDatabase? = null

    fun getDb(context: Context): AppDatabase {
        if (appDatabase == null)
            appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "doroute-db")
                .allowMainThreadQueries()
                .build()

        return appDatabase!!
    }
}
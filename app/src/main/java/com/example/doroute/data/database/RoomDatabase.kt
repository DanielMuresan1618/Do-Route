package com.example.doroute.data.database
import android.content.Context
import androidx.room.Room

object RoomDatabase { //singleton; only returns an AppDatabase
    //will never be changed for this app!
    private var appDatabase: AppDatabase? = null

    fun getDb(context: Context): AppDatabase {
        if (appDatabase == null)
            appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "doroute-db")
                .allowMainThreadQueries()
                .build()

        return appDatabase!!
    }
}
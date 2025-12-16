package com.dickel.zeiterfassung.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dickel.zeiterfassung.data.local.dao.CustomerDao
import com.dickel.zeiterfassung.data.local.dao.ServicescheinDao
import com.dickel.zeiterfassung.data.local.dao.TimeEventDao
import com.dickel.zeiterfassung.data.local.entity.Customer
import com.dickel.zeiterfassung.data.local.entity.Serviceschein
import com.dickel.zeiterfassung.data.local.entity.TimeEvent

@Database(
    entities = [TimeEvent::class, Customer::class, Serviceschein::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timeEventDao(): TimeEventDao
    abstract fun customerDao(): CustomerDao
    abstract fun servicescheinDao(): ServicescheinDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dickel_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

package com.calsnap.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.calsnap.app.data.local.dao.FoodLogDao
import com.calsnap.app.data.local.entity.FoodLogEntity

@Database(
    entities = [FoodLogEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CalSnapDatabase : RoomDatabase() {
    abstract fun foodLogDao(): FoodLogDao
}

package com.calsnap.app.di

import android.content.Context
import androidx.room.Room
import com.calsnap.app.data.local.CalSnapDatabase
import com.calsnap.app.data.local.dao.FoodLogDao
import com.calsnap.app.data.repository.FoodRepositoryImpl
import com.calsnap.app.data.repository.UserRepositoryImpl
import com.calsnap.app.domain.repository.FoodRepository
import com.calsnap.app.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): CalSnapDatabase =
        Room.databaseBuilder(ctx, CalSnapDatabase::class.java, "calsnap.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideFoodLogDao(db: CalSnapDatabase): FoodLogDao = db.foodLogDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindFoodRepository(impl: FoodRepositoryImpl): FoodRepository
}

package com.yummybiteadmin.foodapp.di

import com.yummybiteadmin.foodapp.FirebaseManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
//    @Provides
//    @Singleton
//    fun provideFirebaseManager(): FirebaseManager {
//        return FirebaseManager()
//    }
}
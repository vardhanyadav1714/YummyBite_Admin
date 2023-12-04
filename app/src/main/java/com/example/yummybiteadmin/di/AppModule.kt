package com.example.yummybiteadmin.di

import com.example.yummybiteadmin.FirebaseManager
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
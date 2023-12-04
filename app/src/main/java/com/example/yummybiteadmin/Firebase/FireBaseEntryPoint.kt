package com.example.yummybiteadmin.Firebase

import com.example.yummybiteadmin.FirebaseManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// Your FirebaseManagerEntryPoint interface
@EntryPoint
@InstallIn(SingletonComponent::class)
interface FirebaseManagerEntryPoint {
    fun firebaseManager(): FirebaseManager
}
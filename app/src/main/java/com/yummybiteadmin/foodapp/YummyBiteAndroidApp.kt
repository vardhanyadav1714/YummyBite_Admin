package com.yummybiteadmin.foodapp

import android.app.Application
import android.util.Log
import com.yummybiteadmin.foodapp.model.Food
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@HiltAndroidApp
class YummyBiteAndroidApp : Application() {
    companion object {
        lateinit var instance: YummyBiteAndroidApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}




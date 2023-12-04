package com.example.yummybiteadmin

import android.content.Context
import android.util.Log
import com.example.yummybiteadmin.model.Food
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import javax.inject.Inject

// Your FirebaseManager class
class FirebaseManager @Inject constructor() {
    init {
        FirebaseApp.initializeApp(YummyBiteAndroidApp.instance)
    }

    fun getDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("dishes")
    }

    fun getStorageReference(): StorageReference {
        return FirebaseStorage.getInstance().reference.child("dish_images")
    }
    fun saveDishToFirebase(food: Food) {
        val user = Firebase.auth.currentUser
        val db = FirebaseFirestore.getInstance()

        user?.uid?.let { userId ->
            db.collection("users").document(userId).collection("dishes")
                .add(food)
                .addOnSuccessListener { documentReference ->
                    Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error adding document", e)
                }
        }
    }
    fun deleteDishFromFirebase(food: Food) {
        val user = Firebase.auth.currentUser
        val db = FirebaseFirestore.getInstance()

        user?.uid?.let { userId ->
            db.collection("users").document(userId).collection("dishes")
                .document(food.name) // Assuming 'name' is a unique identifier in this case
                .delete()
                .addOnSuccessListener {
                    Log.d("Firestore", "DocumentSnapshot successfully deleted!")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error deleting document", e)
                }
        }

    }
    fun deleteAllDishesFromFirebase() {
        val user = Firebase.auth.currentUser
        val db = FirebaseFirestore.getInstance()

        user?.uid?.let { userId ->
            val dishesCollection = db.collection("users").document(userId).collection("dishes")

            dishesCollection
                .get()
                .addOnSuccessListener { querySnapshot ->
                    // Iterate through all documents and delete them
                    for (document in querySnapshot.documents) {
                        document.reference.delete()
                            .addOnSuccessListener {
                                Log.d("Firestore", "Document successfully deleted!")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Error deleting document", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error getting documents", e)
                }
        }
    }




}
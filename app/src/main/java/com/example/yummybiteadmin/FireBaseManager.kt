package com.example.yummybiteadmin

import android.util.Log
import com.example.yummybiteadmin.model.Food
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import javax.inject.Inject

class FirebaseManager @Inject constructor() {

    fun getStorageReference(): StorageReference {
        return FirebaseStorage.getInstance().reference.child("dish_images")
    }

    fun saveDishToFirebase(food: Food) {
        val user = Firebase.auth.currentUser
        if (user != null) {
            Log.d("Firestore", "User ID: ${user.uid}")

            val db = FirebaseFirestore.getInstance()

            // Create the root collection "dishes"
            val dishesCollection = db.collection("dishes")

            // Under each user's UID, create a subcollection "user_dishes"
            val userDishesCollection = dishesCollection.document(user.uid).collection("user_dishes")

            // Check if the dish with the given name already exists
            userDishesCollection
                .whereEqualTo("name", food.name)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        // Dish doesn't exist, add it
                        userDishesCollection.add(food)
                            .addOnSuccessListener { documentReference ->
                                Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Error adding document", e)
                            }
                    } else {
                        Log.w("Firestore", "Dish with name ${food.name} already exists")
                        // Handle the case where the dish already exists
                        // You can update the existing dish or handle it according to your requirements
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error checking dish existence", e)
                }
        } else {
            Log.w("Firestore", "User is not authenticated")
        }
    }


    fun deleteDishFromFirebase(food: Food) {
        val user = Firebase.auth.currentUser
        val db = FirebaseFirestore.getInstance()

        user?.uid?.let { userId ->
            val dishesCollection = db.collection("dishes")
            val userDishesCollection = dishesCollection.document(userId).collection("user_dishes")

            userDishesCollection.document(food.name) // Assuming 'name' is a unique identifier in this case
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
            val dishesCollection = db.collection("dishes")
            val userDishesCollection = dishesCollection.document(userId).collection("user_dishes")

            userDishesCollection
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

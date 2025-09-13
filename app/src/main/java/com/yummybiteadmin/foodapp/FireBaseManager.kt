package com.yummybiteadmin.foodapp

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import com.yummybiteadmin.foodapp.model.Food
 import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class FirebaseManager @Inject constructor() {

    fun getStorageReference(): StorageReference {
        return FirebaseStorage.getInstance().reference.child("dish_images")
    }
    fun updateDishFromFirebase(food: Food) {
        val user = Firebase.auth.currentUser
        if (user != null) {
            Log.d("Firestore", "User ID: ${user.uid}")

            val db = FirebaseFirestore.getInstance()

            // Create the root collection "admin"
            val dishesCollection = db.collection("admin")

            // Under each user's UID, create a subcollection "adminDishes"
            val userDishesCollection = dishesCollection.document(user.uid).collection("adminDishes")

            // Check if the dish with the given name already exists
            userDishesCollection
                .whereEqualTo("id", food.id)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val existingFood = document.toObject(Food::class.java)
                        if (existingFood != null && existingFood.id == food.id) {
                            // If dish with the same name and ID exists, update it using the document ID
                            userDishesCollection.document(document.id).set(food)
                                .addOnSuccessListener {
                                    Log.d("Firestore", "Document updated successfully")
                                    // If dish is successfully updated, also update it in the common collection "all_dishes"
                                    updateToAllDishesCollection(food)
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Firestore", "Error updating document", e)
                                }
                            return@addOnSuccessListener
                        }
                    }
                    // If no existing dish found with the same name and ID, log an error
                    Log.e("Firestore", "No existing dish found with the same name and ID")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error querying documents", e)
                }
        } else {
            Log.w("Firestore", "User is not authenticated")
        }
    }
    fun saveDishToFirebase(food: Food) {
        val user = Firebase.auth.currentUser
        if (user != null) {
            Log.d("Firestore", "User ID: ${user.uid}")

            val db = FirebaseFirestore.getInstance()

            // Create the root collection "dishes"
            val dishesCollection = db.collection("admin")

            // Under each user's UID, create a subcollection "adminDishes"
            val userDishesCollection = dishesCollection.document(user.uid).collection("adminDishes")

            // Check if the dish with the given name already exists
            userDishesCollection
                .whereEqualTo("name", food.name)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        // Dish doesn't exist, add it
                        userDishesCollection.add(food)
                            .addOnSuccessListener { documentReference ->
                                Log.d(
                                    "Firestore",
                                    "DocumentSnapshot added with ID: ${documentReference.id}"
                                )

                                // If dish is successfully saved, also save it to the common collection "all_dishes"
                                saveToAllDishesCollection(food)
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

    private fun saveToAllDishesCollection(food: Food) {
        val db = FirebaseFirestore.getInstance()

        // Create the root collection "all_dishes_and_orders"
        val allDishesAndOrdersCollection = db.collection("all_dishes_and_orders")

        // Under "all_dishes_and_orders", create a subcollection "all_dishes"
        val allDishesCollection =
            allDishesAndOrdersCollection.document("all_dishes").collection("dishes")

        // Save the dish to "all_dishes" collection
        allDishesCollection.add(food)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Dish added to 'all_dishes' with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding dish to 'all_dishes'", e)
            }
    }

    suspend fun saveUserInformation(
        email: String, username: String, phone: String, vendor: String,
        callback: (Boolean) -> Unit
    ) {
        try {

            val user = Firebase.auth.currentUser
            if (user != null) {
                val db = FirebaseFirestore.getInstance()
                db.collection("admin").document(user.uid).collection("adminDetails")
                    .document("adminInfo")
                    .set(
                        mapOf(
                            "email" to email,
                            "username" to username,
                            "phone" to phone,
                            "vendor" to vendor
                        )
                    ).await()
                callback(true)

            } else {
                callback(false)
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error saving user information: $e")
            e.printStackTrace()
            callback(false) // Callback indicating failure (exception occurred)
        }
    }

    suspend fun getUserVendor(callback: (String?) -> Unit) {
        try {
            val user = Firebase.auth.currentUser
            if (user != null) {
                val firebaseInstance = FirebaseFirestore.getInstance()
                val documentSnapshot = firebaseInstance.collection("admin").document(user.uid)
                    .collection("adminDetails").document("adminInfo").get().await()


                if (documentSnapshot.exists()) {
                    val vendor = documentSnapshot.getString("vendor")
                    callback(vendor)
                } else {

                    callback(null)
                }
            } else {

                callback(null)
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error retrieving user vendor information: $e")
            e.printStackTrace()
            callback(null)
        }
    }

    suspend fun getUserDetails(callback: (Map<String, Any>?) -> Unit) {
        try {
            val user = Firebase.auth.currentUser
            if (user != null) {
                val firestoreInstance = FirebaseFirestore.getInstance()
                // Fetch user details from Firestore
                val documentSnapshot = firestoreInstance
                    .collection("admin")
                    .document(user.uid)
                    .collection("adminDetails")
                    .document("adminInfo") // Use a specific document ID if needed
                    .get()
                    .await()

                if (documentSnapshot.exists()) {
                    val userDetails = documentSnapshot.data
                    callback(userDetails)
                } else {
                    callback(null) // No user details found
                }
            } else {
                callback(null) // User is not authenticated
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching user details: $e")
            e.printStackTrace()
            callback(null) // Error occurred while fetching user details
        }
    }




    suspend fun deleteDishFromFirebase(food: Food) {
        val user = Firebase.auth.currentUser
        val db = FirebaseFirestore.getInstance()

        user?.uid?.let { userId ->
            val dishesCollection = db.collection("admin")
            val userDishesCollection = dishesCollection.document(userId).collection("adminDishes")

            // Get the document ID for the user's dish
            val userDocumentSnapshot = userDishesCollection.whereEqualTo("id", food.id).get().await()
            val userDocumentId = userDocumentSnapshot.documents.firstOrNull()?.id

            if (userDocumentId != null) {
                // Delete the user's dish using the document ID
                userDishesCollection.document(userDocumentId)
                    .delete()
                    .addOnSuccessListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            deleteFoodFromAllDishesCollection(food)
                        }
                        Log.d("Firestore", "DocumentSnapshot successfully deleted from user's dishes: $userDocumentId")
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error deleting document from user's dishes", e)
                    }
            }
        }
    }

    private suspend fun deleteFoodFromAllDishesCollection(food: Food) {
        val db = FirebaseFirestore.getInstance()

        val allDishesCollection = db.collection("all_dishes_and_orders")
            .document("all_dishes")
            .collection("dishes")

        // Get the document ID for the all dishes collection
        val allDishesDocumentSnapshot = allDishesCollection.whereEqualTo("id", food.id).get().await()
        val allDishesDocumentId = allDishesDocumentSnapshot.documents.firstOrNull()?.id

        if (allDishesDocumentId != null) {
            // Delete the dish from all dishes collection using the document ID
            allDishesCollection.document(allDishesDocumentId)
                .delete()
                .addOnSuccessListener {
                    Log.d("Firestore", "Dish deleted successfully from all dishes: $allDishesDocumentId")
                }
                .addOnFailureListener { e ->
                    Log.d("Firestore", "Failed to delete the dish from all dishes", e)
                }
        }
    }


    suspend fun getAllOrders(): List<Order> {
        val user = Firebase.auth.currentUser
        return try {
            Log.d("Firestore", "Fetching all orders...")

            val db = FirebaseFirestore.getInstance()

            val allOrdersCollection = db.collection("all_dishes_and_orders")
                .document("all_dishes")
                .collection("orders").get().await()


            val ordersList = mutableListOf<Order>()

            for (document in allOrdersCollection.documents) {
                val order = document.toObject(Order::class.java)
                order?.let {
                    if (it.vendorUserId == user?.uid) {
                        ordersList.add(it)
                    }
                }
            }

            Log.d("Firestore", "Fetched ${ordersList.size} orders successfully.")
            ordersList
        } catch (e: FirebaseFirestoreException) {
            Log.e("Firestore", "Firestore exception while fetching orders: $e")
            e.printStackTrace()
            emptyList()
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching all orders: $e")
            e.printStackTrace()
            emptyList()
        }
    }
    suspend fun saveDeliveredOrder() {
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            val db = FirebaseFirestore.getInstance()

            // Reference to the original order collection
            val allOrdersCollection = db.collection("all_dishes_and_orders")
                .document("all_dishes")
                .collection("orders")

            try {
                // Retrieve orders matching the criteria
                val querySnapshot = allOrdersCollection
                    .whereEqualTo("orderPayment", OrderPayment.Paid.name)
                    .whereEqualTo("orderStatus", OrderStatus.Delivered.name)
                    .get()
                    .await()

                for (document in querySnapshot.documents) {
                    val order = document.toObject(Order::class.java)
                    order?.let {
                        if (order.vendorUserId == currentUser.uid) {
                            // Save the order to the admin order collection
                            saveOrderToAdminCollection(currentUser.uid, order)
                            saveOrderToAllOrderAndDishes(currentUser.uid,order)
                            // Delete the order from the original collection
                            allOrdersCollection.document(document.id).delete().await()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Firestore", "Error saving delivered order", e)
            }
        } else {
            Log.w("Firestore", "User is not authenticated")
        }
    }

    private suspend fun saveOrderToAdminCollection(adminUid: String, order: Order) {
        try {
            val db = FirebaseFirestore.getInstance()

            // Reference to the admin order collection
            val adminOrderCollection = db.collection("admin")
                .document(adminUid)
                .collection("adminDeliveredOrders")

            // Save the order to the admin order collection
            adminOrderCollection.add(order).await()

            Log.d("Firestore", "Order saved to admin collection successfully")
        } catch (e: Exception) {
            Log.e("Firestore", "Error saving order to admin collection", e)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    suspend fun getDeliveredOrderFromAdminCollection(): Flow<List<Order>> = flow {
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val orderCollection = db.collection("admin")
                .document(userId)
                .collection("adminDeliveredOrders")
                  Log.d("Firestore","You are inside the firebase collection")
            try {
                val querySnapshot = orderCollection.get().await()
                val orderList = mutableListOf<Order>()
                for (doc in querySnapshot.documents) {
                    val order = doc.toObject<Order>()
                    if (order != null) {
                        orderList.add(order)
                        Log.d("Firestore","Order has ")

                    }
                }
                emit(orderList)
            } catch (e: Exception) {
                Log.e("Firestore", "Error getting delivered orders", e)
                // Handle error
            }
        }
    }

    private suspend fun saveOrderToAllOrderAndDishes(adminUid: String, order: Order) {
        try {
            val db = FirebaseFirestore.getInstance()

            val ordersCollection = db.collection("all_dishes_and_orders")
                .document("all_dishes")
                .collection("deliveredOrders")


            // Save the order to the admin order collection
            ordersCollection.add(order).await()

            Log.d("Firestore", "Order saved to admin collection successfully")
        } catch (e: Exception) {
            Log.e("Firestore", "Error saving order to admin collection", e)
        }
    }
    suspend fun updateOrder(orderId: String, orderStatus: OrderStatus, orderPayment: OrderPayment) {
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            val db = FirebaseFirestore.getInstance()
            Log.d("Firestore", "You are inside the Firebase")

            // Create a reference to the order collection
            val ordersCollection = db.collection("all_dishes_and_orders")
                .document("all_dishes")
                .collection("orders")

            // Query for the order document by orderId
            ordersCollection
                .whereEqualTo("orderId", orderId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        // Get the document ID
                        val documentId = document.id

                        // Create a map containing the fields to be updated
                        val updates = mapOf(
                            "orderStatus" to orderStatus.name,
                            "orderPayment" to orderPayment.name
                        )

                        // Update the specific fields of the order document
                        ordersCollection.document(documentId).update(updates)
                            .addOnSuccessListener {
                                CoroutineScope(Dispatchers.Default).launch {
                                    saveDeliveredOrder()
                                }
                                Log.d("Firestore", "Order updated successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Error updating order", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error querying documents", e)
                }

        } else {
            Log.w("Firestore", "User is not authenticated")
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
    private fun updateToAllDishesCollection(food: Food) {
        val db = FirebaseFirestore.getInstance()

        // Create a reference to the specific document in "all_dishes_and_orders/all_dishes"
        val dishesCollection = db.collection("all_dishes_and_orders")
            .document("all_dishes")
            .collection("dishes")

        // Check if the dish with the given ID already exists
        dishesCollection.whereEqualTo("id", food.id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val existingFood = document.toObject(Food::class.java)
                    if (existingFood != null && existingFood.id == food.id) {
                        // If dish with the same ID exists, update it using the document ID
                        dishesCollection.document(document.id).set(food)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Document updated successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Error updating document", e)
                            }
                        return@addOnSuccessListener
                    }
                }
                // If no existing dish found with the same ID, log an error
                Log.e("Firestore", "No existing dish found with the same ID")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error querying documents", e)
            }
    }




}
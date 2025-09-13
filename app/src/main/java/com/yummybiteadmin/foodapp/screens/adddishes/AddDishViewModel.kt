package com.yummybiteadmin.foodapp.screens.adddishes

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yummybiteadmin.foodapp.FirebaseManager
import com.yummybiteadmin.foodapp.Order
import com.yummybiteadmin.foodapp.model.Food
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddDishViewModel @Inject constructor(
    private val firebaseManager: FirebaseManager
) : ViewModel() {

    private val _selectedFood: MutableLiveData<Food?> = MutableLiveData()
    val selectedFood: LiveData<Food?> get() = _selectedFood

    fun setSelectedFood(food: Food?) {
        _selectedFood.value = food
    }

    private val _selectedOrder:MutableLiveData<Order?> = MutableLiveData()
    val selectedOrder:LiveData<Order?> get() = _selectedOrder

    fun setSelectedOrder(order:Order?){
        _selectedOrder.value=order
    }
    fun saveDishToFirebase(food: Food) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseManager.saveDishToFirebase(food)
        }
    }
    fun deleteDishFromFirebase(food: Food) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                firebaseManager.deleteDishFromFirebase(food)
                // Update UI or any other necessary actions after deletion
                _selectedFood.postValue(null)
            } catch (e: Exception) {
                // Handle the error, log it for debugging
                Log.e("DeleteDish", "Error deleting dish", e)
            }
        }
    }
    private val _userVendor=MutableStateFlow<String?>("")
    val userVendor :StateFlow<String?> get() = _userVendor
    fun getUserbyVendor(){
         viewModelScope.launch {
             firebaseManager.getUserVendor {
                 _userVendor.value=it
             }
         }

    }
    fun deleteAllDishFromFirebase() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Call the function to delete all dishes
                firebaseManager.deleteAllDishesFromFirebase()

                // Update UI or any other necessary actions after deletion
                _selectedFood.postValue(null)
            } catch (e: Exception) {
                // Handle the error, log it for debugging
                Log.e("DeleteDish", "Error deleting dish", e)
            }
        }
    }
    private val _allorders = MutableStateFlow<List<Order>>(emptyList())
    val allorders:StateFlow<List<Order>> = _allorders
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    fun fetchAllOrders(){
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val order = firebaseManager.getAllOrders()
                _allorders.value= order
            }catch (e: Exception) {
                // Log or handle the exception
                e.printStackTrace()
            } finally {
                _isLoading.value = false

            }
        }
    }

    fun isDishNameExists(dishName: String): Boolean {
        var isExists = false
        val user = Firebase.auth.currentUser

        user?.uid?.let { userId ->
            val db = FirebaseFirestore.getInstance()
            val dishesCollection = db.collection("users").document(userId).collection("dishes")

            // Check if a dish with the given name exists in the Firestore collection
            dishesCollection
                .whereEqualTo("name", dishName)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    // If there are any documents in the query result, it means the dish name exists
                    isExists = !querySnapshot.isEmpty
                }
                .addOnFailureListener { exception ->
                    // Handle the error
                    Log.e("Firestore", "Error checking dish name existence", exception)
                }
        }

        return isExists
    }

    fun updateDish(food: Food){
        viewModelScope.launch {
            firebaseManager.updateDishFromFirebase(food)
        }
    }
    fun uploadImageToFirebaseStorage(imageUri: Uri, callback: (String) -> Unit) {
        val storageRef = Firebase.storage.reference
        val imagesRef = storageRef.child("images/${UUID.randomUUID()}")
        val uploadTask = imagesRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imagesRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                callback(downloadUri.toString())
            } else {
                // Handle the error
                Log.e("FirebaseStorage", "Error uploading image: ${task.exception}")
            }
        }
    }
    fun updateOrder(order:Order){
        viewModelScope.launch {

            try {
                firebaseManager.updateOrder(order.orderId,order.orderStatus,order.orderPayment)

            }catch (ex:Exception){
                ex.printStackTrace()
            }
        }
    }
    fun deleteDish(food: Food){
        viewModelScope.launch {
            try {
                firebaseManager.deleteDishFromFirebase(food)
            }catch (ex:Exception){
                ex.printStackTrace()
            }
        }
    }

}
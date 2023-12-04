package com.example.yummybiteadmin.screens.adddishes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummybiteadmin.FirebaseManager
import com.example.yummybiteadmin.model.Food
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

}
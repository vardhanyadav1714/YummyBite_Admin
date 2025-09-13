package com.yummybiteadmin.foodapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase


class DishViewModel:ViewModel() {
    private val _dishes: MutableLiveData<List<Dish>> = MutableLiveData()
    val dishes: LiveData<List<Dish>> get() = _dishes

    fun addDishToFirebase(dish: Dish) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("dishes")

        val newDishReference = reference.push()
        newDishReference.setValue(dish)
            .addOnSuccessListener {
                // Dish added successfully
                //Toast.makeText(LocalContext.current,"Dish Saved Successfully",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // Handle failure to add dish
            }
    }

}
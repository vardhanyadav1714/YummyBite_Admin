package com.yummybiteadmin.foodapp.screens.profile

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yummybiteadmin.foodapp.FirebaseManager
import com.yummybiteadmin.foodapp.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(private val firebaseManager: FirebaseManager):ViewModel
    (){
    private val _userDetails = MutableStateFlow<Map<String, Any>?>(null)
    val userDetails: StateFlow<Map<String, Any>?> get() = _userDetails
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun getUserDetails() {
        viewModelScope.launch {
            _isLoading.value = true // Set loading state to true before fetching user details
            firebaseManager.getUserDetails { userDetails ->
                _userDetails.value = userDetails
                _isLoading.value=false
                // Set loading state back to false after fetching user details
            }
        }
    }

    private val _deliveredOrders = MutableStateFlow<List<Order>>(emptyList())
    val deliveredOrders: StateFlow<List<Order>> = _deliveredOrders
    fun fetchDeliveredOrders() {
        viewModelScope.launch {
            try {
                 _isLoading.value = true

                 firebaseManager.getDeliveredOrderFromAdminCollection()
                    .collect { orders ->
                         _deliveredOrders.value = orders

                         _isLoading.value = false
                    }

            } catch (e: Exception) {
                 Log.e("FetchDeliveredOrders", "Error fetching orders", e)

                 _isLoading.value = false
            }
        }
    }



}

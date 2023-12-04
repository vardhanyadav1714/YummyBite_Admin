package com.example.yummybiteadmin.model
import com.google.firebase.firestore.PropertyName

data class Food(
    var id: String = "",

    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",

    @get:PropertyName("price")
    @set:PropertyName("price")
    var price: String = "",

    @get:PropertyName("imageUrl")
    @set:PropertyName("imageUrl")
    var imageUrl: String = "",

    @get:PropertyName("availability")
    @set:PropertyName("availability")
    var availability: Boolean = false,

    @get:PropertyName("vendor")
    @set:PropertyName("vendor")
    var vendor: String = ""
)


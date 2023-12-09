package com.example.travlog.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Travlogs(
    val userId:String = "",
    val title:String = "",
    val description:String = "",
    val image:String = "",
    //val location: GeoPoint = "",
    val createdOn: Timestamp = Timestamp.now(),
    val modifiedOn: Timestamp = Timestamp.now(),
    val documentId: String = "",
)

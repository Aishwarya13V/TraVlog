package com.example.travlog.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.travlog.models.Travlogs
import com.example.travlog.repository.TravlogRepository
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser

class DetailViewModel(
    private val repository: TravlogRepository
):ViewModel() {
    var detailUiState by mutableStateOf(DetailUiState())
        private set

    private val hasUser:Boolean
        get() =repository.hasUser()

    private val user:FirebaseUser?
        get() = repository.user

    fun onTitleChange(title: String){
        detailUiState = detailUiState.copy(title = title)
    }

    fun onDescriptionChange(description: String){
        detailUiState = detailUiState.copy(description = description)
    }

    fun addTravlog(){
        if(hasUser){
            repository.addTravlog(
                userId = user!!.uid,
                title = detailUiState.title,
                description = detailUiState.description,
                image = "",
                createdOn = Timestamp.now()
            ){
                detailUiState = detailUiState.copy(travlogAddedStatus = it)
            }
        }
    }

    fun setEditFields(travlog: Travlogs){
        detailUiState = detailUiState.copy(
            title = travlog.title,
            description = travlog.description
        )
    }

    fun getTravlog(travlogId:String){
        repository.getTravlog(
            travlogId = travlogId,
            onError = {},
        ){
            detailUiState = detailUiState.copy(selectedTravlog = it)
            detailUiState.selectedTravlog?.let { it1 -> setEditFields(it1) }
        }
    }

    fun updateTravlog(
        travlogId:String
    ){
        repository.updateTravlog(
            title = detailUiState.title,
            description = detailUiState.description,
            travlogId = travlogId,
        ){
            detailUiState = detailUiState.copy(updateTravlogStatus = it)
        }
    }

    fun resetTravlogAddedStatus(){
        detailUiState = detailUiState.copy(
            travlogAddedStatus = false,
            updateTravlogStatus = false,
        )
    }

    fun resetState(){
        detailUiState = DetailUiState()
    }

}

data class DetailUiState(
    val title:String = "",
    val description:String = "",
    val travlogAddedStatus:Boolean = false,
    val updateTravlogStatus:Boolean = false,
    val selectedTravlog: Travlogs? = null
)
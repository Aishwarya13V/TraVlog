package com.example.travlog.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travlog.models.Travlogs
import com.example.travlog.repository.Resources
import com.example.travlog.repository.TravlogRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: TravlogRepository = TravlogRepository()
): ViewModel() {
    var homeUiState by mutableStateOf(HomeUiState())
    val  user = repository.user()
    val hasUser:Boolean
        get() = repository.hasUser()
    private val userId: String
        get() = repository.getUserId()

    fun loadTravlogs(){
        if(hasUser){
            if(userId.isNotBlank()){
                getUserTravlogs(userId)
            }else{
                homeUiState = homeUiState.copy(travlogList = Resources.Error(
                    throwable = Throwable(message = "User is not logged in")
                ))
            }
        }
    }

    private fun getUserTravlogs(userId:String) = viewModelScope.launch {
        repository.getUserTravlogs(userId).collect{
            homeUiState = homeUiState.copy(travlogList = it)
        }
    }

    fun deleteTravlog(travlogId:String) = repository.deleteTravlog(travlogId){
        homeUiState = homeUiState.copy(travlogDeletedStatus =  it)
    }

    fun signOut() = repository.signOut()

}

data class HomeUiState(
    val travlogList:Resources<List<Travlogs>> = Resources.Loading(),
    val travlogDeletedStatus:Boolean = false
)
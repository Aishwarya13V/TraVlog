package com.example.travlog.detail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel,
    travlogId:String,
    onNavigate:() -> Unit
){
    val detailUiState = detailViewModel?.detailUiState?: DetailUiState()

    val isFormNotBlank = detailUiState.title.isNotBlank() &&
            detailUiState.description.isNotBlank()

    val isTravlogIdNotBlank = travlogId.isNotBlank()

    val icon = if(isFormNotBlank) Icons.Default.Refresh
            else Icons.Default.Check

    LaunchedEffect(key1 = Unit){
        if(isTravlogIdNotBlank){
            detailViewModel?.getTravlog(travlogId)
        }else{
            detailViewModel?.resetState()
        }
    }

    val scope = rememberCoroutineScope()


}
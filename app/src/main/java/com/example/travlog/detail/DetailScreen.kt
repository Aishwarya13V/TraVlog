package com.example.travlog.detail

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.travlog.login.SignUpScreen
import com.example.travlog.ui.theme.TraVlogTheme
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel? = null,
    travlogId:String,
    onNavigate:() -> Unit
){
    val detailUiState = detailViewModel?.detailUiState?: DetailUiState()

    val isFormNotBlank = detailUiState.title.isNotBlank() &&
            detailUiState.description.isNotBlank()

    val isTravlogIdNotBlank = travlogId.isNotBlank()

    val icon = if(isTravlogIdNotBlank) Icons.Default.Refresh
            else Icons.Default.Check

    LaunchedEffect(key1 = Unit){
        if(isTravlogIdNotBlank){
            detailViewModel?.getTravlog(travlogId)
        }else{
            detailViewModel?.resetState()
        }
    }

    val scope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF6650a4),
                    titleContentColor = Color.White,
                ),
                title = {
                    Text(
                        "My TraVlogs",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigate.invoke() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (isTravlogIdNotBlank) {
                        IconButton(onClick = { detailViewModel?.deleteTravlog(travlogId) }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Localized description",
                                tint = Color.White
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if(isTravlogIdNotBlank){
                    detailViewModel?.updateTravlog(travlogId)
                }else{
                    detailViewModel?.addTravlog()
                }
            }, containerColor = Color(0xFF6650a4)) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(padding)
        ) {
            if(detailUiState.travlogAddedStatus){
                scope.launch {
                    snackbarHostState.showSnackbar("TraVlog Added Successfully")
                    detailViewModel?.resetTravlogAddedStatus()
                    onNavigate.invoke()
                }
            }
            if(detailUiState.updateTravlogStatus){
                scope.launch {
                    snackbarHostState.showSnackbar("TraVlog Updated Successfully")
                    detailViewModel?.resetTravlogAddedStatus()
                    onNavigate.invoke()
                }
            }
            if(detailUiState.deleteTravlogStatus){
                scope.launch {
                    snackbarHostState.showSnackbar("TraVlog Deleted Successfully")
                    detailViewModel?.resetTravlogAddedStatus()
                    onNavigate.invoke()
                }
            }

            OutlinedTextField(
                value = detailUiState.title,
                onValueChange = {
                    detailViewModel?.onTitleChange(it)
                },
                label = { Text(text = "Title")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            OutlinedTextField(
                value = detailUiState.description,
                onValueChange = {detailViewModel?.onDescriptionChange(it)},
                label = { Text(text = "Description")},
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PrevDetailScreen(){
    TraVlogTheme {
        DetailScreen(detailViewModel = null, travlogId = "") {
            
        }
    }
}
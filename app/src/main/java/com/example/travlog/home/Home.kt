package com.example.travlog.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.travlog.models.Travlogs
import com.example.travlog.repository.Resources
import com.example.travlog.ui.theme.TraVlogTheme
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    homeViewModel: HomeViewModel?,
    onTravlogClick:(id:String) -> Unit,
    navToDetailPage:() -> Unit,
    navToLoginPage:() -> Unit,
){
    val homeUiState = homeViewModel?.homeUiState?: HomeUiState()

    var openDialog by remember { mutableStateOf(false) }

    var selectedTravlog by remember { mutableStateOf<Travlogs?>(null) }

    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(key1 = Unit){
        homeViewModel?.loadTravlogs()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navToDetailPage.invoke() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "My TraVlogs",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = {
                        homeViewModel?.signOut()
                        navToLoginPage.invoke()
                    }) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)

                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) {padding ->
        Column(modifier = Modifier.padding(padding)) {
            when(homeUiState.travlogList){
                is Resources.Loading -> {
                    CircularProgressIndicator(
                       modifier = Modifier
                           .fillMaxSize()
                           .wrapContentSize(align = Alignment.Center)
                    )
                }
                is Resources.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                    ){
                        items((homeUiState.travlogList.data ?: emptyList<Travlogs>()).size)
                        { index->
                            val travlog = homeUiState.travlogList.data?.get(index)
                            if (travlog != null) {
                                TravlogItem(
                                    travlogs = travlog,
                                    onLongClick = {
                                        openDialog = true
                                        selectedTravlog = travlog
                                    }
                                ) {
                                    onTravlogClick.invoke(travlog.documentId)
                                }
                            }
                        }
                    }
                    AnimatedVisibility(visible = openDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                openDialog = false
                            },
                            title = { Text(text = "Delete Travlog?")},
                            confirmButton = {
                                Button(
                                    onClick = {
                                        selectedTravlog?.documentId.let {
                                            if (it != null) {
                                                homeViewModel?.deleteTravlog(it)
                                            }
                                        }
                                    },
                                ) {
                                    Text(text = "Delete")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { openDialog = false }) {
                                    Text(text = "Cancel")
                                }
                            }
                        )
                    }
                }

                else -> {
                    Text(
                        text = homeUiState.travlogList.throwable?.localizedMessage
                            ?: "Unkown Error",
                        color = Color.Red
                    )
                }
            }

        }

    }
    LaunchedEffect(key1 = homeViewModel?.hasUser){
        if(homeViewModel?.hasUser == false){
            navToLoginPage.invoke()
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TravlogItem(
    travlogs: Travlogs,
    onLongClick:() -> Unit,
    onClick:() -> Unit
){
    Card(
        modifier = Modifier
            .combinedClickable(
                onLongClick = { onLongClick.invoke() },
                onClick = { onClick.invoke() }
            )
            .padding(8.dp)
            .fillMaxWidth(),
    ){
        Column {
            Text(
                text = travlogs.title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier.padding(4.dp)
            )

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = travlogs.description,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(4.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = formatDate(travlogs.createdOn),
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.End),
                maxLines = 4
            )
        }
    }
}

private fun formatDate(timestamp: com.google.firebase.Timestamp):String{
    val sdf = SimpleDateFormat("MM-dd-yy hh:mm", Locale.getDefault())
    return sdf.format(timestamp)
}

@Preview(showSystemUi = true)
@Composable
fun PrevHomeScreen() {
    TraVlogTheme {
        Home(homeViewModel = null, onTravlogClick = {}, navToDetailPage = {}){

        }
    }
}
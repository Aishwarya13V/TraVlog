package com.example.travlog.home

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
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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

    //var openDialog by remember { mutableStateOf(false) }

    //var selectedTravlog by remember { mutableStateOf<Travlogs?>(null) }

    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navToDetailPage.invoke() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        topBar = {
            TopAppBar(
                navigationIcon = {},
                actions = {
                    IconButton(onClick = {
                        homeViewModel?.signOut()
                        navToLoginPage.invoke()
                    }) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
                        
                    }
                },
                title = {
                    Text(text = "Home")
                }
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
                    /*LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                    ){
                        items(
                            (homeUiState.travlogList.data ?: emptyList()) as Int
                        ){travlog ->
                            TravlogItem(
                                travlogs = travlog,
                                onLongClick = {
                                    //openDialog = true
                                    //selectedTravlog = travlog
                                }
                            ) {
                                onTravlogClick.invoke(travlog.)
                            }
                        }
                    }*/
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
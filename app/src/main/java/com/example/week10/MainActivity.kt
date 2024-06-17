package com.example.week10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.week10.ui.theme.Week10Theme
import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.httpGet
//import com.github.kittinunf.fuel.json.responseJson // for JSON - uncomment when needed
import com.github.kittinunf.fuel.gson.responseObject // for GSON - uncomment when needed
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

data class Song(val id: Int, val title: String, val artist : String, val year: Int,
                val downloads: Int, val price: Double, val quantity: Int)

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Week10Theme {
                var navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val coroutineScope = rememberCoroutineScope()

                Scaffold(
                    topBar = {
                        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary
                        ), actions = {
                            IconButton(onClick =  {
                                coroutineScope.launch{
                                    if(drawerState.isClosed){
                                        drawerState.open()
                                    }else{
                                        drawerState.close()
                                    }
                                }
                            }){
                                Icon(imageVector = Icons.Filled.Add, "Add Song Button")
                            }
                        }, title = {Text("Hitastic Music App")})
                    }

                ){
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
                            ModalDrawerSheet(modifier = Modifier.height(200.dp)){

                                NavigationDrawerItem(
                                    selected = false,
                                    label = {Text("Add Song")},
                                    onClick = {
                                        coroutineScope.launch{drawerState.close()}
                                        navController.navigate("AddSongPage")
                                    }
                                )

                                NavigationDrawerItem(
                                    selected = false,
                                    label =  {Text("View Songs")},
                                    onClick = {
                                        coroutineScope.launch{drawerState.close()}
                                        navController.navigate("MainDisplay")
                                    }
                                )
                            }
                        })
                        {
                            NavHost(navController = navController, startDestination = "MainDisplay"){
                                composable("MainDisplay"){
                                    MainDisplay(AddSongPageCallBack = {navController.navigate("AddSongPage")})
                                }
                                composable("AddSongPage"){
                                    AddSongPage(MainDisplayCallBack = {navController.navigate("MainDisplay")})
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun MainDisplay(AddSongPageCallBack:() -> Unit){
    var searchArtist by remember { mutableStateOf("") }
    var responseText by remember { mutableStateOf("") }
    var songs by remember { mutableStateOf(listOf<Song>()) }

    Column {

        Row {
            TextField(value = searchArtist, onValueChange = {searchArtist = it}, modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row{
            Column {
                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    var url = "http://10.0.2.2:3000/artist/${searchArtist}"
                    url.httpGet().responseObject<List<Song>> { request, response, result ->
                        when(result){
                            is Result.Success -> {
                                songs = result.get()
                            }
                            is Result.Failure -> {
                                responseText = "ERROR ${result.error.message}"
                                songs = emptyList()
                            }
                        }
                    }
                }) {
                    Text("Get data from Web using Fuel GSON")
                }
            }
        }

        Row {
            Button(modifier = Modifier.fillMaxWidth(), onClick = { AddSongPageCallBack() }) {
                Text("Add a song to the database")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row{
            if(songs.isEmpty()){
                Text(responseText)
            }else{
                LazyColumn {
                    items(songs) {currentItem -> Text(
                        "ID: ${currentItem.id}\nSong Title: ${currentItem.title}\n" +
                                "Artist: ${currentItem.artist}\nYear: ${currentItem.year}\n" +
                                "Downloads: ${currentItem.downloads}\nPrice: ${currentItem.price}" +
                                "\nQuantity: ${currentItem.quantity}\n" +
                                "__________________________________________________"
                    )}
                }
            }
        }
    }
}
@Composable
fun AddSongPage(MainDisplayCallBack:() -> Unit){
    Text("Add Song Page")
}


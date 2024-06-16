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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.week10.ui.theme.Week10Theme
import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson // for JSON - uncomment when needed
// import com.github.kittinunf.fuel.gson.responseObject // for GSON - uncomment when needed
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Week10Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    hitastic()
                }
            }
        }
    }

    @Composable
    fun hitastic(){
        var searchArtist by remember { mutableStateOf("") }
        var responseText by remember { mutableStateOf("") }
        Column {

            Row {
                TextField(value = searchArtist, onValueChange = {searchArtist = it}, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row{
                Button(modifier = Modifier.fillMaxWidth(),onClick = {
                    var response =  ""
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO){
                            response = URL("http://10.0.2.2:3000/artist/${searchArtist}").readText()
                        }
                        responseText = response
                    }
                }) {
                    Text("Click to search for songs with inputted artist")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row{
                Text(responseText)
            }
            
        }
    }
}


package xyz.atrius

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "PopChat",
        state = rememberWindowState(width = 300.dp, height = 300.dp)
    ) {
        MaterialTheme {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val client = remember {
                    HttpClient.newHttpClient()
                }

                val request = remember {
                    HttpRequest
                        .newBuilder()
                        .uri(URI.create("http://localhost:8080/"))
                        .build()
                }

                fun request() = client.send(request, BodyHandlers.ofString()).body()

                val (text, setText) = remember { mutableStateOf(request()) }

                Button(
                    onClick = { setText(request()) }
                ) {
                    Text(text)
                }
            }
        }
    }
}
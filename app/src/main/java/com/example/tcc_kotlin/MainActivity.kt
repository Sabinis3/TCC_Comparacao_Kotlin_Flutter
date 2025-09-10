package com.example.tcc_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tcc_kotlin.screens.BiometriaScreen
import com.example.tcc_kotlin.ui.theme.TCC_KotlinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            TCC_KotlinTheme {
                NavHost(navController, startDestination = "main") {
                    composable("main") {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Column (
                                modifier = Modifier
                                    .fillMaxSize()
                                    .statusBarsPadding()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton (
                                        onClick = { navController.navigate("biometria") },
                                        modifier = Modifier
                                            .width(150.dp),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "Autenticação",
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    };
                                    OutlinedButton (
                                        onClick = { /*TODO*/ },
                                        modifier = Modifier
                                            .width(150.dp),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "Camera",
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }
                    }
                    composable("biometria") { BiometriaScreen() }
                }

            }
        }
    }
}
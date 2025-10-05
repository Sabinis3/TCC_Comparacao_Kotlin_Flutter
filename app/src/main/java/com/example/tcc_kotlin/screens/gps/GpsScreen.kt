package com.example.tcc_kotlin.screens.gps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted

        if (isGranted) {
            scope.launch {
                getCurrentLocation(context) { location, error ->
                    if (location != null) {
                        userLocation = location
                        errorMessage = null
                    } else {
                        errorMessage = error ?: "Não foi possível obter a localização"
                        userLocation = LatLng(-23.5505, -46.6333)
                    }
                    isLoading = false
                }
            }
        } else {
            isLoading = false
            errorMessage = "Permissão de localização negada"
        }
    }

    LaunchedEffect(Unit) {
        Log.d("MapScreen", "Iniciando verificação de permissão")

        if (hasLocationPermission) {
            launch {
                delay(10000)
                if (isLoading) {
                    userLocation = LatLng(-23.5505, -46.6333) // São Paulo como fallback
                    errorMessage = "Timeout ao obter localização. Usando localização padrão."
                    isLoading = false
                }
            }

            getCurrentLocation(context) { location, error ->
                if (location != null) {
                    userLocation = location
                    errorMessage = null
                } else {
                    errorMessage = error ?: "Não foi possível obter a localização"
                    userLocation = LatLng(-23.5505, -46.6333)
                }
                isLoading = false
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Obtendo localização...")
                    }
                }

                !hasLocationPermission -> {
                    // Permissão negada
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Permissão de localização necessária",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Para mostrar sua localização no mapa, precisamos de acesso à sua localização.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            isLoading = true
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }) {
                            Text("Conceder Permissão")
                        }
                    }
                }

                userLocation != null -> {
                    // Mostrar mapa
                    Column {
                        if (errorMessage != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = errorMessage!!,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        GoogleMapView(userLocation = userLocation!!)
                    }
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun GoogleMapView(userLocation: LatLng) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 15f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            myLocationButtonEnabled = true,
            compassEnabled = true
        )
    ) {
        Marker(
            state = MarkerState(position = userLocation),
            title = "Você está aqui",
            snippet = "Sua localização atual"
        )
    }
}

private fun getCurrentLocation(
    context: android.content.Context,
    onResult: (LatLng?, String?) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    try {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onResult(LatLng(location.latitude, location.longitude), null)
                } else {
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                    ).addOnSuccessListener { currentLocation ->
                        if (currentLocation != null) {
                            onResult(LatLng(currentLocation.latitude, currentLocation.longitude), null)
                        } else {
                            onResult(null, "Não foi possível obter sua localização. Verifique se o GPS está ativado.")
                        }
                    }.addOnFailureListener { e ->
                        onResult(null, "Erro ao obter localização: ${e.message}")
                    }
                }
            }
            .addOnFailureListener { e ->
                onResult(null, "Erro ao obter localização: ${e.message}")
            }
    } catch (e: SecurityException) {
        onResult(null, "Erro de permissão de localização")
    }
}
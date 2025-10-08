package com.example.tcc_kotlin.screens.biometria

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.tcc_kotlin.components.GridActionButton
import java.util.concurrent.Executor

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiometriaScreen() {

    val context: Context = LocalContext.current
    val biometricManager = BiometricManager.from(context)
    val activity = LocalContext.current as FragmentActivity

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Fingerprint,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Este dispositivo suporta autenticação biométrica.",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                GridActionButton(
                    onClick = {
                        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                            BiometricManager.BIOMETRIC_SUCCESS -> {
                                authenticateUser(activity)
                            }

                            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
                            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                                Toast.makeText(
                                    context,
                                    "Biometria não está disponível",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    text = "Autenticar com Biometria",
                    icon = Icons.Filled.Fingerprint,
                    aspectRatio = 8f
                )
            }
        }
    }

private fun authenticateUser(activity: FragmentActivity) {
    val executor: Executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(
        activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(activity, "Autenticação bem sucedida!", Toast.LENGTH_LONG).show()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(activity, "Erro de autenticação!", Toast.LENGTH_LONG).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(activity, "Falha na autenticação!", Toast.LENGTH_LONG).show()
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Autenticação Biométrica")
        .setSubtitle("Use sua biometria para autenticar")
        .setNegativeButtonText("Cancelar")
        .build()

    biometricPrompt.authenticate(promptInfo)
}
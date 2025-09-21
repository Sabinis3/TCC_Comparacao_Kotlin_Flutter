package com.example.tcc_kotlin.screens.biometria

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import java.util.concurrent.Executor

@Composable
fun BiometriaScreen(navController: NavController) {

    val biometricManager = BiometricManager.from(navController.context);
    val activity = LocalContext.current as FragmentActivity;

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
                        OutlinedButton (
                            onClick = {
                                when(biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)){
                                    BiometricManager.BIOMETRIC_SUCCESS -> {
                                        authenticateUser(activity);
                                    }
                                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
                                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                                        Toast.makeText(navController.context, "Biometria não está disponível", Toast.LENGTH_LONG).show();
                                    }
                                }
                            },
                            modifier = Modifier
                                .width(200.dp),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Validar Biometria",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        };
                        OutlinedButton (
                            onClick = { navController.navigate("main") },
                            modifier = Modifier
                                .width(200.dp),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Voltar",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center)
                        }

                }
            }
}

private fun authenticateUser(activity: FragmentActivity) {
    val executor: Executor = ContextCompat.getMainExecutor(activity);
    val biometricPrompt = BiometricPrompt(
        activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(activity, "Autenticação bem sucedida!", Toast.LENGTH_LONG).show();
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(activity, "Erro de autenticação: $errString", Toast.LENGTH_LONG).show();
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(activity, "Falha na autenticação", Toast.LENGTH_LONG).show();
            }
        });

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Autenticação Biométrica")
        .setSubtitle("Use sua biometria para autenticar")
        .setNegativeButtonText("Cancelar")
        .build();

    biometricPrompt.authenticate(promptInfo)
}
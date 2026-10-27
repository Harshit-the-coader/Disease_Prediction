package com.example.disesasepredictionai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.disesasepredictionai.data.PredictionResult
import com.example.disesasepredictionai.domain.DiseasePredictor
import com.example.disesasepredictionai.ui.screens.ResultScreen
import com.example.disesasepredictionai.ui.screens.SymptomSelectionScreen
import com.example.disesasepredictionai.ui.theme.DisesasePredictionAITheme

class MainActivity : ComponentActivity() {
    private lateinit var predictor: DiseasePredictor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        predictor = DiseasePredictor(this)
        
        enableEdgeToEdge()
        setContent {
            DisesasePredictionAITheme {
                var isInitialized by remember { mutableStateOf(value = false) }
                var initializationError by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    try {
                        predictor.initialize()
                        isInitialized = true
                    } catch (e: Exception) {
                        initializationError = e.message ?: "Unknown initialization error"
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    when {
                        initializationError != null -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Error: $initializationError", color = MaterialTheme.colorScheme.error)
                            }
                        }
                        !isInitialized -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        else -> {
                            MainNavigation(predictor)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::predictor.isInitialized) {
            predictor.close()
        }
    }
}

@Composable
fun MainNavigation(predictor: DiseasePredictor) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Selection) }
    var lastResult by remember { mutableStateOf<PredictionResult?>(null) }
    
    val allSymptoms = remember { predictor.getAllSymptoms() }

    when (currentScreen) {
        is Screen.Selection -> {
            SymptomSelectionScreen(allSymptoms = allSymptoms) { selectedKeys ->
                val result = predictor.predict(selectedKeys)
                if (result != null) {
                    lastResult = result
                    currentScreen = Screen.Result
                }
            }
        }
        is Screen.Result -> {
            lastResult?.let { result ->
                ResultScreen(
                    result = result,
                    diseaseDetails = predictor.getDiseaseDetails(result.diseaseName),
                    onBack = { currentScreen = Screen.Selection }
                )
            }
        }
    }
}

sealed class Screen {
    object Selection : Screen()
    object Result : Screen()
}

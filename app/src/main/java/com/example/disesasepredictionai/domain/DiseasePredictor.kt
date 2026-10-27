package com.example.disesasepredictionai.domain

import android.content.Context
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import com.example.disesasepredictionai.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.FloatBuffer

class DiseasePredictor(private val context: Context) {
    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private var session: OrtSession? = null
    private val assetManager = AssetManager(context)
    
    private var symptomColumns: List<String> = emptyList()
    private var classes: List<String> = emptyList()
    private var diseaseInfo: Map<String, DiseaseInfo> = emptyMap()
    private var symptomSeverity: Map<String, Int> = emptyMap()

    suspend fun initialize() = withContext(Dispatchers.IO) {
        if (session != null) return@withContext
        
        val modelBytes = context.assets.open("model.onnx").readBytes()
        session = env.createSession(modelBytes)
        
        symptomColumns = assetManager.loadSymptomColumns()
        classes = assetManager.loadClasses()
        diseaseInfo = assetManager.loadDiseaseInfo()
        symptomSeverity = assetManager.loadSymptomSeverity()
    }

    fun getAllSymptoms(): List<Symptom> {
        return symptomColumns.map { 
            Symptom(key = it, displayName = it.toHumanReadable()) 
        }
    }

    fun predict(selectedSymptomKeys: Set<String>): PredictionResult? {
        val currentSession = session ?: return null
        
        // Prepare input tensor: [1, 132]
        val inputArray = FloatArray(symptomColumns.size)
        symptomColumns.forEachIndexed { index, column ->
            inputArray[index] = if (selectedSymptomKeys.contains(column)) 1.0f else 0.0f
        }
        
        val inputTensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(inputArray), longArrayOf(1, symptomColumns.size.toLong()))
        
        val outputMap = currentSession.run(mapOf("symptom_input" to inputTensor))
        
        val predictedLabel = (outputMap["label"].get() as OnnxTensor).value as Array<String>
        val label = predictedLabel[0]
        
        val probabilitiesTensor = (outputMap["probabilities"].get() as OnnxTensor).value as Array<FloatArray>
        val probabilities = probabilitiesTensor[0]
        
        // Calculate urgency
        val totalSeverity = selectedSymptomKeys.sumOf { symptomSeverity[it] ?: 1 }
        val avgSeverity = if (selectedSymptomKeys.isNotEmpty()) totalSeverity.toFloat() / selectedSymptomKeys.size else 0f
        
        val urgency = when {
            avgSeverity >= 5 -> Urgency.HIGH
            avgSeverity >= 3 -> Urgency.MEDIUM
            else -> Urgency.LOW
        }
        
        // Get confidence for predicted label
        val predictedIndex = classes.indexOf(label)
        val confidence = if (predictedIndex != -1) probabilities[predictedIndex] * 100 else 0f
        
        // Get top 3 candidates
        val candidates = classes.mapIndexed { index, name ->
            Candidate(name, probabilities[index] * 100)
        }.sortedByDescending { it.probability }.take(3)
        
        return PredictionResult(
            diseaseName = label,
            confidence = confidence,
            urgency = urgency,
            otherCandidates = candidates,
        )
    }
    
    fun getDiseaseDetails(name: String): DiseaseInfo? {
        return diseaseInfo[name]
    }

    fun close() {
        session?.close()
        env.close()
    }
}

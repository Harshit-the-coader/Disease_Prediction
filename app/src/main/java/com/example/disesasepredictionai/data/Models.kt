package com.example.disesasepredictionai.data

import kotlinx.serialization.Serializable

@Serializable
data class DiseaseInfo(
    val description: String,
    val precautions: List<String>,
    val medications: List<String>,
    val diet: List<String>,
    val workout: List<String>
)

@Serializable
data class SymptomSeverity(
    val severityMap: Map<String, Int>
)

data class Symptom(
    val key: String,
    val displayName: String,
    val isSelected: Boolean = false
)

data class PredictionResult(
    val diseaseName: String,
    val confidence: Float,
    val urgency: Urgency,
    val otherCandidates: List<Candidate> = emptyList()
)

data class Candidate(
    val name: String,
    val probability: Float
)

enum class Urgency {
    LOW, MEDIUM, HIGH
}

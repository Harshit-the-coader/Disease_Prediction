package com.example.disesasepredictionai.data

import android.content.Context
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@OptIn(ExperimentalSerializationApi::class)
class AssetManager(private val context: Context) {
    private val json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }

    fun loadSymptomColumns(): List<String> {
        return context.assets.open("symptom_columns.json").use { 
            json.decodeFromStream(it) 
        }
    }

    fun loadClasses(): List<String> {
        return context.assets.open("classes.json").use { 
            json.decodeFromStream(it) 
        }
    }

    fun loadDiseaseInfo(): Map<String, DiseaseInfo> {
        return context.assets.open("disease_info.json").use { 
            json.decodeFromStream(it) 
        }
    }

    fun loadSymptomSeverity(): Map<String, Int> {
        return context.assets.open("symptom_severity.json").use { 
            json.decodeFromStream(it) 
        }
    }
}

fun String.toHumanReadable(): String {
    return this.split("_")
        .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
}

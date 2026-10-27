package com.example.disesasepredictionai.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.disesasepredictionai.data.DiseaseInfo
import com.example.disesasepredictionai.data.PredictionResult
import com.example.disesasepredictionai.data.Urgency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    result: PredictionResult,
    diseaseDetails: DiseaseInfo?,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prediction Results") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Disclaimer
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "This is an educational hackathon prototype, not a medical diagnosis. It does not replace professional care — seek a doctor or emergency services as soon as you're able to.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Prediction
            Text(
                text = "Likely Condition:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = result.diseaseName,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${result.confidence.toInt()}% Confidence",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Urgency Indicator
            UrgencyIndicator(result.urgency)

            Spacer(modifier = Modifier.height(24.dp))

            // Medical Details
            if (diseaseDetails != null) {
                InfoSection("Description", diseaseDetails.description)
                InfoSectionList("Precautions", diseaseDetails.precautions)
                InfoSectionList("Medications", diseaseDetails.medications)
                InfoSectionList("Diet", diseaseDetails.diet)
                InfoSectionList("Workout", diseaseDetails.workout)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Other Candidates
            Text(
                text = "Other Possibilities",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            result.otherCandidates.forEach { candidate ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(candidate.name, style = MaterialTheme.typography.bodyLarge)
                    Text("${candidate.probability.toInt()}%", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }
                HorizontalDivider()
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("CHECK AGAIN", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun UrgencyIndicator(urgency: Urgency) {
    val (color, text) = when (urgency) {
        Urgency.LOW -> Color(0xFF4CAF50) to "LOW"
        Urgency.MEDIUM -> Color(0xFFFFC107) to "MEDIUM"
        Urgency.HIGH -> Color(0xFFF44336) to "HIGH"
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Urgency Level", style = MaterialTheme.typography.labelMedium)
        Surface(
            color = color,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
fun InfoSection(title: String, content: String) {
    if (content.isBlank()) return
    var expanded by remember { mutableStateOf(value = false) }
    
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
        }
        AnimatedVisibility(visible = expanded) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        HorizontalDivider()
    }
}

@Composable
fun InfoSectionList(title: String, items: List<String>) {
    if (items.isEmpty()) return
    var expanded by remember { mutableStateOf(value = false) }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
        }
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                items.forEach { item ->
                    Text("• $item", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        }
        HorizontalDivider()
    }
}

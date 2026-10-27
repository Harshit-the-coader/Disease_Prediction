package com.example.disesasepredictionai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.disesasepredictionai.data.Symptom

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SymptomSelectionScreen(
    allSymptoms: List<Symptom>,
    onCheckSymptoms: (Set<String>) -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    val selectedSymptoms = remember { mutableStateListOf<String>() }
    
    val filteredSymptoms = remember(searchQuery, allSymptoms) {
        allSymptoms.filter { 
            it.displayName.contains(searchQuery, ignoreCase = true) || 
            it.key.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Offline Symptom Checker", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (selectedSymptoms.size < 3) {
                        Text(
                            text = "Add a few more symptoms for a more reliable result.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Button(
                        onClick = { onCheckSymptoms(selectedSymptoms.toSet()) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = selectedSymptoms.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text("CHECK SYMPTOMS (${selectedSymptoms.size})", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search symptoms...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            // Selected Symptoms Chips
            if (selectedSymptoms.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    selectedSymptoms.forEach { key ->
                        val symptom = allSymptoms.find { it.key == key }
                        if (symptom != null) {
                            InputChip(
                                selected = true,
                                onClick = { selectedSymptoms.remove(key) },
                                label = { Text(symptom.displayName) },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Symptom List
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(filteredSymptoms) { symptom ->
                    val isSelected = selectedSymptoms.contains(symptom.key)
                    ListItem(
                        headlineContent = { Text(symptom.displayName) },
                        trailingContent = {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    if (checked) selectedSymptoms.add(symptom.key)
                                    else selectedSymptoms.remove(symptom.key)
                                }
                            )
                        },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

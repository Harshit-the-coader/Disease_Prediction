# Implementation Plan - Offline Symptom Checker

Build an offline Android app that predicts diseases from symptoms using an ONNX model. All processing is on-device, requiring no internet connection.

## User Review Required

> [!IMPORTANT]
> The app will use the ONNX Runtime for Android. Ensure that the `model.onnx` and JSON metadata files are correctly placed in `app/src/main/assets`.

## Proposed Changes

### 1. Dependencies & Configuration
#### [MODIFY] [build.gradle.kts](file:///C:/Users/hs261/AndroidStudioProjects/DisesasePredictionAI/app/build.gradle.kts)
- Add ONNX Runtime dependency: `implementation("com.microsoft.onnxruntime:onnxruntime-android:1.29.0")`.
- Add Serialization dependency for JSON parsing: `implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")`.

### 2. Data Models
#### [NEW] [Models.kt](file:///C:/Users/hs261/AndroidStudioProjects/DisesasePredictionAI/app/src/main/java/com/example/disesasepredictionai/data/Models.kt)
- Define `DiseaseInfo`, `SymptomSeverity`, `PredictionResult`, and other data classes.

### 3. Data Layer (Asset Loading & Inference)
#### [NEW] [AssetManager.kt](file:///C:/Users/hs261/AndroidStudioProjects/DisesasePredictionAI/app/src/main/java/com/example/disesasepredictionai/data/AssetManager.kt)
- Helper to load and parse JSON assets (`symptom_columns.json`, `classes.json`, `disease_info.json`, `symptom_severity.json`).

#### [NEW] [DiseasePredictor.kt](file:///C:/Users/hs261/AndroidStudioProjects/DisesasePredictionAI/app/src/main/java/com/example/disesasepredictionai/domain/DiseasePredictor.kt)
- Manage ONNX session lifecycle.
- Implement inference logic: transform selected symptoms into input tensor, execute model, and parse outputs.

### 4. UI Layer (Compose)
#### [NEW] [SymptomSelectionScreen.kt](file:///C:/Users/hs261/AndroidStudioProjects/DisesasePredictionAI/app/src/main/java/com/example/disesasepredictionai/ui/screens/SymptomSelectionScreen.kt)
- Searchable checklist of 132 symptoms.
- Chip-based review of selected symptoms.
- Validation (min 1 symptom).

#### [NEW] [ResultScreen.kt](file:///C:/Users/hs261/AndroidStudioProjects/DisesasePredictionAI/app/src/main/java/com/example/disesasepredictionai/ui/screens/ResultScreen.kt)
- Display top prediction with confidence.
- Top 3 candidates list.
- Urgency indicator based on severity weights.
- Expandable medical info (Precautions, Diet, etc.).
- Legal disclaimer.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/hs261/AndroidStudioProjects/DisesasePredictionAI/app/src/main/java/com/example/disesasepredictionai/MainActivity.kt)
- Set up Navigation and initialize the `DiseasePredictor`.

## Verification Plan

### Automated Tests
- Create unit tests for `DiseasePredictor` to verify tensor input generation and output parsing logic.

### Manual Verification
- **Airplane Mode Test**: Verify the app works completely without network access.
- **Symptom Accuracy**: Input known sets of symptoms (e.g., for "Fungal infection") and verify the model's output against expected results.
- **UI Stress Test**: Rapid searching and selection of multiple symptoms.

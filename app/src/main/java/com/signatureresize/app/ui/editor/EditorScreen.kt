package com.signatureresize.app.ui.editor

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.signatureresize.app.utils.ImageResizerUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var resultBytes by remember { mutableStateOf<ByteArray?>(null) }
    
    // Inputs (Defaults)
    var width by remember { mutableStateOf("200") }
    var height by remember { mutableStateOf("200") }
    var minKb by remember { mutableStateOf("10") } // Not used logic yet, usually target is max
    var maxKb by remember { mutableStateOf("20") }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedUri = uri
        resultBytes = null // Reset previous result
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editor") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Image Preview / Picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color.LightGray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (resultBytes != null) {
                    val bitmap = BitmapFactory.decodeByteArray(resultBytes, 0, resultBytes!!.size)
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Result",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (selectedUri != null) {
                    // Show original (simplified, ideally load bitmap)
                    Text("Image Selected: \n$selectedUri", color = Color.Black)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PhotoLibrary, "Gallery", tint = Color.DarkGray, modifier = Modifier.size(48.dp))
                        Text("Tap 'Select Image' to start", color = Color.DarkGray)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Image")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 2. Settings (Inputs)
            Text("Settings", style = MaterialTheme.typography.titleMedium)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = width, 
                    onValueChange = { width = it }, 
                    label = { Text("Width (px)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = height, 
                    onValueChange = { height = it }, 
                    label = { Text("Height (px)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = maxKb, 
                    onValueChange = { maxKb = it }, 
                    label = { Text("Max Size (KB)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Presets Row
            Text("Quick Presets", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuggestionChip(
                    onClick = { width = "132"; height = "170"; maxKb = "20" },
                    label = { Text("UPSC Sign") }
                )
                SuggestionChip(
                    onClick = { width = "350"; height = "450"; maxKb = "50" },
                    label = { Text("UPSC Photo") }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 3. Process Button
            Button(
                onClick = {
                    if (selectedUri != null) {
                        scope.launch {
                            val w = width.toIntOrNull()
                            val h = height.toIntOrNull()
                            val kb = maxKb.toIntOrNull()
                            
                            val result = ImageResizerUtils.resizeImage(context, selectedUri!!, w, h, kb)
                            if (result != null) {
                                resultBytes = result
                            }
                        }
                    }
                },
                enabled = selectedUri != null,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Resize Image")
            }
            
            // 4. Save/Share (Only if result exists)
            if (resultBytes != null) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { /* TODO: Save to Gallery */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Save, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save to Gallery")
                }
                
                Text(
                    text = "Final Size: ${resultBytes!!.size / 1024} KB", 
                    style = MaterialTheme.typography.bodyMedium, 
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

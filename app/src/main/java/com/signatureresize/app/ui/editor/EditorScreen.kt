package com.signatureresize.app.ui.editor

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.signatureresize.app.models.ExamPresets
import com.signatureresize.app.models.Preset
import com.signatureresize.app.utils.ImageResizerUtils
import com.signatureresize.app.utils.RequestStoragePermissions
import kotlinx.coroutines.launch
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EditorScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var resultBytes by remember { mutableStateOf<ByteArray?>(null) }
    var showCustomOptions by remember { mutableStateOf(false) }
    var selectedPreset by remember { mutableStateOf<Preset?>(null) }
    
    // Inputs
    var width by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var maxKb by remember { mutableStateOf("20") }
    
    // Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            resultBytes = null
        }
    }

    // Permission Handler
    // In a real app, you might want to show this only when needed
    // For now we request on screen load to ensure smooth UX
    RequestStoragePermissions(onPermissionGranted = {})

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Studio", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
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
            
            // 1. Upload Area (Hero Section)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .clickable { galleryLauncher.launch("image/*") }
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp)),
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
                   // Badge for processed
                   Box(
                       modifier = Modifier
                           .align(Alignment.TopEnd)
                           .padding(12.dp)
                           .background(Color(0xFF10B981), RoundedCornerShape(8.dp))
                           .padding(horizontal = 8.dp, vertical = 4.dp)
                   ) {
                       Text("Done: ${resultBytes!!.size / 1024} KB", color = Color.White, style = MaterialTheme.typography.labelSmall)
                   }
                } else if (selectedUri != null) {
                    // Show Placeholder for selected
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Image Loaded", fontWeight = FontWeight.Bold)
                        Text("Tap to change", style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Tap to Upload", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Signature or Photo", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Presets (Horizontal Scroll)
            Text("Select Format", style = MaterialTheme.typography.labelLarge, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ExamPresets.presets) { preset ->
                    FilterChip(
                        selected = selectedPreset?.id == preset.id,
                        onClick = { 
                            selectedPreset = preset
                            width = preset.widthPx?.toString() ?: ""
                            height = preset.heightPx?.toString() ?: ""
                            maxKb = preset.maxKb.toString()
                        },
                        label = { Text(preset.name) },
                        leadingIcon = if (selectedPreset?.id == preset.id) {
                            { Icon(Icons.Default.Check, null) }
                        } else null
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // 3. Custom Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCustomOptions = !showCustomOptions },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Tune, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Manual Settings", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            }
            
            AnimatedVisibility(visible = showCustomOptions) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = width,
                            onValueChange = { width = it },
                            label = { Text("Width px") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = height,
                            onValueChange = { height = it },
                            label = { Text("Height px") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = maxKb,
                        onValueChange = { maxKb = it },
                        label = { Text("Max Size (KB)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            // 4. Action Button
            Button(
                onClick = {
                    if (selectedUri == null) return@Button
                    scope.launch {
                        val w = width.toIntOrNull()
                        val h = height.toIntOrNull()
                        val kb = maxKb.toIntOrNull()
                        
                        val result = ImageResizerUtils.resizeImage(context, selectedUri!!, w, h, kb)
                        if (result != null) {
                            resultBytes = result
                            // In non-demo, trigger Ad here (interstitial)
                            Toast.makeText(context, "Resized Successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to process", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = selectedUri != null,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (resultBytes == null) "Process Image" else "Process Again", fontSize = 18.sp)
            }
            
            if (resultBytes != null) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                         // Actual Save Logic would go here
                         Toast.makeText(context, "Saved to Gallery (Simulated)", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Save, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save to Gallery")
                }
            }
        }
    }
}

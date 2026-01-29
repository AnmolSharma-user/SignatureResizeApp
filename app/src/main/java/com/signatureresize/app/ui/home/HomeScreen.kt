package com.signatureresize.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.signatureresize.app.ui.components.ToolCard
import com.signatureresize.app.ui.components.AdMobBanner

data class ToolItem(
    val title: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToEditor: () -> Unit
) {
    val tools = listOf(
        ToolItem("Signature Resize", Icons.Default.Create, Color(0xFF3B82F6)), // Blue
        ToolItem("Photo Resize", Icons.Default.Image, Color(0xFF10B981)),   // Green
        ToolItem("UPSC Preset", Icons.Default.School, Color(0xFFF59E0B)),   // Orange
        ToolItem("GATE Preset", Icons.Default.School, Color(0xFFEF4444)),   // Red
        ToolItem("SSC Preset", Icons.Default.School, Color(0xFF8B5CF6)),    // Purple
        ToolItem("Document", Icons.Default.Description, Color(0xFF64748B))  // Slate
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Signature Resize") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
             // AdMob Banner
             AdMobBanner()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(tools) { tool ->
                    ToolCard(
                        title = tool.title,
                        icon = tool.icon,
                        color = tool.color,
                        onClick = onNavigateToEditor
                    )
                }
            }
        }
    }
}

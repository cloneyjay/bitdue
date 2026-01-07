package com.bitdue.financeapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bitdue.financeapp.data.local.entity.CategoryEntity
import com.bitdue.financeapp.data.models.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCategoryDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, icon: String, color: Long, type: TransactionType) -> Unit,
    existingCategory: CategoryEntity? = null,
    modifier: Modifier = Modifier
) {
    var categoryName by remember { mutableStateOf(existingCategory?.name ?: "") }
    var selectedIcon by remember { mutableStateOf(existingCategory?.icon ?: "📁") }
    var selectedColor by remember { mutableStateOf(existingCategory?.color ?: Color(0xFF6200EE).value.toLong()) }
    var selectedType by remember { mutableStateOf(existingCategory?.type ?: TransactionType.EXPENSE) }
    var showIconPicker by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    
    val isEditMode = existingCategory != null
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditMode) "Edit Category" else "Add Category",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Category Name
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Icon Selector
                Text(
                    text = "Icon",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(selectedColor))
                        .clickable { showIconPicker = !showIconPicker }
                        .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedIcon,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                }
                
                if (showIconPicker) {
                    Spacer(modifier = Modifier.height(12.dp))
                    IconPicker(
                        onIconSelected = { icon ->
                            selectedIcon = icon
                            showIconPicker = false
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Color Selector
                Text(
                    text = "Color",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = { showColorPicker = !showColorPicker },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(selectedColor)
                    ),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Select Color", color = Color.White)
                }
                
                if (showColorPicker) {
                    Spacer(modifier = Modifier.height(12.dp))
                    ColorPicker(
                        selectedColor = selectedColor,
                        onColorSelected = { color ->
                            selectedColor = color
                            showColorPicker = false
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Transaction Type
                Text(
                    text = "Type",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterChip(
                        selected = selectedType == TransactionType.EXPENSE,
                        onClick = { selectedType = TransactionType.EXPENSE },
                        label = { Text("Expense") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedType == TransactionType.INCOME,
                        onClick = { selectedType = TransactionType.INCOME },
                        label = { Text("Income") },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (categoryName.isNotBlank()) {
                                onSave(categoryName, selectedIcon, selectedColor, selectedType)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = categoryName.isNotBlank()
                    ) {
                        Text(if (isEditMode) "Update" else "Add")
                    }
                }
            }
        }
    }
}

@Composable
private fun IconPicker(
    onIconSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val availableIcons = listOf(
        "🍔", "🚗", "🛍️", "🎬", "💡", "⚕️", "📚", "💰", "💼", "📈",
        "🏠", "✈️", "🎮", "📱", "💻", "🎵", "🏋️", "🍕", "☕", "🎨",
        "📦", "🎁", "💳", "🏦", "📊", "🔧", "⚽", "🎯", "📷", "🌟"
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(availableIcons) { icon ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { onIconSelected(icon) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorPicker(
    selectedColor: Long,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val availableColors = listOf(
        Color(0xFF6200EE), // Purple
        Color(0xFF03DAC5), // Teal
        Color(0xFFFF6B6B), // Red
        Color(0xFF4ECDC4), // Cyan
        Color(0xFFFFE66D), // Yellow
        Color(0xFF95E1D3), // Mint
        Color(0xFFF38181), // Pink
        Color(0xFFAA96DA), // Lavender
        Color(0xFFFCBF49), // Orange
        Color(0xFF06D6A0), // Green
        Color(0xFF118AB2), // Blue
        Color(0xFFEF476F), // Crimson
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(availableColors) { color ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color)
                        .clickable { onColorSelected(color.value.toLong()) }
                        .then(
                            if (color.value.toLong() == selectedColor) {
                                Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            } else {
                                Modifier.border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            }
                        )
                )
            }
        }
    }
}

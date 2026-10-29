package com.example.calcmaster.inventory

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onSendSpecToCalculator: (String) -> Unit
) {
    // Current inventory state initialized with realistic school physics lab apparatus
    var inventoryItems by remember {
        mutableStateOf(
            InventoryExtractor.extractFromText(InventoryExtractor.getSampleInventoryManifest())
        )
    }

    var rawInputText by remember { mutableStateOf("") }
    var isExtractionPanelOpen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showOnlyNeedsCalibration by remember { mutableStateOf(false) }

    // Summary calculations
    val totalUnits = inventoryItems.sumOf { it.quantity }
    val totalValuation = inventoryItems.sumOf { it.quantity * it.unitCost }
    val needsCalibrationCount = inventoryItems.count { it.needsCalibration }
    val lowStockCount = inventoryItems.count { it.quantity < 5 }

    val categories = listOf("All", "Mechanics", "Optics", "Electronics", "Thermodynamics", "Waves & Acoustics", "General Lab")

    val filteredItems = inventoryItems.filter { item ->
        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) || item.sku.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || item.category.equals(selectedCategory, ignoreCase = true)
        val matchesCalib = !showOnlyNeedsCalibration || item.needsCalibration
        matchesSearch && matchesCategory && matchesCalib
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Lab Inventory & Extraction",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Automated Data Extraction & Stock Management",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = { isExtractionPanelOpen = !isExtractionPanelOpen },
                        modifier = Modifier.testTag("toggle_extract_panel_btn")
                    ) {
                        Icon(
                            imageVector = if (isExtractionPanelOpen) Icons.Default.Close else Icons.Default.AutoFixHigh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isExtractionPanelOpen) "Close" else "Extract Data")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Automated Extraction Panel
            item {
                AnimatedVisibility(visible = isExtractionPanelOpen) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .testTag("extraction_panel_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Automated Data Extraction Engine",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Paste receipts, shipping manifests, CSV tables, or invoices. The engine extracts Item Name, SKU, Category, Quantities, Unit Costs, and Calibration tags.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = rawInputText,
                                onValueChange = { rawInputText = it },
                                placeholder = { Text("e.g. 10x Spherometer (SKU: PHY-SPH-02) $24.00 Shelf A3, Needs Calibration...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .testTag("raw_manifest_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (rawInputText.isNotBlank()) {
                                            val extracted = InventoryExtractor.extractFromText(rawInputText)
                                            if (extracted.isNotEmpty()) {
                                                inventoryItems = (extracted + inventoryItems).distinctBy { it.sku }
                                                rawInputText = ""
                                                isExtractionPanelOpen = false
                                            }
                                        }
                                    },
                                    enabled = rawInputText.isNotBlank(),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("run_extraction_btn")
                                ) {
                                    Icon(Icons.Default.Bolt, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Extract & Add")
                                }

                                OutlinedButton(
                                    onClick = {
                                        rawInputText = InventoryExtractor.getSampleInventoryManifest()
                                    },
                                    modifier = Modifier.testTag("load_sample_manifest_btn")
                                ) {
                                    Text("Load Sample")
                                }
                            }
                        }
                    }
                }
            }

            // Summary Statistics Dashboard
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Total Apparatus",
                        value = "$totalUnits units",
                        subtitle = "${inventoryItems.size} distinct SKUs",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Lab Valuation",
                        value = String.format("$%.0f", totalValuation),
                        subtitle = "Equip. Assets",
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Needs Calib.",
                        value = "$needsCalibrationCount items",
                        subtitle = if (needsCalibrationCount > 0) "Pending" else "All Good",
                        color = if (needsCalibrationCount > 0) Color(0xFFF59E0B) else Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Search Bar & Filter
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search apparatus, SKU, or rack...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("inventory_search_field"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Category Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(4).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }

            // Section Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Equipment Manifest (${filteredItems.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    FilterChip(
                        selected = showOnlyNeedsCalibration,
                        onClick = { showOnlyNeedsCalibration = !showOnlyNeedsCalibration },
                        label = { Text("Needs Calib Only", style = MaterialTheme.typography.labelSmall) },
                        leadingIcon = if (showOnlyNeedsCalibration) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null
                    )
                }
            }

            // Equipment Item Cards
            items(filteredItems, key = { it.id }) { item ->
                InventoryItemCard(
                    item = item,
                    onQuantityChange = { delta ->
                        inventoryItems = inventoryItems.map {
                            if (it.id == item.id) it.copy(quantity = (it.quantity + delta).coerceAtLeast(0)) else it
                        }
                    },
                    onToggleCalibration = {
                        inventoryItems = inventoryItems.map {
                            if (it.id == item.id) it.copy(needsCalibration = !it.needsCalibration) else it
                        }
                    },
                    onSendToCalc = {
                        onSendSpecToCalculator(item.name)
                    },
                    onDelete = {
                        inventoryItems = inventoryItems.filter { it.id != item.id }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun InventoryItemCard(
    item: InventoryItem,
    onQuantityChange: (Int) -> Unit,
    onToggleCalibration: () -> Unit,
    onSendToCalc: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("inventory_item_${item.sku}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = item.sku,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = item.category,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Delete button
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Delete item",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details row: location, unit cost, status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Location: ${item.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Unit Cost: $${String.format("%.2f", item.unitCost)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Calibration badge
                Surface(
                    onClick = onToggleCalibration,
                    shape = RoundedCornerShape(8.dp),
                    color = if (item.needsCalibration) Color(0xFFF59E0B).copy(alpha = 0.15f) else Color(0xFF10B981).copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (item.needsCalibration) Icons.Default.Warning else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (item.needsCalibration) Color(0xFFF59E0B) else Color(0xFF10B981),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (item.needsCalibration) "Calib. Due" else "Calibrated",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (item.needsCalibration) Color(0xFFF59E0B) else Color(0xFF10B981)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stock Count adjustment & Send to Calculator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalIconButton(
                        onClick = { onQuantityChange(-1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                    }
                    Text(
                        text = "${item.quantity} in stock",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                    FilledTonalIconButton(
                        onClick = { onQuantityChange(1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                    }
                }

                FilledTonalButton(
                    onClick = onSendToCalc,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Use in Solver", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

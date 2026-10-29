package com.example.calcmaster.inventory

data class InventoryItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val sku: String,
    val category: String, // Mechanics, Optics, Electronics, Thermal, Wave/Sound, General Lab
    val quantity: Int,
    val unitCost: Double,
    val location: String,
    val needsCalibration: Boolean = false,
    val lastAuditedDate: String = "2026-10-28",
    val extractionConfidence: Float = 0.95f
)

data class ExtractionBatch(
    val rawText: String,
    val extractedItems: List<InventoryItem>,
    val timestamp: Long = System.currentTimeMillis()
)

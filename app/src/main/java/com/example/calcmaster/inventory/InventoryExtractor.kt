package com.example.calcmaster.inventory

import java.util.UUID

object InventoryExtractor {

    private val SKU_REGEX = Regex("""\b(?:SKU|Code|Cat#?|ID|Item#?)\s*[:#-]?\s*([A-Za-z0-9_-]{3,15})\b""", RegexOption.IGNORE_CASE)
    private val QTY_REGEX = Regex("""\b(?:Qty|Quantity|Count|Units?|Pcs?)\s*[:=]?\s*(\d+)\b""", RegexOption.IGNORE_CASE)
    private val X_QTY_REGEX = Regex("""\b(\d+)\s*[xX]\s+""", RegexOption.IGNORE_CASE)
    private val COST_REGEX = Regex("""(?:\$|₹|USD|Rs\.?)\s*(\d+(?:\.\d{1,2})?)|(\d+(?:\.\d{1,2})?)\s*(?:USD|\$|₹|/unit|each)""", RegexOption.IGNORE_CASE)
    private val LOCATION_REGEX = Regex("""\b(?:Rack|Shelf|Bin|Room|Bench|Cabinet|Drawer|Location)\s*[:#-]?\s*([A-Za-z0-9\s-]{2,12})\b""", RegexOption.IGNORE_CASE)

    fun extractFromText(rawText: String): List<InventoryItem> {
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val results = mutableListOf<InventoryItem>()

        for (line in lines) {
            // Check if line looks like CSV/TSV
            if (line.contains(",") || line.contains("\t") || line.contains("|")) {
                val csvItem = parseDelimitedLine(line)
                if (csvItem != null) {
                    results.add(csvItem)
                    continue
                }
            }

            // Otherwise, freeform heuristic extraction
            val item = parseFreeformLine(line)
            if (item != null) {
                results.add(item)
            }
        }

        return results
    }

    private fun parseDelimitedLine(line: String): InventoryItem? {
        val delimiter = when {
            line.contains("|") -> "|"
            line.contains("\t") -> "\t"
            else -> ","
        }
        val parts = line.split(delimiter).map { it.trim().removeSurrounding("\"") }
        if (parts.size < 2) return null

        val headerCandidate = parts[0].lowercase()
        if (headerCandidate == "item" || headerCandidate == "name" || headerCandidate == "sku") return null

        // Check if parts have explicit labels (e.g. "SKU: MEC-01" or "Cost: $35")
        val hasLabels = parts.any {
            it.startsWith("sku", ignoreCase = true) ||
            it.startsWith("cost", ignoreCase = true) ||
            it.startsWith("qty", ignoreCase = true) ||
            it.startsWith("shelf", ignoreCase = true) ||
            it.startsWith("rack", ignoreCase = true) ||
            it.contains("calibrat", ignoreCase = true)
        }

        if (hasLabels) {
            val rawName = parts[0]
            var sku: String? = null
            var category: String? = null
            var qty: Int? = null
            var cost = 0.0
            var location = "Main Store"
            var needsCal = false

            for (part in parts) {
                SKU_REGEX.find(part)?.let { sku = it.groupValues[1] }
                QTY_REGEX.find(part)?.let { qty = it.groupValues[1].toIntOrNull() }
                COST_REGEX.find(part)?.let { match ->
                    val v1 = match.groupValues[1].toDoubleOrNull()
                    val v2 = match.groupValues[2].toDoubleOrNull()
                    cost = v1 ?: v2 ?: 0.0
                }
                LOCATION_REGEX.find(part)?.let { location = it.value }
                if (part.contains("calibrat", ignoreCase = true)) {
                    needsCal = !part.contains("calibrated", ignoreCase = true)
                }
            }

            // Extract quantity from name if not found in parts (e.g. "24x Vernier Calipers")
            if (qty == null) {
                X_QTY_REGEX.find(rawName)?.let { qty = it.groupValues[1].toIntOrNull() }
            }
            if (qty == null) qty = 1

            val cleanName = rawName
                .replace(X_QTY_REGEX, "")
                .replace(SKU_REGEX, "")
                .trim()

            val finalSku = sku ?: generateSku(cleanName)
            val finalCat = category ?: detectCategory(cleanName)

            return InventoryItem(
                name = cleanName,
                sku = finalSku,
                category = finalCat,
                quantity = qty ?: 1,
                unitCost = cost,
                location = location,
                needsCalibration = needsCal,
                extractionConfidence = 0.98f
            )
        }

        // Positional CSV: Name, SKU, Category, Qty, Cost, Location, CalibStatus
        val rawName = parts[0]
        val cleanName = rawName.replace(X_QTY_REGEX, "").trim()
        val sku = parts.getOrNull(1)?.takeIf { it.isNotEmpty() } ?: generateSku(cleanName)
        val category = parts.getOrNull(2)?.takeIf { it.isNotEmpty() } ?: detectCategory(cleanName)
        val qty = parts.getOrNull(3)?.toIntOrNull()
            ?: X_QTY_REGEX.find(rawName)?.groupValues?.get(1)?.toIntOrNull()
            ?: 1
        val cost = parts.getOrNull(4)?.replace("$", "")?.replace("₹", "")?.toDoubleOrNull() ?: 0.0
        val location = parts.getOrNull(5) ?: "Main Store"
        val needsCal = parts.getOrNull(6)?.let {
            it.contains("calib", ignoreCase = true) && !it.contains("calibrated", ignoreCase = true)
        } ?: false

        return InventoryItem(
            name = cleanName,
            sku = sku,
            category = category,
            quantity = qty,
            unitCost = cost,
            location = location,
            needsCalibration = needsCal,
            extractionConfidence = 0.98f
        )
    }

    private fun parseFreeformLine(line: String): InventoryItem? {
        if (line.length < 5) return null

        // Extract SKU
        val skuMatch = SKU_REGEX.find(line)
        val sku = skuMatch?.groupValues?.get(1)

        // Extract Quantity
        var qty = QTY_REGEX.find(line)?.groupValues?.get(1)?.toIntOrNull()
        if (qty == null) {
            val xMatch = X_QTY_REGEX.find(line)
            qty = xMatch?.groupValues?.get(1)?.toIntOrNull()
        }
        if (qty == null) qty = 1

        // Extract Unit Cost
        val costMatch = COST_REGEX.find(line)
        val cost = costMatch?.groupValues?.get(1)?.toDoubleOrNull()
            ?: costMatch?.groupValues?.get(2)?.toDoubleOrNull()
            ?: 0.0

        // Extract Location
        val locMatch = LOCATION_REGEX.find(line)
        val location = locMatch?.value ?: "Lab Cabinet A"

        // Calibration status
        val needsCal = line.contains("calibrat", ignoreCase = true) && !line.contains("calibrated", ignoreCase = true)

        // Clean Name
        var cleanName = line
            .replace(SKU_REGEX, "")
            .replace(QTY_REGEX, "")
            .replace(X_QTY_REGEX, "")
            .replace(COST_REGEX, "")
            .replace(LOCATION_REGEX, "")
            .replace(Regex("""[@|•\-:–]"""), " ")
            .replace(Regex("""\s+"""), " ")
            .trim()

        if (cleanName.isEmpty() || cleanName.length < 3) {
            cleanName = "Lab Apparatus " + UUID.randomUUID().toString().take(4)
        }

        val category = detectCategory(cleanName)
        val finalSku = sku ?: generateSku(cleanName)

        return InventoryItem(
            name = cleanName,
            sku = finalSku,
            category = category,
            quantity = qty,
            unitCost = cost,
            location = location,
            needsCalibration = needsCal,
            extractionConfidence = if (sku != null) 0.94f else 0.85f
        )
    }

    private fun detectCategory(name: String): String {
        val lower = name.lowercase()
        return when {
            lower.contains("caliper") || lower.contains("screw") || lower.contains("pendulum") ||
                    lower.contains("spring") || lower.contains("pulley") || lower.contains("track") ||
                    lower.contains("mass") || lower.contains("weight") || lower.contains("incline") -> "Mechanics"

            lower.contains("lens") || lower.contains("mirror") || lower.contains("prism") ||
                    lower.contains("laser") || lower.contains("bench") || lower.contains("slit") ||
                    lower.contains("polarizer") -> "Optics"

            lower.contains("multimeter") || lower.contains("resistor") || lower.contains("rheostat") ||
                    lower.contains("galvanometer") || lower.contains("potentiometer") ||
                    lower.contains("voltmeter") || lower.contains("ammeter") || lower.contains("circuit") -> "Electronics"

            lower.contains("calorimeter") || lower.contains("thermometer") || lower.contains("burner") ||
                    lower.contains("flask") || lower.contains("hygrometer") -> "Thermodynamics"

            lower.contains("sonometer") || lower.contains("tuning fork") || lower.contains("resonance") -> "Waves & Acoustics"

            else -> "General Lab"
        }
    }

    private fun generateSku(name: String): String {
        val prefix = when (detectCategory(name)) {
            "Mechanics" -> "MEC"
            "Optics" -> "OPT"
            "Electronics" -> "ELE"
            "Thermodynamics" -> "THM"
            "Waves & Acoustics" -> "WAV"
            else -> "LAB"
        }
        val rand = (100..999).random()
        return "$prefix-$rand"
    }

    fun getSampleInventoryManifest(): String {
        return """
            24x Vernier Calipers (0.01cm precision) | SKU: MEC-VC-01 | Cost: $35.00 | Shelf A1 | Calibrated
            12x Micrometer Screw Gauges (0.001cm) | SKU: MEC-SG-04 | Cost: $42.00 | Shelf A2 | Needs Calibration
            8x Simple Pendulum Apparatus w/ Heavy Bobs | SKU: MEC-PD-11 | Cost: $28.50 | Cabinet M3
            16x Equilateral Glass Prisms (Crown glass) | SKU: OPT-PR-60 | Cost: $14.00 | Shelf B1
            10x Convex Lenses f=15cm focal length | SKU: OPT-LX-15 | Cost: $12.50 | Shelf B2
            6x He-Ne Laser Demonstration Source 632.8nm | SKU: OPT-LZ-02 | Cost: $280.00 | Room 104 Locker | Needs Calibration
            15x Digital Multimeters True RMS | SKU: ELE-DM-88 | Cost: $48.00 | Bench 3 Drawer | Calibrated
            10x Searle's Apparatus for Young's Modulus | SKU: MEC-SE-09 | Cost: $115.00 | Rack C4
            20x Brass Slotted Weight Sets (500g total) | SKU: MEC-WT-50 | Cost: $22.00 | Bin 5
            6x Sonometer Double Wire Acoustic Box | SKU: WAV-SN-02 | Cost: $65.00 | Rack W1
        """.trimIndent()
    }
}

package com.example.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SampleDataGenerator {

    fun getCurrentDateFormatted(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getCurrentTimestampFormatted(): String {
        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getSamplePackingList(): PackingListEntity {
        val today = getCurrentDateFormatted()
        return PackingListEntity(
            customerName = "Apex Global Logistics Inc.",
            orderNumber = "ORD-2026-100",
            packingDate = today,
            dispatchDate = "2026-09-28",
            destination = "Terminal 4, Central Freight Depot",
            status = "In Packing",
            notes = "Priority export consignment. Verified by Quality Inspector #14."
        )
    }

    private val sampleCatalog = listOf(
        // 1 - 20: Electronics & Control
        Triple("Microcontroller Boards ESP32-WROOM", "SKU-MCU-01", 50 to "Pcs"),
        Triple("Step-Down DC Buck Converters 5A", "SKU-PWR-02", 40 to "Pcs"),
        Triple("Industrial Relay Module 8-Channel", "SKU-RLY-03", 25 to "Pcs"),
        Triple("High-Speed Shielded USB-C Cables 2m", "SKU-CAB-04", 100 to "Pcs"),
        Triple("Solid State Relay 40A DC-AC", "SKU-SSR-05", 20 to "Pcs"),
        Triple("High Torque NEMA 17 Stepper Motors", "SKU-MOT-06", 30 to "Pcs"),
        Triple("Optical Rotary Encoders 600P/R", "SKU-ENC-07", 15 to "Pcs"),
        Triple("Ceramic Capacitor Assortment Kit", "SKU-CAP-08", 10 to "Boxes"),
        Triple("Precision Metal Film Resistors 1/4W", "SKU-RES-09", 15 to "Boxes"),
        Triple("Dual Output Switching Power Supply 24V", "SKU-PSU-10", 12 to "Pcs"),
        Triple("DIN Rail Power Supply 60W MeanWell", "SKU-DIN-11", 10 to "Pcs"),
        Triple("16x2 I2C Character LCD Display Modules", "SKU-LCD-12", 45 to "Pcs"),
        Triple("Hall Effect Current Sensor Modules 30A", "SKU-SEN-13", 35 to "Pcs"),
        Triple("Thermocouple K-Type Sensors 1m Lead", "SKU-THC-14", 50 to "Pcs"),
        Triple("Ultra-Bright Industrial LED Array Modules", "SKU-LED-15", 60 to "Pcs"),
        Triple("RS-485 Serial Interface Transceivers", "SKU-COM-16", 40 to "Pcs"),
        Triple("Heavy Duty Push Button Stations IP65", "SKU-SWI-17", 18 to "Pcs"),
        Triple("Piezoelectric Warning Buzzer Alarms 12V", "SKU-BUZ-18", 50 to "Pcs"),
        Triple("Sub-Miniature Toggle Switches SPDT", "SKU-SWI-19", 120 to "Pcs"),
        Triple("Shielded CAT6 Industrial Ethernet Cable 10m", "SKU-NET-20", 25 to "Rolls"),

        // 21 - 40: Mechanical & Fasteners
        Triple("Stainless Steel Hex Cap Screws M6x25", "SKU-FST-21", 500 to "Pcs"),
        Triple("Metric Socket Head Screws M4x16", "SKU-FST-22", 600 to "Pcs"),
        Triple("High Tensile Grade 8.8 Hex Bolts M8x40", "SKU-FST-23", 400 to "Pcs"),
        Triple("Spring & Flat Washers Kit M6 Stainless", "SKU-FST-24", 8 to "Boxes"),
        Triple("Nylon Insert Lock Nuts M8 304 Steel", "SKU-FST-25", 500 to "Pcs"),
        Triple("Deep Groove Ball Bearings 608ZZ ABEC-9", "SKU-BRG-26", 80 to "Pcs"),
        Triple("Linear Motion Ball Bearings LM8UU", "SKU-BRG-27", 60 to "Pcs"),
        Triple("Flexible Aluminum Shaft Couplers 5x8mm", "SKU-CPL-28", 40 to "Pcs"),
        Triple("Hardened Steel Linear Rods 8mm x 400mm", "SKU-ROD-29", 20 to "Pcs"),
        Triple("T-Nut Drop-In Hammer Nuts 2020 Series", "SKU-ALU-30", 300 to "Pcs"),
        Triple("Extruded Aluminum Angle Brackets 20x20", "SKU-ALU-31", 100 to "Pcs"),
        Triple("Miniature Pneumatic Push-In Fittings 6mm", "SKU-PNE-32", 75 to "Pcs"),
        Triple("Polyurethane Air Tubing 6mm OD (50m Coil)", "SKU-PNE-33", 4 to "Rolls"),
        Triple("Pneumatic Solenoid Air Valve 5/2 Way 24V", "SKU-PNE-34", 12 to "Pcs"),
        Triple("Compact Guided Air Cylinders 16mm Bore", "SKU-CYL-35", 8 to "Pcs"),
        Triple("Brass Ball Valves 1/2 Inch NPT Female", "SKU-VLV-36", 15 to "Pcs"),
        Triple("Hydraulic High-Pressure NBR O-Ring Kit", "SKU-ORG-37", 5 to "Boxes"),
        Triple("Stainless Steel Hose Clamps 12-20mm", "SKU-CLP-38", 120 to "Pcs"),
        Triple("Precision Ground Steel Dowel Pins 6x30mm", "SKU-PIN-39", 200 to "Pcs"),
        Triple("Medium Strength Threadlocking Fluid 50ml", "SKU-ADH-40", 10 to "Pcs"),

        // 41 - 60: Tools & Assembly Supplies
        Triple("Digital Vernier Caliper 150mm Stainless", "SKU-TOL-41", 6 to "Pcs"),
        Triple("Automatic Wire Stripper & Crimping Tool", "SKU-TOL-42", 8 to "Pcs"),
        Triple("ESD-Safe Precision Tweezers Set (7-Pc)", "SKU-TOL-43", 15 to "Sets"),
        Triple("Temperature Controlled Soldering Iron 60W", "SKU-SOL-44", 10 to "Pcs"),
        Triple("Lead-Free Solder Wire Spool 0.8mm 500g", "SKU-SOL-45", 14 to "Rolls"),
        Triple("Desoldering Braid Wick 2.5mm Width", "SKU-SOL-46", 30 to "Rolls"),
        Triple("Compact Digital Multimeter True-RMS CAT-III", "SKU-MET-47", 5 to "Pcs"),
        Triple("Industrial Heat Gun 1800W Dual Temperature", "SKU-HGN-48", 4 to "Pcs"),
        Triple("Polyolefin Heat Shrink Tubing Assortment", "SKU-HST-49", 20 to "Boxes"),
        Triple("Ratcheting Insulated Terminal Crimper", "SKU-TOL-50", 6 to "Pcs"),
        Triple("Ball-End Metric Hex Key Wrench Set (9-Pc)", "SKU-TOL-51", 12 to "Sets"),
        Triple("Heavy-Duty Utility Knives + 10x Blades", "SKU-TOL-52", 20 to "Sets"),
        Triple("Magnetic Precision Screwdriver Bit Set 64-in-1", "SKU-TOL-53", 10 to "Sets"),
        Triple("Diagonal Cutting Pliers High-Leverage 8-Inch", "SKU-TOL-54", 12 to "Pcs"),
        Triple("Self-Adjusting Cable Tie Tension & Cut Tool", "SKU-TOL-55", 5 to "Pcs"),
        Triple("UV-Resistant Black Nylon Cable Ties 300mm", "SKU-TIE-56", 20 to "Bags"),
        Triple("Spiral Cable Wrap Organizer 10m Dispenser", "SKU-ORG-57", 10 to "Rolls"),
        Triple("Braided Expandable Cable Sleeving 12mm", "SKU-SLV-58", 8 to "Rolls"),
        Triple("High-Temp Kapton Polyimide Tape 20mm x 33m", "SKU-TAP-59", 25 to "Rolls"),
        Triple("Dual-Sided Thermal Conductive Tape 25mm", "SKU-TAP-60", 18 to "Rolls"),

        // 61 - 80: Packaging & Protection Supplies
        Triple("Corrugated Shipping Cartons Double-Wall #4", "SKU-PKG-61", 50 to "Cartons"),
        Triple("Heavy Duty Carton Sealing Tape 48mm x 100m", "SKU-TAP-62", 36 to "Rolls"),
        Triple("Industrial Pistol-Grip Tape Dispenser Gun", "SKU-DSP-63", 8 to "Pcs"),
        Triple("Antistatic Air Bubble Cushioning Roll 50m", "SKU-PKG-64", 6 to "Rolls"),
        Triple("Biodegradable Cornstarch Packing Peanuts", "SKU-PKG-65", 4 to "Bags"),
        Triple("Recycled Kraft Cushioning Paper Roll 300m", "SKU-PKG-66", 5 to "Rolls"),
        Triple("Moisture Absorber Silica Gel Packets 10g", "SKU-PKG-67", 10 to "Bags"),
        Triple("Direct Thermal Barcode Labels 4x6 Inch", "SKU-LBL-68", 12 to "Rolls"),
        Triple("Bright Red 'FRAGILE - HANDLE WITH CARE' Tape", "SKU-LBL-69", 20 to "Rolls"),
        Triple("'THIS WAY UP' Orientation Arrow Labels", "SKU-LBL-70", 15 to "Rolls"),
        Triple("Self-Adhesive Packing List Enclosed Pouches", "SKU-ENV-71", 10 to "Boxes"),
        Triple("Cast Stretch Film Pallet Wrap 500mm 23mic", "SKU-WRK-72", 12 to "Rolls"),
        Triple("Heavy-Duty Polypropylene Strapping Band 16mm", "SKU-STP-73", 4 to "Rolls"),
        Triple("Manual Steel Strapping Tensioner & Sealer", "SKU-STP-74", 2 to "Sets"),
        Triple("Galvanized Metal Strapping Seals 16mm", "SKU-STP-75", 5 to "Boxes"),
        Triple("Rigid Cardboard Edge Angle Protectors 1m", "SKU-EDG-76", 80 to "Pcs"),
        Triple("Polyethylene Closed-Cell Foam Sheets 3mm", "SKU-FOM-77", 40 to "Pcs"),
        Triple("Metallic Silver Anti-Static Shielding Bags", "SKU-BAG-78", 8 to "Boxes"),
        Triple("Water-Activated Reinforced Gummed Paper Tape", "SKU-TAP-79", 15 to "Rolls"),
        Triple("Tamper-Evident Red Security Barcode Seals", "SKU-SEC-80", 250 to "Pcs"),

        // 81 - 100: Assemblies, Sensors & Final QC
        Triple("Brushless DC Axial Cooling Fans 80mm 24V", "SKU-FAN-81", 30 to "Pcs"),
        Triple("Finger Safety Metal Wire Grills for 80mm Fan", "SKU-FAN-82", 30 to "Pcs"),
        Triple("Extruded Aluminum Heat Sinks 120x60x25mm", "SKU-HSK-83", 20 to "Pcs"),
        Triple("High-Performance Thermal Compound Syringes 30g", "SKU-THC-84", 25 to "Pcs"),
        Triple("Mushroom Head Emergency Stop Switch with Box", "SKU-SWI-85", 15 to "Pcs"),
        Triple("Pilot LED Indicator Light 22mm Emerald Green", "SKU-PLT-86", 40 to "Pcs"),
        Triple("Pilot LED Indicator Light 22mm Bright Red", "SKU-PLT-87", 40 to "Pcs"),
        Triple("Keyed Industrial 2-Position Selector Switch", "SKU-SWI-88", 18 to "Pcs"),
        Triple("Multi-Layer LED Tower Stack Signal Light 24V", "SKU-SIG-89", 6 to "Pcs"),
        Triple("Inductive Proximity Sensor M12 Shielded NPN", "SKU-SEN-90", 22 to "Pcs"),
        Triple("Polarized Retroreflective Optical Sensor 3m", "SKU-SEN-91", 16 to "Pcs"),
        Triple("Nickel-Plated Brass Cable Gland M16 IP68", "SKU-GLD-92", 60 to "Pcs"),
        Triple("Nickel-Plated Brass Cable Gland M20 IP68", "SKU-GLD-93", 60 to "Pcs"),
        Triple("Diecast Aluminum Weatherproof Junction Enclosure", "SKU-ENC-94", 12 to "Pcs"),
        Triple("Standard Slotted Steel DIN Rail 35mm x 1m", "SKU-DIN-95", 25 to "Pcs"),
        Triple("Spring-Clamp Feed-Through Terminal Blocks 2.5mm", "SKU-TRM-96", 300 to "Pcs"),
        Triple("Heavy Duty End Stop Clamps for DIN Terminals", "SKU-TRM-97", 80 to "Pcs"),
        Triple("Yellow-Green Grounding Earth Terminal Blocks", "SKU-TRM-98", 60 to "Pcs"),
        Triple("Certified Calibration & QC Inspection Reports", "SKU-DOC-99", 1 to "Sets"),
        Triple("Master Consignment Packing Slip & Cargo Seals", "SKU-DOC-100", 1 to "Sets")
    )

    fun generate100SampleItems(listId: Long): List<PackingItemEntity> {
        val items = mutableListOf<PackingItemEntity>()
        val timeNow = getCurrentTimestampFormatted()

        for (i in 1..100) {
            val catalogIndex = i - 1
            val entry = sampleCatalog.getOrElse(catalogIndex) {
                Triple("Logistics Item #$i", "SKU-ITM-${String.format(Locale.US, "%03d", i)}", 10 to "Pcs")
            }
            val boxNum = ((i - 1) / 10) + 1 // 10 items per box across Box 1 to 10
            // Pre-pack first 38 items to give realistic active state
            val isPacked = i <= 38
            val remarks = when {
                i == 1 || i == 6 -> "ESD Sensitive - Static Bag"
                i in 21..25 -> "High Tensile - Heavy"
                i in 61..65 -> "Outer Packaging - Top Layer"
                i == 99 -> "Signed by Lead Inspector"
                i == 100 -> "Do Not Break Tamper Seal"
                else -> ""
            }

            items.add(
                PackingItemEntity(
                    listId = listId,
                    sno = i,
                    name = entry.first,
                    sku = entry.second,
                    quantity = entry.third.first,
                    unit = entry.third.second,
                    boxNumber = "Box #$boxNum",
                    isPacked = isPacked,
                    packedDate = if (isPacked) timeNow else "",
                    remarks = remarks
                )
            )
        }
        return items
    }

    fun generateCustom100Template(
        listId: Long,
        customerName: String,
        prefix: String = "Part"
    ): List<PackingItemEntity> {
        val items = mutableListOf<PackingItemEntity>()
        for (i in 1..100) {
            val boxNum = ((i - 1) / 10) + 1
            val formattedSno = String.format(Locale.US, "%03d", i)
            items.add(
                PackingItemEntity(
                    listId = listId,
                    sno = i,
                    name = "$prefix #$formattedSno ($customerName)",
                    sku = "SKU-${prefix.uppercase(Locale.US)}-$formattedSno",
                    quantity = 10,
                    unit = "Pcs",
                    boxNumber = "Box #$boxNum",
                    isPacked = false,
                    packedDate = "",
                    remarks = "Serial Item $i of 100"
                )
            )
        }
        return items
    }
}

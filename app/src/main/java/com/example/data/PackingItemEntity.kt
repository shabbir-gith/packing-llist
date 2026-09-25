package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "packing_items",
    foreignKeys = [
        ForeignKey(
            entity = PackingListEntity::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["listId"]),
        Index(value = ["listId", "sno"])
    ]
)
data class PackingItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val listId: Long,
    val sno: Int,               // Serial Number (1 - 100)
    val name: String,           // Item description
    val sku: String = "",       // Stock keeping unit / code
    val quantity: Int = 1,      // Quantity
    val unit: String = "Pcs",   // Unit of measurement: Pcs, Boxes, Kg, Cartons, Rolls, Bags, Sets
    val boxNumber: String = "Box 1", // Carton / Box assignment
    val isPacked: Boolean = false,
    val packedDate: String = "", // Timestamp when marked packed (e.g., "24 Sep 2026, 14:30")
    val remarks: String = ""    // Special remarks (e.g., "Fragile", "QC Verified")
)

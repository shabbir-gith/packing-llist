package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "packing_lists")
data class PackingListEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerName: String,
    val orderNumber: String,
    val packingDate: String,    // e.g. "2026-09-24"
    val dispatchDate: String,   // e.g. "2026-09-28"
    val destination: String = "",
    val status: String = "In Packing", // "Draft", "In Packing", "Completed", "Dispatched"
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

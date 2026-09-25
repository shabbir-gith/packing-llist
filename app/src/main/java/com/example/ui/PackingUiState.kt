package com.example.ui

import com.example.data.PackingItemEntity
import com.example.data.PackingListEntity

enum class SnoRangeFilter(val label: String, val from: Int, val to: Int) {
    ALL("All 1-100", 1, 100),
    RANGE_1_25("1-25", 1, 25),
    RANGE_26_50("26-50", 26, 50),
    RANGE_51_75("51-75", 51, 75),
    RANGE_76_100("76-100", 76, 100)
}

enum class ItemStatusFilter(val label: String) {
    ALL("All"),
    PENDING("Pending"),
    PACKED("Packed")
}

data class PackingUiState(
    val isLoading: Boolean = true,
    val allLists: List<PackingListEntity> = emptyList(),
    val activeList: PackingListEntity? = null,
    val allItems: List<PackingItemEntity> = emptyList(),
    val filteredItems: List<PackingItemEntity> = emptyList(),
    val searchQuery: String = "",
    val snoRange: SnoRangeFilter = SnoRangeFilter.ALL,
    val statusFilter: ItemStatusFilter = ItemStatusFilter.ALL,
    val totalCount: Int = 0,
    val packedCount: Int = 0,
    val totalQuantity: Int = 0,
    val packedQuantity: Int = 0,
    val boxCount: Int = 0,
    val activeListMenuOpen: Boolean = false,
    val showNewListDialog: Boolean = false,
    val showEditListDialog: Boolean = false,
    val showAddItemDialog: Boolean = false,
    val editingItem: PackingItemEntity? = null,
    val showBatchPackDialog: Boolean = false,
    val showJumpToSnoDialog: Boolean = false,
    val showShareDialog: Boolean = false,
    val messageSnackbar: String? = null
) {
    val progressFraction: Float
        get() = if (totalCount > 0) packedCount.toFloat() / totalCount else 0f

    val progressPercent: Int
        get() = (progressFraction * 100).toInt()
}

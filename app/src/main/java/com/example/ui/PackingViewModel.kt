package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.PackingItemEntity
import com.example.data.PackingListEntity
import com.example.data.PackingRepository
import com.example.data.SampleDataGenerator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class PackingFilterState(
    val query: String = "",
    val range: SnoRangeFilter = SnoRangeFilter.ALL,
    val status: ItemStatusFilter = ItemStatusFilter.ALL
)

data class PackingDialogState(
    val showNewListDialog: Boolean = false,
    val showEditListDialog: Boolean = false,
    val showAddItemDialog: Boolean = false,
    val editingItem: PackingItemEntity? = null,
    val showBatchPackDialog: Boolean = false,
    val showJumpToSnoDialog: Boolean = false,
    val showShareDialog: Boolean = false,
    val snackbarMessage: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class PackingViewModel(
    private val repository: PackingRepository
) : ViewModel() {

    private val _selectedListId = MutableStateFlow<Long?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _snoRange = MutableStateFlow(SnoRangeFilter.ALL)
    private val _statusFilter = MutableStateFlow(ItemStatusFilter.ALL)
    private val _dialogState = MutableStateFlow(PackingDialogState())

    private val _allLists = repository.allLists

    private val _activeListFlow = _selectedListId.flatMapLatest { id ->
        if (id != null) {
            repository.getListById(id)
        } else {
            flowOf(null)
        }
    }

    private val _itemsFlow = _selectedListId.flatMapLatest { id ->
        if (id != null) {
            repository.getItemsForList(id)
        } else {
            flowOf(emptyList())
        }
    }

    private val _filterState = combine(_searchQuery, _snoRange, _statusFilter) { q, r, s ->
        PackingFilterState(query = q, range = r, status = s)
    }

    val uiState: StateFlow<PackingUiState> = combine(
        _allLists,
        _activeListFlow,
        _itemsFlow,
        _filterState,
        _dialogState
    ) { allLists, activeList, allItems, filter, dialogs ->
        val filtered = allItems.filter { item ->
            val matchesRange = when (filter.range) {
                SnoRangeFilter.ALL -> true
                else -> item.sno in filter.range.from..filter.range.to
            }
            val matchesStatus = when (filter.status) {
                ItemStatusFilter.ALL -> true
                ItemStatusFilter.PENDING -> !item.isPacked
                ItemStatusFilter.PACKED -> item.isPacked
            }
            val matchesSearch = if (filter.query.isBlank()) {
                true
            } else {
                val q = filter.query.trim().lowercase(Locale.ROOT)
                item.sno.toString() == q ||
                        item.sno.toString().startsWith(q) ||
                        item.name.lowercase(Locale.ROOT).contains(q) ||
                        item.sku.lowercase(Locale.ROOT).contains(q) ||
                        item.boxNumber.lowercase(Locale.ROOT).contains(q)
            }
            matchesRange && matchesStatus && matchesSearch
        }

        val totalCount = allItems.size
        val packedCount = allItems.count { it.isPacked }
        val totalQty = allItems.sumOf { it.quantity }
        val packedQty = allItems.filter { it.isPacked }.sumOf { it.quantity }
        val distinctBoxes = allItems.map { it.boxNumber.trim() }.filter { it.isNotEmpty() }.distinct().size

        PackingUiState(
            isLoading = false,
            allLists = allLists,
            activeList = activeList,
            allItems = allItems,
            filteredItems = filtered,
            searchQuery = filter.query,
            snoRange = filter.range,
            statusFilter = filter.status,
            totalCount = totalCount,
            packedCount = packedCount,
            totalQuantity = totalQty,
            packedQuantity = packedQty,
            boxCount = distinctBoxes,
            showNewListDialog = dialogs.showNewListDialog,
            showEditListDialog = dialogs.showEditListDialog,
            showAddItemDialog = dialogs.showAddItemDialog,
            editingItem = dialogs.editingItem,
            showBatchPackDialog = dialogs.showBatchPackDialog,
            showJumpToSnoDialog = dialogs.showJumpToSnoDialog,
            showShareDialog = dialogs.showShareDialog,
            messageSnackbar = dialogs.snackbarMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PackingUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            val initialId = repository.ensureInitialData()
            _selectedListId.value = initialId
        }
    }

    fun selectList(listId: Long) {
        _selectedListId.value = listId
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSnoRange(range: SnoRangeFilter) {
        _snoRange.value = range
    }

    fun setStatusFilter(filter: ItemStatusFilter) {
        _statusFilter.value = filter
    }

    fun toggleItemPacked(item: PackingItemEntity) {
        viewModelScope.launch {
            repository.toggleItemPacked(item)
            val action = if (!item.isPacked) "packed" else "unpacked"
            showSnackbar("Item #${item.sno} marked $action")
        }
    }

    fun markAllPacked(packed: Boolean) {
        val listId = _selectedListId.value ?: return
        viewModelScope.launch {
            repository.markAllPacked(listId, packed)
            val label = if (packed) "All items packed!" else "All items reset to pending"
            showSnackbar(label)
            closeBatchDialog()
        }
    }

    fun markRangePacked(fromSno: Int, toSno: Int, packed: Boolean) {
        val listId = _selectedListId.value ?: return
        viewModelScope.launch {
            val start = minOf(fromSno, toSno)
            val end = maxOf(fromSno, toSno)
            repository.markRangePacked(listId, start, end, packed)
            val label = if (packed) "Packed S.No $start to $end" else "Unpacked S.No $start to $end"
            showSnackbar(label)
            closeBatchDialog()
        }
    }

    fun saveItem(
        sno: Int,
        name: String,
        sku: String,
        quantity: Int,
        unit: String,
        boxNumber: String,
        remarks: String,
        editingId: Long?
    ) {
        val listId = _selectedListId.value ?: return
        viewModelScope.launch {
            if (editingId != null && editingId > 0) {
                val existing = uiState.value.allItems.find { it.id == editingId }
                if (existing != null) {
                    val updated = existing.copy(
                        sno = sno,
                        name = name.trim(),
                        sku = sku.trim(),
                        quantity = quantity,
                        unit = unit.trim(),
                        boxNumber = boxNumber.trim(),
                        remarks = remarks.trim()
                    )
                    repository.updateItem(updated)
                    showSnackbar("Updated Item #$sno")
                }
            } else {
                val newItem = PackingItemEntity(
                    listId = listId,
                    sno = sno,
                    name = name.trim(),
                    sku = sku.trim(),
                    quantity = quantity,
                    unit = unit.trim(),
                    boxNumber = boxNumber.trim(),
                    remarks = remarks.trim()
                )
                repository.insertItem(newItem)
                showSnackbar("Added Item #$sno: ${name.take(20)}")
            }
            closeItemDialog()
        }
    }

    fun deleteItem(item: PackingItemEntity) {
        viewModelScope.launch {
            repository.deleteItem(item)
            showSnackbar("Deleted Item #${item.sno}")
        }
    }

    fun createNewList(
        customerName: String,
        orderNumber: String,
        packingDate: String,
        dispatchDate: String,
        destination: String,
        notes: String,
        generate100: Boolean,
        prefix: String?
    ) {
        viewModelScope.launch {
            val newId = if (generate100) {
                repository.createListWith100Items(
                    customerName = customerName.trim(),
                    orderNumber = orderNumber.trim(),
                    packingDate = packingDate.trim(),
                    dispatchDate = dispatchDate.trim(),
                    destination = destination.trim(),
                    notes = notes.trim(),
                    itemPrefix = prefix
                )
            } else {
                repository.createEmptyList(
                    customerName = customerName.trim(),
                    orderNumber = orderNumber.trim(),
                    packingDate = packingDate.trim(),
                    dispatchDate = dispatchDate.trim(),
                    destination = destination.trim(),
                    notes = notes.trim()
                )
            }
            _selectedListId.value = newId
            _searchQuery.value = ""
            _snoRange.value = SnoRangeFilter.ALL
            _statusFilter.value = ItemStatusFilter.ALL
            showSnackbar("Packing list created for $customerName")
            closeNewListDialog()
        }
    }

    fun updateActiveList(
        customerName: String,
        orderNumber: String,
        packingDate: String,
        dispatchDate: String,
        destination: String,
        status: String,
        notes: String
    ) {
        val current = uiState.value.activeList ?: return
        viewModelScope.launch {
            val updated = current.copy(
                customerName = customerName.trim(),
                orderNumber = orderNumber.trim(),
                packingDate = packingDate.trim(),
                dispatchDate = dispatchDate.trim(),
                destination = destination.trim(),
                status = status,
                notes = notes.trim(),
                updatedAt = System.currentTimeMillis()
            )
            repository.updateList(updated)
            showSnackbar("Updated list details")
            closeEditListDialog()
        }
    }

    fun deleteActiveList() {
        val current = uiState.value.activeList ?: return
        viewModelScope.launch {
            repository.deleteList(current)
            val remaining = uiState.value.allLists.filter { it.id != current.id }
            if (remaining.isNotEmpty()) {
                _selectedListId.value = remaining.first().id
            } else {
                val newId = repository.ensureInitialData()
                _selectedListId.value = newId
            }
            showSnackbar("Deleted packing list: ${current.customerName}")
            closeEditListDialog()
        }
    }

    fun fill100Template(prefix: String) {
        val list = uiState.value.activeList ?: return
        viewModelScope.launch {
            repository.fill100Template(list.id, list.customerName, prefix)
            showSnackbar("Generated 100 items for ${list.customerName}")
        }
    }

    fun fill100IndustrialCatalog() {
        val list = uiState.value.activeList ?: return
        viewModelScope.launch {
            repository.fill100IndustrialCatalog(list.id)
            showSnackbar("Restored standard 100 items catalog")
        }
    }

    fun openNewListDialog() {
        _dialogState.update { it.copy(showNewListDialog = true) }
    }

    fun closeNewListDialog() {
        _dialogState.update { it.copy(showNewListDialog = false) }
    }

    fun openEditListDialog() {
        _dialogState.update { it.copy(showEditListDialog = true) }
    }

    fun closeEditListDialog() {
        _dialogState.update { it.copy(showEditListDialog = false) }
    }

    fun openAddItemDialog() {
        _dialogState.update { it.copy(showAddItemDialog = true, editingItem = null) }
    }

    fun openEditItemDialog(item: PackingItemEntity) {
        _dialogState.update { it.copy(showAddItemDialog = true, editingItem = item) }
    }

    fun closeItemDialog() {
        _dialogState.update { it.copy(showAddItemDialog = false, editingItem = null) }
    }

    fun openBatchDialog() {
        _dialogState.update { it.copy(showBatchPackDialog = true) }
    }

    fun closeBatchDialog() {
        _dialogState.update { it.copy(showBatchPackDialog = false) }
    }

    fun openJumpToSnoDialog() {
        _dialogState.update { it.copy(showJumpToSnoDialog = true) }
    }

    fun closeJumpToSnoDialog() {
        _dialogState.update { it.copy(showJumpToSnoDialog = false) }
    }

    fun openShareDialog() {
        _dialogState.update { it.copy(showShareDialog = true) }
    }

    fun closeShareDialog() {
        _dialogState.update { it.copy(showShareDialog = false) }
    }

    fun dismissSnackbar() {
        _dialogState.update { it.copy(snackbarMessage = null) }
    }

    private fun showSnackbar(msg: String) {
        _dialogState.update { it.copy(snackbarMessage = msg) }
    }

    fun generateManifestText(): String {
        val list = uiState.value.activeList ?: return "No Packing List Loaded"
        val items = uiState.value.allItems
        val total = items.size
        val packed = items.count { it.isPacked }
        val totalQty = items.sumOf { it.quantity }
        val packedQty = items.filter { it.isPacked }.sumOf { it.quantity }

        val sb = StringBuilder()
        sb.appendLine("==========================================")
        sb.appendLine("             PACKING LIST MANIFEST        ")
        sb.appendLine("==========================================")
        sb.appendLine("Customer:      ${list.customerName}")
        sb.appendLine("Order / Ref #: ${list.orderNumber}")
        sb.appendLine("Packing Date:  ${list.packingDate}")
        sb.appendLine("Dispatch Date: ${list.dispatchDate}")
        if (list.destination.isNotBlank()) {
            sb.appendLine("Destination:   ${list.destination}")
        }
        sb.appendLine("Status:        ${list.status}")
        sb.appendLine("Packing Meter: $packed / $total items packed (${if (total > 0) (packed * 100 / total) else 0}%)")
        sb.appendLine("Quantity:      $packedQty / $totalQty units packed")
        if (list.notes.isNotBlank()) {
            sb.appendLine("Notes:         ${list.notes}")
        }
        sb.appendLine("------------------------------------------")
        sb.appendLine("S.No | Status | Qty | Unit | Box   | Description")
        sb.appendLine("------------------------------------------")

        for (item in items) {
            val statusStr = if (item.isPacked) "[✓] PACKED" else "[ ] PENDING"
            val snoStr = String.format(Locale.US, "#%03d", item.sno)
            val packedInfo = if (item.isPacked && item.packedDate.isNotBlank()) " (${item.packedDate})" else ""
            sb.appendLine("$snoStr | $statusStr | ${item.quantity} ${item.unit} | ${item.boxNumber} | ${item.name}$packedInfo")
            if (item.remarks.isNotBlank()) {
                sb.appendLine("      ↳ Note: ${item.remarks}")
            }
        }
        sb.appendLine("==========================================")
        sb.appendLine("End of Packing List (${items.size} items total)")
        sb.appendLine("Generated via Packing List 100 on ${SampleDataGenerator.getCurrentTimestampFormatted()}")
        sb.appendLine("==========================================")
        return sb.toString()
    }
}

class PackingViewModelFactory(private val repository: PackingRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PackingViewModel::class.java)) {
            return PackingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

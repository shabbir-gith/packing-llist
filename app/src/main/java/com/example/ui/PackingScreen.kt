package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddEditItemDialog
import com.example.ui.components.BatchActionsDialog
import com.example.ui.components.CreateEditListDialog
import com.example.ui.components.FilterAndSearchBar
import com.example.ui.components.JumpToSnoDialog
import com.example.ui.components.PackingHeaderCard
import com.example.ui.components.PackingItemCard
import com.example.ui.components.ShareManifestDialog
import com.example.ui.theme.NavyPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackingScreen(
    viewModel: PackingViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var showMenu by remember { mutableStateOf(false) }
    var showListDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.messageSnackbar) {
        val msg = uiState.messageSnackbar
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissSnackbar()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showListDropdown = true }
                            .padding(vertical = 4.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = uiState.activeList?.customerName ?: "Packing List 100",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Switch List",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "Packing List • ${uiState.totalCount} Items",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        // Dropdown to switch between customer packing lists
                        DropdownMenu(
                            expanded = showListDropdown,
                            onDismissRequest = { showListDropdown = false }
                        ) {
                            Text(
                                text = "Select Packing List",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            HorizontalDivider()

                            uiState.allLists.forEach { item ->
                                val isSelected = item.id == uiState.activeList?.id
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                }
                                                Text(
                                                    text = item.customerName,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                            Text(
                                                text = "Date: ${item.packingDate} • ${item.orderNumber}",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.selectList(item.id)
                                        showListDropdown = false
                                    }
                                )
                            }

                            HorizontalDivider()

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.PostAdd,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Create New Packing List", fontWeight = FontWeight.SemiBold)
                                    }
                                },
                                onClick = {
                                    showListDropdown = false
                                    viewModel.openNewListDialog()
                                }
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.openNewListDialog() },
                        modifier = Modifier.testTag("new_list_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PostAdd,
                            contentDescription = "New Packing List",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.testTag("top_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options"
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Jump to S.No") },
                            leadingIcon = { Icon(Icons.Default.NearMe, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.openJumpToSnoDialog()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Batch Packing Actions") },
                            leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.openBatchDialog()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Share / Export Manifest") },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.openShareDialog()
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Edit List Details") },
                            leadingIcon = { Icon(Icons.Default.ContentPaste, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.openEditListDialog()
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openAddItemDialog() },
                modifier = Modifier.testTag("add_item_fab"),
                containerColor = NavyPrimary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Item"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Item", fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("packing_items_list"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header Card with Customer Info, Dates, Progress Bar, Metrics, Action Buttons
                item(key = "header_card") {
                    PackingHeaderCard(
                        list = uiState.activeList,
                        totalItems = uiState.totalCount,
                        packedItems = uiState.packedCount,
                        totalQty = uiState.totalQuantity,
                        packedQty = uiState.packedQuantity,
                        boxCount = uiState.boxCount,
                        progressPercent = uiState.progressPercent,
                        progressFraction = uiState.progressFraction,
                        onEditList = { viewModel.openEditListDialog() },
                        onJumpToSno = { viewModel.openJumpToSnoDialog() },
                        onBatchActions = { viewModel.openBatchDialog() },
                        onShare = { viewModel.openShareDialog() }
                    )
                }

                // Filter & Search Bar
                item(key = "filter_bar") {
                    FilterAndSearchBar(
                        searchQuery = uiState.searchQuery,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        selectedRange = uiState.snoRange,
                        onRangeSelected = { viewModel.setSnoRange(it) },
                        selectedStatus = uiState.statusFilter,
                        onStatusSelected = { viewModel.setStatusFilter(it) },
                        resultCount = uiState.filteredItems.size,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // Empty state if filtered results are 0
                if (uiState.filteredItems.isEmpty()) {
                    item(key = "empty_state") {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                                .testTag("empty_state_container")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterAltOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (uiState.allItems.isEmpty()) "No Items in this Packing List" else "No matching items found",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (uiState.allItems.isEmpty()) {
                                        "Use the button below to auto-fill 100 items or add items manually."
                                    } else {
                                        "Try adjusting your S.No range filter or search term."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(14.dp))

                                if (uiState.allItems.isEmpty()) {
                                    OutlinedButton(
                                        onClick = { viewModel.fill100IndustrialCatalog() }
                                    ) {
                                        Text("Fill 100 Industrial Items")
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.setSearchQuery("")
                                            viewModel.setSnoRange(SnoRangeFilter.ALL)
                                            viewModel.setStatusFilter(ItemStatusFilter.ALL)
                                        }
                                    ) {
                                        Text("Reset Filters")
                                    }
                                }
                            }
                        }
                    }
                }

                // 100 Items List (Items with S.No)
                items(
                    items = uiState.filteredItems,
                    key = { it.id }
                ) { item ->
                    PackingItemCard(
                        item = item,
                        onTogglePacked = { viewModel.toggleItemPacked(it) },
                        onEdit = { viewModel.openEditItemDialog(it) },
                        onDelete = { viewModel.deleteItem(it) }
                    )
                }
            }
        }

        // Dialogs
        if (uiState.showNewListDialog) {
            CreateEditListDialog(
                listToEdit = null,
                onDismiss = { viewModel.closeNewListDialog() },
                onCreate = { cust, ord, pDate, dDate, dest, notes, gen100, prefix ->
                    viewModel.createNewList(cust, ord, pDate, dDate, dest, notes, gen100, prefix)
                },
                onUpdate = { _, _, _, _, _, _, _ -> }
            )
        }

        if (uiState.showEditListDialog) {
            CreateEditListDialog(
                listToEdit = uiState.activeList,
                onDismiss = { viewModel.closeEditListDialog() },
                onCreate = { _, _, _, _, _, _, _, _ -> },
                onUpdate = { cust, ord, pDate, dDate, dest, status, notes ->
                    viewModel.updateActiveList(cust, ord, pDate, dDate, dest, status, notes)
                },
                onDelete = { viewModel.deleteActiveList() }
            )
        }

        if (uiState.showAddItemDialog) {
            val maxSno = uiState.allItems.maxOfOrNull { it.sno } ?: 0
            val suggested = (maxSno + 1).coerceAtMost(100)
            AddEditItemDialog(
                itemToEdit = uiState.editingItem,
                suggestedSno = suggested,
                onDismiss = { viewModel.closeItemDialog() },
                onSave = { sno, name, sku, qty, unit, box, remarks, editId ->
                    viewModel.saveItem(sno, name, sku, qty, unit, box, remarks, editId)
                }
            )
        }

        if (uiState.showBatchPackDialog) {
            BatchActionsDialog(
                totalItems = uiState.totalCount,
                onDismiss = { viewModel.closeBatchDialog() },
                onPackRange = { from, to, packed ->
                    viewModel.markRangePacked(from, to, packed)
                },
                onPackAll = { packed ->
                    viewModel.markAllPacked(packed)
                },
                onFill100Catalog = {
                    viewModel.fill100IndustrialCatalog()
                },
                onFill100CustomTemplate = { prefix ->
                    viewModel.fill100Template(prefix)
                }
            )
        }

        if (uiState.showJumpToSnoDialog) {
            JumpToSnoDialog(
                maxSno = uiState.totalCount.coerceAtLeast(1),
                onDismiss = { viewModel.closeJumpToSnoDialog() },
                onJump = { targetSno ->
                    viewModel.closeJumpToSnoDialog()
                    // Scroll to target item in LazyColumn
                    val targetIndex = uiState.filteredItems.indexOfFirst { it.sno >= targetSno }
                    if (targetIndex >= 0) {
                        coroutineScope.launch {
                            // +2 accounts for the header card and filter bar
                            listState.animateScrollToItem((targetIndex + 2).coerceAtLeast(0))
                        }
                    }
                }
            )
        }

        if (uiState.showShareDialog) {
            ShareManifestDialog(
                manifestText = viewModel.generateManifestText(),
                customerName = uiState.activeList?.customerName ?: "Packing List",
                onDismiss = { viewModel.closeShareDialog() }
            )
        }
    }
}

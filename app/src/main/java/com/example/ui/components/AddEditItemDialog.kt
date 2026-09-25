package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PackingItemEntity

@Composable
fun AddEditItemDialog(
    itemToEdit: PackingItemEntity?,
    suggestedSno: Int,
    onDismiss: () -> Unit,
    onSave: (
        sno: Int,
        name: String,
        sku: String,
        quantity: Int,
        unit: String,
        boxNumber: String,
        remarks: String,
        editingId: Long?
    ) -> Unit
) {
    var snoText by remember { mutableStateOf(itemToEdit?.sno?.toString() ?: suggestedSno.toString()) }
    var nameText by remember { mutableStateOf(itemToEdit?.name ?: "") }
    var skuText by remember { mutableStateOf(itemToEdit?.sku ?: "") }
    var quantityText by remember { mutableStateOf(itemToEdit?.quantity?.toString() ?: "1") }
    var unitText by remember { mutableStateOf(itemToEdit?.unit ?: "Pcs") }
    var boxNumberText by remember {
        mutableStateOf(itemToEdit?.boxNumber ?: "Box #${((suggestedSno - 1) / 10) + 1}")
    }
    var remarksText by remember { mutableStateOf(itemToEdit?.remarks ?: "") }

    var isError by remember { mutableStateOf(false) }

    val commonUnits = listOf("Pcs", "Boxes", "Kg", "Cartons", "Rolls", "Sets", "Bags", "Meters")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (itemToEdit != null) "Edit Item #${itemToEdit.sno}" else "Add Packing Item",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = snoText,
                        onValueChange = { snoText = it.filter { c -> c.isDigit() } },
                        label = { Text("S.No (1-100)*") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("item_sno_input")
                    )

                    OutlinedTextField(
                        value = boxNumberText,
                        onValueChange = { boxNumberText = it },
                        label = { Text("Box / Carton") },
                        placeholder = { Text("Box #1") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("item_box_input")
                    )
                }

                OutlinedTextField(
                    value = nameText,
                    onValueChange = {
                        nameText = it
                        if (isError && it.isNotBlank()) isError = false
                    },
                    label = { Text("Item Description *") },
                    placeholder = { Text("e.g. Microcontroller Board") },
                    isError = isError,
                    supportingText = { if (isError) Text("Item name is required", color = MaterialTheme.colorScheme.error) },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("item_name_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = skuText,
                        onValueChange = { skuText = it },
                        label = { Text("SKU / Code") },
                        placeholder = { Text("SKU-101") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("item_sku_input")
                    )

                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it.filter { c -> c.isDigit() } },
                        label = { Text("Quantity*") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("item_quantity_input")
                    )
                }

                // Quick Unit Selector
                Column {
                    Text(
                        text = "Unit of Measure:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        commonUnits.forEach { u ->
                            FilterChip(
                                selected = unitText.equals(u, ignoreCase = true),
                                onClick = { unitText = u },
                                label = { Text(u, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = remarksText,
                    onValueChange = { remarksText = it },
                    label = { Text("Remarks / Package Tag") },
                    placeholder = { Text("e.g. Fragile, Inspection Passed") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("item_remarks_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameText.isBlank()) {
                        isError = true
                        return@Button
                    }
                    val sno = snoText.toIntOrNull()?.coerceIn(1, 100) ?: suggestedSno
                    val qty = quantityText.toIntOrNull()?.coerceAtLeast(1) ?: 1
                    onSave(
                        sno,
                        nameText,
                        skuText,
                        qty,
                        unitText.ifBlank { "Pcs" },
                        boxNumberText.ifBlank { "Box 1" },
                        remarksText,
                        itemToEdit?.id
                    )
                },
                modifier = Modifier.testTag("save_item_button")
            ) {
                Text(if (itemToEdit != null) "Update Item" else "Add Item")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_item_button")
            ) {
                Text("Cancel")
            }
        }
    )
}

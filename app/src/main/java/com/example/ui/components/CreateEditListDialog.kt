package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PackingListEntity
import com.example.data.SampleDataGenerator

@Composable
fun CreateEditListDialog(
    listToEdit: PackingListEntity?,
    onDismiss: () -> Unit,
    onCreate: (
        customerName: String,
        orderNumber: String,
        packingDate: String,
        dispatchDate: String,
        destination: String,
        notes: String,
        generate100: Boolean,
        prefix: String?
    ) -> Unit,
    onUpdate: (
        customerName: String,
        orderNumber: String,
        packingDate: String,
        dispatchDate: String,
        destination: String,
        status: String,
        notes: String
    ) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val today = remember { SampleDataGenerator.getCurrentDateFormatted() }

    var customerNameText by remember { mutableStateOf(listToEdit?.customerName ?: "") }
    var orderNumberText by remember { mutableStateOf(listToEdit?.orderNumber ?: "ORD-${System.currentTimeMillis() % 10000}") }
    var packingDateText by remember { mutableStateOf(listToEdit?.packingDate ?: today) }
    var dispatchDateText by remember { mutableStateOf(listToEdit?.dispatchDate ?: today) }
    var destinationText by remember { mutableStateOf(listToEdit?.destination ?: "") }
    var statusText by remember { mutableStateOf(listToEdit?.status ?: "In Packing") }
    var notesText by remember { mutableStateOf(listToEdit?.notes ?: "") }

    // Creation options
    var generate100Items by remember { mutableStateOf(true) }
    var itemTemplateType by remember { mutableStateOf("catalog") } // "catalog" or "custom"
    var customPrefixText by remember { mutableStateOf("Item") }

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Packing List?") },
            text = { Text("Are you sure you want to delete '${listToEdit?.customerName}' and all its items? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete?.invoke()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete List")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (listToEdit != null) "Edit Packing List" else "New Packing List",
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
                OutlinedTextField(
                    value = customerNameText,
                    onValueChange = {
                        customerNameText = it
                        if (isError && it.isNotBlank()) isError = false
                    },
                    label = { Text("Customer Name *") },
                    placeholder = { Text("e.g. Acme Corp / Blue Ribbon Logistics") },
                    isError = isError,
                    supportingText = { if (isError) Text("Customer name is required", color = MaterialTheme.colorScheme.error) },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("customer_name_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = orderNumberText,
                        onValueChange = { orderNumberText = it },
                        label = { Text("Order / Invoice #") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("order_number_input")
                    )

                    OutlinedTextField(
                        value = packingDateText,
                        onValueChange = { packingDateText = it },
                        label = { Text("Packing Date") },
                        placeholder = { Text("YYYY-MM-DD") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("packing_date_input")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = dispatchDateText,
                        onValueChange = { dispatchDateText = it },
                        label = { Text("Dispatch Date") },
                        placeholder = { Text("YYYY-MM-DD") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("dispatch_date_input")
                    )

                    OutlinedTextField(
                        value = destinationText,
                        onValueChange = { destinationText = it },
                        label = { Text("Destination / Port") },
                        placeholder = { Text("Dock 4 / Cargo Bay") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("destination_input")
                    )
                }

                if (listToEdit != null) {
                    OutlinedTextField(
                        value = statusText,
                        onValueChange = { statusText = it },
                        label = { Text("Status (Draft / In Packing / Dispatched)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Notes / Special Instructions") },
                    placeholder = { Text("e.g. Inspect seal before loading") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("notes_input")
                )

                // If Creating a new list: 100 Items Template Option
                if (listToEdit == null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = generate100Items,
                                    onCheckedChange = { generate100Items = it },
                                    modifier = Modifier.testTag("generate_100_checkbox")
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "Generate 100 Items Sheet",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Pre-fills S.No 1 through 100 ready for packing",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                                    )
                                }
                            }

                            if (generate100Items) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = itemTemplateType == "catalog",
                                        onClick = { itemTemplateType = "catalog" }
                                    )
                                    Text("Standard Catalog (Industrial Parts)", fontSize = 12.sp)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = itemTemplateType == "custom",
                                        onClick = { itemTemplateType = "custom" }
                                    )
                                    Text("Custom Template", fontSize = 12.sp)
                                }

                                if (itemTemplateType == "custom") {
                                    OutlinedTextField(
                                        value = customPrefixText,
                                        onValueChange = { customPrefixText = it },
                                        label = { Text("Item Prefix (e.g. Carton, Roll, Product)") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (customerNameText.isBlank()) {
                        isError = true
                        return@Button
                    }
                    if (listToEdit != null) {
                        onUpdate(
                            customerNameText,
                            orderNumberText,
                            packingDateText,
                            dispatchDateText,
                            destinationText,
                            statusText,
                            notesText
                        )
                    } else {
                        val prefix = if (itemTemplateType == "custom") customPrefixText else null
                        onCreate(
                            customerNameText,
                            orderNumberText,
                            packingDateText,
                            dispatchDateText,
                            destinationText,
                            notesText,
                            generate100Items,
                            prefix
                        )
                    }
                },
                modifier = Modifier.testTag("save_list_button")
            ) {
                Text(if (listToEdit != null) "Save Changes" else "Create Packing List")
            }
        },
        dismissButton = {
            Row {
                if (listToEdit != null && onDelete != null) {
                    TextButton(
                        onClick = { showDeleteConfirm = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.testTag("delete_list_button")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.width(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete")
                    }
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("cancel_list_button")
                ) {
                    Text("Cancel")
                }
            }
        }
    )
}

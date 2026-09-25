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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.GreenSuccess

@Composable
fun BatchActionsDialog(
    totalItems: Int,
    onDismiss: () -> Unit,
    onPackRange: (from: Int, to: Int, packed: Boolean) -> Unit,
    onPackAll: (Boolean) -> Unit,
    onFill100Catalog: () -> Unit,
    onFill100CustomTemplate: (prefix: String) -> Unit
) {
    var rangeFromText by remember { mutableStateOf("1") }
    var rangeToText by remember { mutableStateOf("25") }
    var customPrefixText by remember { mutableStateOf("Item") }
    var showConfirmResetCatalog by remember { mutableStateOf(false) }

    if (showConfirmResetCatalog) {
        AlertDialog(
            onDismissRequest = { showConfirmResetCatalog = false },
            title = { Text("Restore Standard 100 Items?") },
            text = {
                Text("This will replace current items with a curated 100-item industrial checklist (S.No 1-100). Any custom changes to items will be reset.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmResetCatalog = false
                        onFill100Catalog()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberAccent)
                ) {
                    Text("Yes, Restore 100 Items")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmResetCatalog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FormatListNumbered,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Batch Packing Actions",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section 1: Range Packing
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Pack by Serial Number Range",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = rangeFromText,
                                onValueChange = { rangeFromText = it.filter { c -> c.isDigit() } },
                                label = { Text("From S.No") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("batch_range_from_input")
                            )
                            OutlinedTextField(
                                value = rangeToText,
                                onValueChange = { rangeToText = it.filter { c -> c.isDigit() } },
                                label = { Text("To S.No") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("batch_range_to_input")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val from = rangeFromText.toIntOrNull() ?: 1
                                    val to = rangeToText.toIntOrNull() ?: 25
                                    onPackRange(from, to, true)
                                },
                                modifier = Modifier.weight(1f).testTag("batch_pack_range_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess)
                            ) {
                                Text("Pack Range", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = {
                                    val from = rangeFromText.toIntOrNull() ?: 1
                                    val to = rangeToText.toIntOrNull() ?: 25
                                    onPackRange(from, to, false)
                                },
                                modifier = Modifier.weight(1f).testTag("batch_unpack_range_button")
                            ) {
                                Text("Unpack Range", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Section 2: Global All Pack / Unpack
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onPackAll(true) },
                        modifier = Modifier.weight(1f).testTag("pack_all_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.width(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pack All ($totalItems)", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { onPackAll(false) },
                        modifier = Modifier.weight(1f).testTag("unpack_all_button")
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.width(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset All", fontSize = 12.sp)
                    }
                }

                HorizontalDivider()

                // Section 3: 100 Items Template Management
                Column {
                    Text(
                        text = "100 Items Sheet Options",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = { showConfirmResetCatalog = true },
                        modifier = Modifier.fillMaxWidth().testTag("restore_industrial_catalog_button")
                    ) {
                        Icon(Icons.Default.SettingsBackupRestore, contentDescription = null, modifier = Modifier.width(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Fill 100 Industrial Items Catalog")
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = customPrefixText,
                            onValueChange = { customPrefixText = it },
                            label = { Text("Prefix") },
                            placeholder = { Text("e.g. Carton") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                onFill100CustomTemplate(customPrefixText.ifBlank { "Item" })
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Generate 100", fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

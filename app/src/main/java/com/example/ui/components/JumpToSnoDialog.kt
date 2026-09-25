package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.ui.theme.NavyPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JumpToSnoDialog(
    maxSno: Int = 100,
    onDismiss: () -> Unit,
    onJump: (Int) -> Unit
) {
    var targetSnoText by remember { mutableStateOf("1") }
    var sliderValue by remember { mutableFloatStateOf(1f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.NearMe,
                    contentDescription = null,
                    tint = NavyPrimary
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = "Jump to S.No (1 - $maxSno)",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Quickly scroll straight to any serial number item in this packing list.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = targetSnoText,
                    onValueChange = { input ->
                        val clean = input.filter { it.isDigit() }
                        targetSnoText = clean
                        val num = clean.toIntOrNull()
                        if (num != null) {
                            sliderValue = num.coerceIn(1, maxSno).toFloat()
                        }
                    },
                    label = { Text("Serial Number (S.No)") },
                    placeholder = { Text("e.g. 45") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("jump_sno_input_field")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("1", style = MaterialTheme.typography.labelSmall)
                    Slider(
                        value = sliderValue,
                        onValueChange = { newVal ->
                            sliderValue = newVal
                            targetSnoText = newVal.toInt().toString()
                        },
                        valueRange = 1f..maxSno.toFloat(),
                        steps = (maxSno - 2).coerceAtLeast(0),
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                    )
                    Text("$maxSno", style = MaterialTheme.typography.labelSmall)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Quick Presets:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Quick jump preset chips
                val presets = listOf(1, 10, 25, 50, 75, 90, 100)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presets.filter { it <= maxSno }.forEach { p ->
                        FilterChip(
                            selected = targetSnoText == p.toString(),
                            onClick = {
                                targetSnoText = p.toString()
                                sliderValue = p.toFloat()
                            },
                            label = { Text("#$p", fontSize = 12.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sno = targetSnoText.toIntOrNull()?.coerceIn(1, maxSno) ?: 1
                    onJump(sno)
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_jump_button")
            ) {
                Text("Go to S.No")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_jump_button")
            ) {
                Text("Cancel")
            }
        }
    )
}

package com.iangachoki.plantspotscout.ui.spots

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.iangachoki.plantspotscout.R

/**
 * Asks for a spot name and a room. [onConfirm] is only called with a non-blank name.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSpotDialog(
    rooms: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, room: String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var room by rememberSaveable { mutableStateOf(rooms.first()) }
    var roomMenuOpen by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_spot)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.spot_name)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenuBox(
                    expanded = roomMenuOpen,
                    onExpandedChange = { roomMenuOpen = it }
                ) {
                    OutlinedTextField(
                        value = room,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.room)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roomMenuOpen) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = roomMenuOpen,
                        onDismissRequest = { roomMenuOpen = false }
                    ) {
                        rooms.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    room = option
                                    roomMenuOpen = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name.trim(), room) },
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

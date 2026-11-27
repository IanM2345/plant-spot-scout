package com.iangachoki.plantspotscout.ui.plants

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.model.PlantMatch
import com.iangachoki.plantspotscout.model.PlantType
import com.iangachoki.plantspotscout.model.matchingType

@Composable
fun IdentifyingDialog() {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {},
        text = { Text(stringResource(R.string.identifying)) }
    )
}

/** Up to three Pl@ntNet matches; a row that matches one of [types] opens the spot ranking for it. */
@Composable
fun IdentifyResultsDialog(
    matches: List<PlantMatch>,
    types: List<PlantType>,
    onPickType: (PlantType) -> Unit,
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(stringResource(R.string.identify_plant)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                matches.forEach { match ->
                    val type = matchingType(types, match)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(if (type != null) Modifier.clickable { onPickType(type) } else Modifier),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(match.commonName ?: match.scientificName, style = MaterialTheme.typography.titleMedium)
                            Text(
                                match.scientificName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = FontStyle.Italic
                            )
                            if (type != null) {
                                Text(
                                    stringResource(R.string.in_your_plant_list),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Text(
                            stringResource(R.string.score_percent, (match.score * 100).toInt()),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                Text(
                    stringResource(R.string.identified_by),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = { TextButton(onClick = onClose) { Text(stringResource(R.string.close)) } }
    )
}

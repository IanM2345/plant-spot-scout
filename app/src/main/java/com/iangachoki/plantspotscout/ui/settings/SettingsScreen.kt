package com.iangachoki.plantspotscout.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iangachoki.plantspotscout.R
import com.iangachoki.plantspotscout.ui.SpotsViewModel
import com.iangachoki.plantspotscout.ui.components.ScreenHeader
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(viewModel: SpotsViewModel, modifier: Modifier = Modifier) {
    val demoMode by viewModel.demoMode.collectAsState()
    var showClearDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val versionName = remember(context) {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clearedMessage = stringResource(R.string.data_cleared)

    val itemColors = ListItemDefaults.colors(containerColor = Color.Transparent)

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ScreenHeader(
                title = stringResource(R.string.tab_settings),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            SectionHeader(stringResource(R.string.settings_measuring))
            ListItem(
                colors = itemColors,
                headlineContent = { Text(stringResource(R.string.demo_mode)) },
                supportingContent = { Text(stringResource(R.string.demo_mode_summary)) },
                trailingContent = {
                    Switch(checked = demoMode, onCheckedChange = viewModel::setDemoMode)
                },
                modifier = Modifier.clickable { viewModel.setDemoMode(!demoMode) }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            SectionHeader(stringResource(R.string.settings_data))
            ListItem(
                colors = itemColors,
                headlineContent = {
                    Text(stringResource(R.string.clear_data), color = MaterialTheme.colorScheme.error)
                },
                supportingContent = { Text(stringResource(R.string.clear_data_summary)) },
                modifier = Modifier.clickable { showClearDialog = true }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            SectionHeader(stringResource(R.string.settings_about))
            ListItem(
                colors = itemColors,
                headlineContent = { Text(stringResource(R.string.app_name)) },
                supportingContent = {
                    Column {
                        Text(stringResource(R.string.version, versionName))
                        Text(stringResource(R.string.about_text), modifier = Modifier.padding(top = 4.dp))
                    }
                }
            )
        }
        SnackbarHost(snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(R.string.clear_data_title)) },
            text = { Text(stringResource(R.string.clear_data_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        viewModel.clearAllData()
                        scope.launch { snackbarHostState.showSnackbar(clearedMessage) }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.clear))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 4.dp)
    )
}

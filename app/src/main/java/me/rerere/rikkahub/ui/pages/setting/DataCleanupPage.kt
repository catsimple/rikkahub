package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.Alert01
import me.rerere.hugeicons.stroke.Database02
import me.rerere.hugeicons.stroke.Delete01
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.files.FilesManager
import me.rerere.rikkahub.data.files.StorageInfo
import me.rerere.rikkahub.ui.components.nav.BackButton
import me.rerere.rikkahub.ui.components.ui.CardGroup
import me.rerere.rikkahub.ui.context.LocalToaster
import me.rerere.rikkahub.ui.theme.CustomColors
import me.rerere.rikkahub.utils.plus
import org.koin.compose.koinInject

@Composable
fun DataCleanupPage(
    filesManager: FilesManager = koinInject(),
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scope = rememberCoroutineScope()
    val toaster = LocalToaster.current

    val storageInfo by filesManager.observeStorageInfo().collectAsStateWithLifecycle(
        initialValue = StorageInfo(0, 0L, 0L)
    )

    var orphanCount by remember { mutableStateOf<Int?>(null) }
    var orphanSize by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        val (count, size) = filesManager.countOrphanFiles()
        orphanCount = count
        orphanSize = size
    }

    var daysValue by remember { mutableFloatStateOf(30f) }
    var showDaysConfirmDialog by remember { mutableStateOf(false) }
    var pendingCleanupCount by remember { mutableStateOf(0) }
    var pendingCleanupSize by remember { mutableStateOf(0L) }

    val dataCleanupTitle = stringResource(R.string.data_cleanup_title)
    val storageTitle = stringResource(R.string.data_cleanup_storage_title)
    val totalFiles = stringResource(R.string.data_cleanup_total_files)
    val dbSize = stringResource(R.string.data_cleanup_db_size)
    val orphanTitle = stringResource(R.string.data_cleanup_orphan_title)
    val orphanFiles = stringResource(R.string.data_cleanup_orphan_files)
    val calculating = stringResource(R.string.calculating)
    val orphanAction = stringResource(R.string.data_cleanup_orphan_action)
    val orphanNone = stringResource(R.string.data_cleanup_orphan_none)
    val daysTitle = stringResource(R.string.data_cleanup_days_title)
    val daysAction = stringResource(R.string.data_cleanup_days_action)
    val daysNone = stringResource(R.string.data_cleanup_days_none)
    val cancel = stringResource(R.string.cancel)
    val confirm = stringResource(R.string.data_cleanup_confirm)
    val daysDialogTitle = stringResource(R.string.data_cleanup_days_dialog_title)
    val daysResultTemplate = stringResource(R.string.data_cleanup_days_result)
    val orphanResultTemplate = stringResource(R.string.data_cleanup_orphan_result)
    val totalFilesDesc = stringResource(
        R.string.data_cleanup_total_files_desc,
        storageInfo.fileCount,
        formatBytes(storageInfo.totalSize)
    )
    val orphanNoneMsg = stringResource(R.string.data_cleanup_orphan_none)

    if (showDaysConfirmDialog) {
        val dialogMsg = stringResource(
            R.string.data_cleanup_days_dialog_message,
            daysValue.toInt(),
            formatBytes(pendingCleanupSize),
            pendingCleanupCount
        )
        AlertDialog(
            onDismissRequest = { showDaysConfirmDialog = false },
            title = { Text(daysDialogTitle) },
            text = { Text(dialogMsg) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDaysConfirmDialog = false
                        scope.launch {
                            val deleted = filesManager.deleteFilesOlderThan(daysValue.toInt())
                            toaster.show(String.format(daysResultTemplate, deleted))
                        }
                    }
                ) {
                    Text(confirm)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDaysConfirmDialog = false }) {
                    Text(cancel)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(dataCleanupTitle) },
                navigationIcon = { BackButton() },
                scrollBehavior = scrollBehavior,
                colors = CustomColors.topBarColors,
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = CustomColors.topBarColors.containerColor,
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding + PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    title = { Text(storageTitle) },
                ) {
                    item(
                        leadingContent = { Icon(HugeIcons.Database02, null) },
                        headlineContent = { Text(totalFiles) },
                        supportingContent = { Text(totalFilesDesc) },
                    )
                    item(
                        leadingContent = { Icon(HugeIcons.Database02, null) },
                        headlineContent = { Text(dbSize) },
                        supportingContent = { Text(formatBytes(storageInfo.dbSize)) },
                    )
                }
            }

            item {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    title = { Text(orphanTitle) },
                ) {
                    item(
                        leadingContent = { Icon(HugeIcons.Delete01, null) },
                        headlineContent = { Text(orphanFiles) },
                        supportingContent = {
                            if (orphanCount == null) {
                                Text(calculating)
                            } else {
                                Text(
                                    stringResource(
                                        R.string.data_cleanup_orphan_desc,
                                        orphanCount ?: 0,
                                        formatBytes(orphanSize ?: 0L)
                                    )
                                )
                            }
                        },
                        trailingContent = {
                            Button(
                                onClick = {
                                    scope.launch {
                                        val (count, size) = filesManager.countOrphanFiles()
                                        if (count > 0) {
                                            val deleted = filesManager.deleteOrphanFiles()
                                            toaster.show(
                                                String.format(orphanResultTemplate, deleted)
                                            )
                                            orphanCount = 0
                                            orphanSize = 0L
                                        } else {
                                            toaster.show(orphanNoneMsg)
                                        }
                                    }
                                },
                                enabled = orphanCount != null && (orphanCount ?: 0) > 0
                            ) {
                                Text(orphanAction)
                            }
                        }
                    )
                }
            }

            item {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    title = { Text(daysTitle) },
                ) {
                    item(
                        leadingContent = { Icon(HugeIcons.Alert01, null) },
                        headlineContent = {
                            Text(stringResource(R.string.data_cleanup_days_label, daysValue.toInt()))
                        },
                        supportingContent = {
                            Column {
                                Slider(
                                    value = daysValue,
                                    onValueChange = { daysValue = it },
                                    valueRange = 1f..90f,
                                    steps = 88,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("1", style = MaterialTheme.typography.labelSmall)
                                    Text("90", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        },
                        trailingContent = {
                            Button(
                                onClick = {
                                    scope.launch {
                                        val (count, size) = filesManager.countFilesOlderThan(daysValue.toInt())
                                        if (count > 0) {
                                            pendingCleanupCount = count
                                            pendingCleanupSize = size
                                            showDaysConfirmDialog = true
                                        } else {
                                            toaster.show(daysNone)
                                        }
                                    }
                                }
                            ) {
                                Text(daysAction)
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes < 1024) return "${bytes}B"
    val kb = bytes / 1024.0
    if (kb < 1024) return String.format("%.1f KB", kb)
    val mb = kb / 1024.0
    if (mb < 1024) return String.format("%.1f MB", mb)
    val gb = mb / 1024.0
    return String.format("%.1f GB", gb)
}

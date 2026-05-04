package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.components.nav.BackButton
import me.rerere.rikkahub.ui.components.ui.CardGroup
import me.rerere.rikkahub.ui.theme.CustomColors
import me.rerere.rikkahub.utils.plus
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingAdvancedPage(vm: SettingVM = koinViewModel()) {
    val settings by vm.settings.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(R.string.setting_page_advanced_settings)) },
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
                    title = { Text(stringResource(R.string.setting_page_advanced_settings)) },
                ) {
                    item(
                        headlineContent = { Text(stringResource(R.string.setting_page_heic_to_jpg)) },
                        supportingContent = { Text(stringResource(R.string.setting_page_heic_to_jpg_desc)) },
                        trailingContent = {
                            Switch(
                                checked = settings.heicToJpg,
                                onCheckedChange = { vm.updateSettings(settings.copy(heicToJpg = it)) },
                            )
                        }
                    )
                    item(
                        headlineContent = { Text(stringResource(R.string.setting_page_image_compress)) },
                        supportingContent = { Text(stringResource(R.string.setting_page_image_compress_desc)) },
                        trailingContent = {
                            Switch(
                                checked = settings.imageCompressEnabled,
                                onCheckedChange = {
                                    vm.updateSettings(settings.copy(imageCompressEnabled = it))
                                },
                            )
                        }
                    )
                    if (settings.imageCompressEnabled) {
                        item(
                            headlineContent = { Text(stringResource(R.string.setting_page_image_compress_quality)) },
                            supportingContent = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Slider(
                                        value = settings.imageCompressQuality.toFloat(),
                                        onValueChange = {
                                            vm.updateSettings(settings.copy(imageCompressQuality = it.toInt()))
                                        },
                                        valueRange = 10f..100f,
                                        steps = 8,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = stringResource(R.string.setting_page_image_compress_quality_value, settings.imageCompressQuality),
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            }
                        )
                    }
                    item(
                        headlineContent = { Text(stringResource(R.string.setting_page_ocr_compress)) },
                        supportingContent = { Text(stringResource(R.string.setting_page_ocr_compress_desc)) },
                        trailingContent = {
                            Switch(
                                checked = settings.ocrCompressEnabled,
                                onCheckedChange = {
                                    vm.updateSettings(settings.copy(ocrCompressEnabled = it))
                                },
                            )
                        }
                    )
                    if (settings.ocrCompressEnabled) {
                        item(
                            headlineContent = { Text(stringResource(R.string.setting_page_ocr_compress_quality)) },
                            supportingContent = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Slider(
                                        value = settings.ocrCompressQuality.toFloat(),
                                        onValueChange = {
                                            vm.updateSettings(settings.copy(ocrCompressQuality = it.toInt()))
                                        },
                                        valueRange = 10f..100f,
                                        steps = 8,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = stringResource(R.string.setting_page_image_compress_quality_value, settings.ocrCompressQuality),
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            }
                        )
                    }
                    item(
                        headlineContent = { Text(stringResource(R.string.setting_page_search_highlight)) },
                        supportingContent = { Text(stringResource(R.string.setting_page_search_highlight_desc)) },
                        trailingContent = {
                            Switch(
                                checked = settings.searchHighlightEnabled,
                                onCheckedChange = {
                                    vm.updateSettings(settings.copy(searchHighlightEnabled = it))
                                },
                            )
                        }
                    )
                }
            }
        }
    }
}

package com.orbitwall.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.orbitwall.model.Resolution
import com.orbitwall.model.WallSettings
import kotlin.math.roundToInt

@Composable
fun ControlsPanel(
    settings: WallSettings,
    onSettingsChange: (WallSettings) -> Unit,
    isBusy: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingSlider(
            title = "Zoom Offset",
            value = settings.zoomOffset.toFloat(),
            range = -3f..3f,
            steps = 5,
            isBusy = isBusy
        ) { newValue ->
            onSettingsChange(settings.copy(zoomOffset = newValue.roundToInt()))
        }
        SettingSlider(
            title = "Blur",
            value = settings.blur,
            range = 0f..20f,
            steps = 5,
            isBusy = isBusy
        ) { newValue ->
            onSettingsChange(settings.copy(blur = newValue))
        }
        SettingSlider(
            title = "Brightness",
            value = settings.brightness,
            range = 0.5f..1.5f,
            steps = 5,
            isBusy = isBusy
        ) { newValue ->
            onSettingsChange(settings.copy(brightness = newValue))
        }
        SettingSlider(
            title = "Overlay",
            value = settings.overlayOpacity,
            range = 0f..0.8f,
            steps = 4,
            isBusy = isBusy
        ) { newValue ->
            onSettingsChange(settings.copy(overlayOpacity = newValue))
        }
        ResolutionSelector(
            selected = settings.resolution,
            onResolutionSelected = { onSettingsChange(settings.copy(resolution = it)) }
        )
    }
}

@Composable
private fun SettingSlider(
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    isBusy: Boolean,
    onValueChange: (Float) -> Unit
) {
    Column {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        Slider(
            value = value,
            valueRange = range,
            steps = steps,
            onValueChange = { if (!isBusy) onValueChange(it) }
        )
    }
}

@Composable
private fun ResolutionSelector(
    selected: Resolution,
    onResolutionSelected: (Resolution) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Resolution.values().forEach { resolution ->
            AssistChip(
                onClick = { onResolutionSelected(resolution) },
                label = { Text(text = stringResource(id = resolution.label)) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (resolution == selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

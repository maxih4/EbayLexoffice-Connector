package components


import androidx.compose.desktop.ui.tooling.preview.Preview

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.DurationUnit
import kotlin.time.toDuration


@Composable
@Preview
@ExperimentalMaterial3Api
fun datePickerRange(onDismissRequest: () -> Unit, state: DateRangePickerState) {

    DatePickerDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = { }) {
                Text("Ok")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text("Cancel")
            }
        }
    ) {
        //Todo 1 Stunde abziehen, da sonst datum failt
        DateRangePicker(state = state, showModeToggle = false, dateValidator = { timestamp ->
            (timestamp <= Clock.System.now().toEpochMilliseconds()) && (timestamp >= Clock.System.now().minus(
                730L.toDuration(
                    DurationUnit.DAYS
                )
            ).toEpochMilliseconds())
        })
    }
}


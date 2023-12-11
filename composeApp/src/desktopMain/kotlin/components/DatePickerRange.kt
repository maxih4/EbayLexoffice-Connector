package components


import androidx.compose.desktop.ui.tooling.preview.Preview


import androidx.compose.material3.*


import androidx.compose.runtime.Composable

import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
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


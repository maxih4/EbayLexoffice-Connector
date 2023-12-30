package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
@Preview
fun DividerImpl() {
    Divider(startIndent = 8.dp, thickness = 1.dp, color = Color.LightGray)


}
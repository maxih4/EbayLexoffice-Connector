package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.multiplatform.progressindicator.SimpleProgressIndicatorWithAnim

@Composable
@Preview
fun OrdersAreLoadingDialog(onDismissRequest: () -> Unit,progress: Float) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column (modifier = Modifier.fillMaxSize().padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween){
                Text(
                    text = "Creating Invoices from Ebay orders",
                    textAlign = TextAlign.Center,
                )
                SimpleProgressIndicatorWithAnim(
                    modifier = Modifier

                        .fillMaxWidth()
                        .height(10.dp),
                    progressBarColor = Color.Green,
                    cornerRadius = 35.dp,
                    thumbRadius = 1.dp,
                    thumbOffset = 1.5.dp,
                    progress = progress
                )

            }
        }
    }
}
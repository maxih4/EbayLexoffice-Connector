package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
@Preview
fun OrderTableHeader() {
    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Spacer(Modifier.size(24.dp))
            Text(
                modifier = Modifier.align(Alignment.CenterVertically).weight(0.5f), text = "OrderId", fontSize = 20.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.size(24.dp))
            Divider(
                color = Color.Black, modifier = Modifier.fillMaxHeight()  //fill the max height
                    .width(2.dp)
            )
            Text(
                text = "Order Items",
                modifier = Modifier.align(Alignment.CenterVertically).weight(2f),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
            )
            Divider(
                color = Color.Black, modifier = Modifier.fillMaxHeight()  //fill the max height
                    .width(2.dp)
            )
            Text(
                text = "Order Date",
                modifier = Modifier.align(Alignment.CenterVertically).weight(0.5f),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
            )
        }
        Divider(
            modifier = Modifier, thickness = 2.dp, color = Color.Black
        )

    }
}
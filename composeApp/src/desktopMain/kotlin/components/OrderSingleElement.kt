package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*


import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.ebay.Orders
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}


@Composable
@Preview
fun OrderCompose(order: Orders, checkedOrders: List<Orders>, onCheckedChange: (Orders) -> Unit) {
    var items = ""
    val checkedState = rememberSaveable { mutableStateOf(false) }
    checkedState.value = checkedOrders.contains(order)
    val creationDate = ZonedDateTime.parse(
        order.creationDate,
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
    )

    order.lineItems.forEach {
        items += it.title.toString()
    }

    Column(modifier = Modifier.fillMaxWidth()) {

        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).conditional(checkedState.value)
        {
            background(Color.LightGray)
        },
            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Checkbox(checked = checkedState.value, onCheckedChange = {
                checkedState.value = it
                onCheckedChange(order)
            },
                modifier = Modifier.align(Alignment.CenterVertically))
            Text(
                modifier = Modifier.align(Alignment.CenterVertically).weight(0.5f),
                text = order.orderId.orEmpty(),

            )
            Divider(
                color = Color.Black,
                modifier = Modifier
                    .fillMaxHeight()  //fill the max height
                    .width(2.dp)
            )
            Text(
                text = items,
                modifier = Modifier.align(Alignment.CenterVertically).weight(2f)
            )
            Divider(
                color = Color.Black,
                modifier = Modifier
                    .fillMaxHeight()  //fill the max height
                    .width(2.dp)
            )
            Text(
                text = creationDate.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)),
                modifier = Modifier.align(Alignment.CenterVertically).weight(0.5f)
            )
        }
        Divider(
            modifier = Modifier, thickness = 1.dp, color = Color.Gray
        )

    }
}
package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import model.ebay.Orders


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


    order.lineItems.forEach {
        items += it.title.toString()
    }
    Column(modifier = Modifier.fillMaxWidth()) {

        Row(modifier = Modifier.fillMaxWidth()

            .conditional(checkedState.value)
            {
                background(Color.LightGray)
            }) {
            Checkbox(checked = checkedState.value, onCheckedChange = {
                checkedState.value = it
                onCheckedChange(order)
            })
            Text(modifier = Modifier.align(Alignment.CenterVertically),
                text = order.orderId.orEmpty(),
                color = Color.Green
            )

            Text(text = items,
                modifier = Modifier.align(Alignment.CenterVertically))
        }


    }
}
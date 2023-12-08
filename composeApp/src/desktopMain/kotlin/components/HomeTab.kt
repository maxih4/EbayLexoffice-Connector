package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.material.*
import androidx.compose.material.Button
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.ktor.security.EbayAuthController
import controller.LexofficeController
import io.ktor.client.statement.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import model.ebay.OrderResponse
import model.ebay.Orders
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


import storage.kvstore
import java.time.LocalDateTime
import java.util.*


object HomeTab : KoinComponent, Tab {
    private val store: kvstore by inject()
    private val settings = store.settings
    private val lexofficeController: LexofficeController by inject()


    private fun readResolve(): Any = HomeTab


    override val options: TabOptions
        @Composable
        get() {

            val icon = rememberVectorPainter(Icons.Default.Home)

            return remember {

                TabOptions(
                    index = 0u,
                    title = "Home",
                    icon = icon

                )
            }

        }



    @Composable
    @Preview
    override fun Content() {
        val orders = rememberSaveable { mutableStateListOf<Orders>() }
        val checkedOrders = rememberSaveable { mutableStateListOf<Orders>() }
        val coroutineScope = rememberCoroutineScope()
        val ebayAuthController = EbayAuthController()

        fun makeInvoiceForOrders(){


            coroutineScope.launch{

               val res = lexofficeController.getContactOrCreateNew("Handke","Maximilian","lisa.kettner99@gmail.com","Berlin",
                   "DE","Straße 5","Extra Text","12345")

                //Res ist neu erstellter Kontakt oder der gefundene

                val output = lexofficeController.createInvoiceFromOrder(checkedOrders.first(),res)
                println("Outcome: " + output)
            }

        }
        fun getOrders() {
            coroutineScope.launch {
                val now = LocalDateTime.now()

                if (now > LocalDateTime.parse(
                        settings.getString(
                            "refresh_token_expires_at",
                            now.minusDays(1L).toString()
                        )
                    )
                ) {
                    ebayAuthController.openBrowser()
                    ebayAuthController.openServer().access_token
                }

                val json = Json { ignoreUnknownKeys = true }
                orders.clear()
                checkedOrders.clear()

                orders.addAll(
                    json.decodeFromString<OrderResponse>(
                        ebayAuthController.getResponse("https://api.ebay.com/sell/fulfillment/v1/order")
                            .bodyAsText()
                    ).orders
                )


            }
        }

        val scrollstate = rememberScrollState()

        BoxWithConstraints {


            Column(
                modifier = Modifier
                    .verticalScroll(scrollstate).fillMaxWidth()

            ) {
                ExtendedFloatingActionButton(
                    onClick = { getOrders() },
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(5.dp),
                    text = { Text("GetOrders") },
                    icon = { Icon(Icons.Filled.AddCircle, "") },
                    backgroundColor = Color.LightGray
                )

                if (orders.isNotEmpty()) {
                    orders.forEach {
                        OrderCompose(it, checkedOrders, onCheckedChange = {
                            if(checkedOrders.contains(it)){
                                checkedOrders.remove(it)
                            }else {
                                checkedOrders.add(it)
                            }
                        })

                    }

                    Button(onClick = { makeInvoiceForOrders();println("Checked Orders: " + checkedOrders.toList().toString()) }) {
                        Text(text = "Check Checked Orders")
                    }
                }

            }


            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollstate),
                style = LocalScrollbarStyle.current.copy(unhoverColor = Color.Black, shape = RectangleShape),
                modifier = Modifier.align(
                    Alignment.CenterEnd
                )
            )
        }

    }


}
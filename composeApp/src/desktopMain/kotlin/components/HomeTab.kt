package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn


import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.ktor.security.EbayController
import controller.LexofficeController
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import kotlinx.serialization.json.Json
import model.ebay.OrderResponse
import model.ebay.Orders
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


import storage.kvstore
import java.text.SimpleDateFormat
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


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    @Preview
    override fun Content() {
        val orders = rememberSaveable { mutableStateListOf<Orders>() }
        val checkedOrders = rememberSaveable { mutableStateListOf<Orders>() }
        val coroutineScope = rememberCoroutineScope()
        val ebayAuthController = EbayController()
        val checkedAllState = rememberSaveable { mutableStateOf(false) }
        val checkAllEnabled = rememberSaveable { mutableStateOf(false) }
        val isLoading = rememberSaveable { mutableStateOf(false) }
        val loadingProgress = rememberSaveable { mutableStateOf(0F) }
        val openDateRangePicker = rememberSaveable { mutableStateOf(false) }
        val dateRangePickerState = rememberDateRangePickerState()
        val startDate = dateRangePickerState.selectedStartDateMillis?.let {
            SimpleDateFormat("dd.MM.yyyy").format(
                Date.from(
                    Instant.fromEpochMilliseconds(it).toJavaInstant()
                )
            )
        }

        val endDate = dateRangePickerState.selectedEndDateMillis?.let {
            SimpleDateFormat("dd.MM.yyyy").format(
                Date.from(
                    Instant.fromEpochMilliseconds(it).toJavaInstant()
                )
            )
        }
        val startAndEndDateText = rememberSaveable { mutableStateOf("") }

        startAndEndDateText.value = if (!startDate.isNullOrEmpty() && !endDate.isNullOrEmpty()) {
            "From:  $startDate\nTo:  $endDate"
        } else {
            "From: start date\nTo: end date"
        }


        fun makeInvoiceForOrders() {
            //Todo Fehler wenn checked Orders leer
            isLoading.value = true
            loadingProgress.value = 0F
            val progressSize: Float = (1F / checkedOrders.size)
            checkedOrders.forEach {
                var lastName = ""
                var firstName = ""
                if (it.buyer?.buyerRegistrationAddress?.fullName?.contains(" ") == true) {
                    lastName = it.buyer?.buyerRegistrationAddress?.fullName?.split(" ")!![1]
                    firstName = it.buyer?.buyerRegistrationAddress?.fullName?.split(" ")!![0]
                } else {
                    lastName = it.buyer?.username.toString()

                }
                coroutineScope.launch {
                    val contactId = lexofficeController.getContactOrCreateNew(
                        lastName = lastName,
                        firstName = firstName,
                        email = it.buyer?.buyerRegistrationAddress?.email.orEmpty(),
                        city = it.buyer?.buyerRegistrationAddress?.contactAddress?.city!!,
                        countryCode = it.buyer?.buyerRegistrationAddress?.contactAddress?.countryCode!!,
                        street = it.buyer?.buyerRegistrationAddress?.contactAddress?.addressLine1.orEmpty(),
                        supplement = "",
                        zip = it.buyer?.buyerRegistrationAddress?.contactAddress?.postalCode!!
                    )
                    val output = lexofficeController.createInvoiceFromOrder(it, contactId)

                    println("Outcome: $output")

                    loadingProgress.value += progressSize

                    if (loadingProgress.value >= 1) {
                        isLoading.value = false
                    }
                }

            }


        }

        fun getOrders(startDate: String?, endDate: String?) {
            checkAllEnabled.value = false
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

                val url = "https://api.ebay.com/sell/fulfillment/v1/order"


                orders.addAll(

                    ebayAuthController.getResponse(url, startDate, endDate).orders
                )

                checkAllEnabled.value = true
            }


        }

        val scrollstate = rememberScrollState()

        BoxWithConstraints {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollstate).fillMaxWidth()

            ) {
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Checkbox(
                            checked = checkedAllState.value,
                            modifier = Modifier.align(Alignment.CenterVertically),
                            onCheckedChange = {
                                checkAllOrder(checkedAllState, it, checkedOrders, orders)

                            },
                            enabled = checkAllEnabled.value
                        )
                        Text(
                            "Check all Orders",
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                    ExtendedFloatingActionButton(
                        onClick = { openDateRangePicker.value = true },
                        modifier = Modifier.padding(5.dp),
                        text = { Text("Date Filter") },
                        icon = { Icon(Icons.Filled.DateRange, "") },
                        backgroundColor = Color.LightGray
                    )
                    if (openDateRangePicker.value) {
                        datePickerRange(
                            onDismissRequest = { openDateRangePicker.value = false },
                            state = dateRangePickerState
                        )
                    }


                    Text(
                        modifier = Modifier.width(120.dp).align(Alignment.CenterVertically),
                        text = startAndEndDateText.value,
                        fontWeight = FontWeight.Bold
                    )
                    ExtendedFloatingActionButton(
                        onClick = {
                            startAndEndDateText.value =
                                "From: start date\nTo: end date";dateRangePickerState.setSelection(null, null)
                        },
                        modifier = Modifier.padding(5.dp),
                        text = { Text("Reset Date") },
                        icon = { Icon(Icons.Filled.Delete, "") },
                        backgroundColor = Color.LightGray
                    )
                    ExtendedFloatingActionButton(
                        onClick = { getOrders(startDate, endDate) },
                        modifier = Modifier.padding(5.dp),
                        text = { Text("GetOrders") },
                        icon = { Icon(Icons.Filled.AddCircle, "") },
                        backgroundColor = Color.LightGray
                    )


                }


                    if (orders.isNotEmpty()) {

                        OrderTableHeader()

                        orders.forEach {
                            OrderCompose(it, checkedOrders, onCheckedChange = {
                                if (checkedOrders.contains(it)) {
                                    checkedOrders.remove(it)
                                } else {
                                    checkedOrders.add(it)
                                }
                            })


                        }

                        ExtendedFloatingActionButton(
                            onClick = {
                                makeInvoiceForOrders();println(
                                "Checked Orders: " + checkedOrders.map { o -> o.orderId.toString() }.toString()
                            )
                            },
                            modifier = Modifier.padding(5.dp).align(Alignment.CenterHorizontally),
                            text = { Text("Create Invoice") },
                            icon = { Icon(Icons.Filled.TaskAlt, "") },
                            backgroundColor = Color.LightGray
                        )
                    }


            }
            if (isLoading.value) {
                OrdersAreLoadingDialog(onDismissRequest = {}, progress = loadingProgress.value)
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

    private fun checkAllOrder(
        checkedAllState: MutableState<Boolean>,
        it: Boolean,
        checkedOrders: SnapshotStateList<Orders>,
        orders: SnapshotStateList<Orders>
    ) {
        checkedAllState.value = it;
        //Check all other Orders, and add all to the checked Orders field
        if ((checkedOrders.size != orders.size) && it) {
            checkedOrders.addAll(orders)
            val distinct = checkedOrders.distinct()
            checkedOrders.clear()
            checkedOrders.addAll(distinct)
        } else {
            checkedOrders.clear()
        }
    }


}
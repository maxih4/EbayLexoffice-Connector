package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*


import androidx.compose.material.*
import androidx.compose.material.Button
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.DateRangePicker
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.ktor.security.EbayAuthController
import controller.LexofficeController
import epicarchitect.calendar.compose.basis.config.rememberBasisEpicCalendarConfig
import epicarchitect.calendar.compose.datepicker.EpicDatePicker
import epicarchitect.calendar.compose.datepicker.config.rememberEpicDatePickerConfig
import epicarchitect.calendar.compose.datepicker.state.EpicDatePickerState
import epicarchitect.calendar.compose.datepicker.state.rememberEpicDatePickerState
import epicarchitect.calendar.compose.pager.config.rememberEpicCalendarPagerConfig
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import kotlinx.serialization.json.Json
import model.ebay.OrderResponse
import model.ebay.Orders
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


import storage.kvstore
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
        val ebayAuthController = EbayAuthController()
        val checkedAllState = rememberSaveable { mutableStateOf(false) }
        val checkAllEnabled = rememberSaveable { mutableStateOf(false) }
        val isLoading = rememberSaveable { mutableStateOf(false) }
        val loadingProgress = rememberSaveable { mutableStateOf(0F) }
        val openDateRangePicker = rememberSaveable { mutableStateOf(false) }
        val dateRangePickerState = rememberDateRangePickerState()
        val startDate = dateRangePickerState.selectedStartDateMillis?.let {SimpleDateFormat("dd.MM.yyyy").format(Date.from(Instant.fromEpochMilliseconds(it).toJavaInstant()))}

        val endDate = dateRangePickerState.selectedEndDateMillis?.let { SimpleDateFormat("dd.MM.yyyy").format(Date.from(Instant.fromEpochMilliseconds(it).toJavaInstant())) }
        val startAndEndDateText = rememberSaveable { mutableStateOf("") }

        startAndEndDateText.value= if(!startDate.isNullOrEmpty() && !endDate.isNullOrEmpty()){
            startDate + "\n" + endDate
        }else{
            "From: start date\nTo: end date"
        }


        fun makeInvoiceForOrders() {
            //Todo Fehler wenn checked Orders leer
            isLoading.value = true
            loadingProgress.value = 0F
            val progressSize: Float = (1F / checkedOrders.size)
            checkedOrders.forEach {


                coroutineScope.launch {
                    val contactId = lexofficeController.getContactOrCreateNew(
                        lastName = it.buyer?.buyerRegistrationAddress?.fullName?.split(" ")!![1],
                        firstName = it.buyer?.buyerRegistrationAddress?.fullName?.split(" ")!![0],
                        email = it.buyer?.buyerRegistrationAddress?.email.orEmpty(),
                        city = it.buyer?.buyerRegistrationAddress?.contactAddress?.city!!,
                        countryCode = it.buyer?.buyerRegistrationAddress?.contactAddress?.countryCode!!,
                        street = it.buyer?.buyerRegistrationAddress?.contactAddress?.addressLine1!!,
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

        fun getOrders() {
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

                orders.addAll(
                    json.decodeFromString<OrderResponse>(
                        ebayAuthController.getResponse("https://api.ebay.com/sell/fulfillment/v1/order")
                            .bodyAsText()
                    ).orders
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
                    Button(onClick = {openDateRangePicker.value=true}){
                        Text(text = "OpenDateRangePicker")
                    }
                    if(openDateRangePicker.value){
                        datePickerRange(onDismissRequest = {openDateRangePicker.value=false}, state = dateRangePickerState)
                    }


                    Text(modifier = Modifier.width(120.dp).align(Alignment.CenterVertically),
                        text=startAndEndDateText.value,
                        fontWeight = FontWeight.Bold
                    )

                    ExtendedFloatingActionButton(
                        onClick = { getOrders() },
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

                    Button(onClick = {
                        makeInvoiceForOrders();println(
                        "Checked Orders: " + checkedOrders.map { o -> o.orderId.toString() }.toString()
                    )
                    }) {
                        Text(text = "Create Invoice from Orders")
                    }
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
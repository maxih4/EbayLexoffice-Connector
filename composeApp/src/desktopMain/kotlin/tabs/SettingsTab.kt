package tabs


import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import components.DividerImpl
import components.LabelImpl
import controller.MailController
import controller.StorageController
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.Desktop
import java.net.URI
import kotlin.io.path.Path

object SettingsTab : KoinComponent, Tab {
    private val store: StorageController by inject()
    private val settings = store.settings
    private val mailController: MailController by inject()


    private fun readResolve(): Any = SettingsTab

    override val options: TabOptions
        @Composable
        get() {

            val icon = rememberVectorPainter(Icons.Default.Settings)

            return remember {
                TabOptions(
                    index = 2u,
                    title = "Settings",
                    icon = icon
                )
            }

        }

    private fun openBrowserApiKey() {
        val desktop = Desktop.getDesktop()
        desktop.browse(
            URI.create(
                "https://app.lexoffice.de/addons/public-api"
            )
        )
    }


    @Composable
    @Preview
    override fun Content() {
        val coroutineScope = rememberCoroutineScope()
        var apiKey by mutableStateOf(settings.getString("apiKey", ""))
        var apiKeyVisible by rememberSaveable { mutableStateOf(false) }
        var passwordSMTP by mutableStateOf(settings.getString("passwordSMTP", ""))
        var passwordSMTPVisible by rememberSaveable { mutableStateOf(false) }
        var usernameSMTP by mutableStateOf(settings.getString("usernameSMTP", ""))
        var host by mutableStateOf(settings.getString("smtpHost", ""))
        var port by mutableStateOf(settings.getString("port", ""))
        var invoiceHeader by mutableStateOf(settings.getString("invoiceHeader", ""))
        var unitName by mutableStateOf(settings.getString("unitName", "Stück"))
        var mailSubject by mutableStateOf(settings.getString("mailSubject","Rechnung zur Bestellung"))
        var fileNameInvoice by mutableStateOf(settings.getString("fileNameInvoice","Rechnung"))
        var shippingCostName by mutableStateOf(settings.getString("shippingCostName", "Versandkosten"))
        var invoiceTitle by mutableStateOf(settings.getString("invoiceTitle", "Rechnung"))
        var invoiceFooter by mutableStateOf(settings.getString("invoiceFooter", ""))
        var paymentTermLabel by mutableStateOf(
            settings.getString(
                "paymentTermLabel",
                "Sobald der Gesamtbetrag bei uns eingegangen ist, wird die Ware von uns verschickt."
            )
        )
        var paymentTermDuration by mutableStateOf(settings.getString("paymentTermDuration", "14"))
        var isErrorPaymentTermDurationNotInt by rememberSaveable { mutableStateOf(false) }
        var shippingDateDays by mutableStateOf(settings.getString("shippingDateDays", "5"))
        var isErrorShippingDateDaysNotInt by rememberSaveable { mutableStateOf(false) }
        var reset by mutableStateOf(false)
        var finalizeInvoiceState by mutableStateOf(settings.getBoolean("finalizeInvoiceState", false))

        val scrollstate = rememberScrollState()
        val localFocusManager = LocalFocusManager.current
        BoxWithConstraints {
            Column(modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollstate)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        localFocusManager.clearFocus()
                    })
                }) {
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(start = 10.dp, top = 10.dp, end = 10.dp)
                        .fillMaxWidth()
                ) {
                    LabelImpl("Lexoffice Api Settings")
                }
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(10.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {


                    OutlinedTextField(

                        value = apiKey,
                        onValueChange = { apiKey = it;settings.putString("apiKey", it) },
                        label = { Text("Api Key") },
                        visualTransformation = if (apiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image = if (apiKeyVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff


                            val description = if (apiKeyVisible) "Hide Api Key" else "Show Api Key"

                            IconButton(onClick = { apiKeyVisible = !apiKeyVisible }) {
                                Icon(imageVector = image, description)
                            }
                        },
                        modifier = Modifier.weight(0.8f).padding(10.dp)
                    )
                    Box(modifier = Modifier.align(Alignment.CenterVertically).weight(0.8f)) {
                        ExtendedFloatingActionButton(
                            onClick = { openBrowserApiKey() },
                            modifier = Modifier.padding(start = 10.dp).align(Alignment.Center),

                            text = { Text("Get Api Key") },
                            icon = { Icon(Icons.Filled.AddCircle, "") },
                            backgroundColor = Color.LightGray,

                            )
                    }


                }
                DividerImpl()
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(start = 10.dp, top = 10.dp, end = 10.dp)
                        .fillMaxWidth()
                ) {
                    LabelImpl("Settings for Sending Mails")
                }
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(10.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    OutlinedTextField(
                        value = host,
                        onValueChange = { host = it;settings.putString("smtpHost", it) },
                        label = { Text("SMTP Host") },
                        modifier = Modifier.weight(0.8f).padding(10.dp)
                    )
                    OutlinedTextField(
                        value = port,
                        onValueChange = { port = it;settings.putString("port", it) },
                        label = { Text("SMTP Port") },
                        modifier = Modifier.weight(0.8f).padding(10.dp)
                    )
                }
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(10.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    OutlinedTextField(
                        value = mailSubject,
                        onValueChange = { mailSubject = it;settings.putString("mailSubject", it) },
                        label = { Text("Mail subject + orderId") },
                        modifier = Modifier.weight(0.8f).padding(10.dp)
                    )
                    OutlinedTextField(
                        value = fileNameInvoice,
                        onValueChange = { fileNameInvoice = it;settings.putString("fileNameInvoice", it) },
                        label = { Text("Invoice attachment name + orderId") },
                        modifier = Modifier.weight(0.8f).padding(10.dp)
                    )
                }
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(10.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedTextField(
                        value = usernameSMTP,
                        onValueChange = { usernameSMTP = it;settings.putString("usernameSMTP", it) },
                        label = { Text("SMTP Username") },
                        modifier = Modifier.weight(0.8f).padding(10.dp)
                    )
                    OutlinedTextField(
                        value = passwordSMTP,
                        onValueChange = { passwordSMTP = it;settings.putString("passwordSMTP", it) },
                        label = { Text("SMTP Password") },
                        visualTransformation = if (passwordSMTPVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image = if (passwordSMTPVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff
                            val description = if (passwordSMTPVisible) "Hide SMTP Password" else "Show SMTP Password"

                            IconButton(onClick = { passwordSMTPVisible = !passwordSMTPVisible }) {
                                Icon(imageVector = image, description)
                            }
                        },
                        modifier = Modifier.weight(0.8f).padding(10.dp)
                    )
                }
                DividerImpl()
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(start = 10.dp, top = 10.dp, end = 10.dp)
                        .fillMaxWidth()
                ) {
                    LabelImpl("Lexoffice Invoice Settings")
                }
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(10.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedTextField(
                        value = unitName,
                        onValueChange = { unitName = it;settings.putString("unitName", it) },
                        label = { Text("Unit name") },
                        modifier = Modifier.weight(0.8f).padding(10.dp)
                    )
                    OutlinedTextField(
                        value = shippingCostName,
                        onValueChange = { shippingCostName = it;settings.putString("shippingCostName", it) },
                        label = { Text("Position name for shipping cost on invoice") },
                        modifier = Modifier.weight(0.8f).padding(10.dp)
                    )
                }
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(10.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedTextField(
                        value = invoiceTitle,
                        onValueChange = { invoiceTitle = it;settings.putString("invoiceTitle", it) },
                        label = { Text("Invoice title") },
                        modifier = Modifier.weight(0.8f).padding(10.dp)
                    )
                    OutlinedTextField(
                        value = invoiceFooter,
                        onValueChange = { invoiceFooter = it;settings.putString("invoiceFooter", it) },
                        label = { Text("Footer text for invoice") },
                        modifier = Modifier.weight(0.8f).padding(10.dp)
                    )
                }
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(10.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                   /* OutlinedTextField(
                        value = invoiceTitle,
                        onValueChange = { invoiceTitle = it;settings.putString("invoiceTitle", it) },
                        label = { Text("Invoice title") },
                        modifier = Modifier.weight(0.8f).padding(10.dp)
                    )*/
                    OutlinedTextField(
                        value = invoiceHeader,
                        onValueChange = { invoiceHeader= it;settings.putString("invoiceHeader", it) },
                        label = { Text("Header text for invoice") },
                        modifier = Modifier.weight(0.8f).padding(10.dp)
                    )
                }
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(10.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedTextField(
                        value = paymentTermLabel,
                        onValueChange = { paymentTermLabel = it;settings.putString("paymentTermLabel", it) },
                        label = { Text("Payment term on invoice") },
                        modifier = Modifier.weight(0.8f).padding(10.dp)
                    )
                    OutlinedTextField(
                        value = paymentTermDuration,
                        onValueChange = {
                            paymentTermDuration = it;
                            try {
                                paymentTermDuration.toInt()
                                isErrorPaymentTermDurationNotInt = false;
                            } catch (e: Exception) {
                                isErrorPaymentTermDurationNotInt = true;
                            }
                            if (!isErrorPaymentTermDurationNotInt) {
                                settings.putString("paymentTermDuration", it)
                            }
                        },
                        label = { Text("Payment duration in days") },
                        modifier = Modifier.weight(0.8f).padding(10.dp),
                        isError = isErrorPaymentTermDurationNotInt,
                        supportingText = {
                            if (isErrorPaymentTermDurationNotInt) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Not a valid Number",
                                    color = Color.Red
                                )
                            }
                        },

                        )
                }
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(10.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(modifier = Modifier.weight(0.8f).height(IntrinsicSize.Min).padding(10.dp),
                        verticalArrangement = Arrangement.Center) {
                        Row(modifier=Modifier.padding(10.dp)) {
                            Checkbox(
                                checked = finalizeInvoiceState,
                                onCheckedChange = {
                                    finalizeInvoiceState = it;settings.putBoolean("finalizeInvoiceState", it)
                                },

                                )
                            Text(
                                "Finalize invoice? False=Draft mode",
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                    OutlinedTextField(
                        value = shippingDateDays,
                        onValueChange = {
                            shippingDateDays = it;
                            try {
                                shippingDateDays.toInt()
                                isErrorShippingDateDaysNotInt = false;
                            } catch (e: Exception) {
                                isErrorShippingDateDaysNotInt = true;
                            }
                            if (!isErrorShippingDateDaysNotInt) {
                                settings.putString("shippingDateDays", it)
                            }
                        },
                        label = { Text("Shipping duration in days") },
                        modifier = Modifier.weight(0.8f).padding(10.dp),
                        isError = isErrorShippingDateDaysNotInt,
                        supportingText = {
                            if (isErrorShippingDateDaysNotInt) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Not a valid Number",
                                    color = Color.Red
                                )
                            }
                        },

                        )
                }
                DividerImpl()
                Row(modifier = Modifier.padding(10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

                    ExtendedFloatingActionButton(
                        onClick = {
                            store.settings.clear()
                            reset = !reset
                        },
                        modifier = Modifier.padding(start = 10.dp).align(Alignment.CenterVertically),

                        text = { Text("Reset Cache and Saved Data", color = Color.White) },
                        icon = { Icon(Icons.Filled.Delete, "", tint = Color.White) },
                        backgroundColor = Color.Red
                    )
                }

            }

            LaunchedEffect(reset) {}
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



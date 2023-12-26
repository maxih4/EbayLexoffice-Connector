package tabs


import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import controller.MailController
import controller.StorageController
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.Desktop
import java.net.URI

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
                    index = 1u,
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
        var reset by mutableStateOf(false)

        val localFocusManager = LocalFocusManager.current

        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                })
            }) {

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
            Row(modifier = Modifier.padding(10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                ExtendedFloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            sendMail()
                        }

                    },
                    modifier = Modifier.padding(start = 10.dp).align(Alignment.CenterVertically),

                    text = { Text("Send Mail") },
                    icon = { Icon(Icons.Filled.Send, "") },
                    backgroundColor = Color.LightGray
                )
                ExtendedFloatingActionButton(
                    onClick = {
                        store.settings.clear()
                        reset=!reset
                    },
                    modifier = Modifier.padding(start = 10.dp).align(Alignment.CenterVertically),

                    text = { Text("Reset Cache and Saved Data") },
                    icon = { Icon(Icons.Filled.Delete, "") },
                    backgroundColor = Color.LightGray
                )}

            }



        LaunchedEffect(reset){}
    }

    private suspend fun sendMail() {
        val mailList = mutableListOf<Deferred<Job>>()
        val starttime = Clock.System.now()
        println("Start sending mails")
        for (i in 1..20) {

            mailList.add(CoroutineScope(Dispatchers.Default).async {
                mailController.sendMail(
                    from = "handkemax@gmail.com",
                    to = "max.handke99@gmail.com",
                    "TestMail $i",
                    "<3"
                )
            })

        }
        val awaitedList = mailList.awaitAll().joinAll()

        val time = starttime - Clock.System.now()
        println("Sending $20 mails took me $time")

    }

}



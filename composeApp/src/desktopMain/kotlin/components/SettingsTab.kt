package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import storage.kvstore
import java.awt.Desktop
import java.awt.SystemColor.text
import java.net.URI

object SettingsTab : KoinComponent, Tab {
    private val store: kvstore by inject()
    private val settings = store.settings
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

        var apiKey by rememberSaveable { mutableStateOf(settings.getString("apiKey","")) }
        var apiKeyVisible by rememberSaveable { mutableStateOf(false) }
        val localFocusManager = LocalFocusManager.current

        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                })
            }) {
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {

                OutlinedTextField(

                    value = apiKey,
                    onValueChange = { apiKey = it;settings.putString("apiKey",it)},
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
                    }
                )
                ExtendedFloatingActionButton(
                    onClick = { openBrowserApiKey() },
                    modifier = Modifier.padding(start = 10.dp).align(Alignment.CenterVertically),

                    text = { Text("Get Api Key") },
                    icon = { Icon(Icons.Filled.AddCircle, "") },
                    backgroundColor = Color.LightGray
                )

            }


        }
    }


}
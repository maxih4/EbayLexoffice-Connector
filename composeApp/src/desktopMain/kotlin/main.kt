import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import controller.LexofficeController
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import storage.kvstore


fun main() = application {
    val appModule = module {
        single {kvstore()}
        single{ LexofficeController()}
    }
    val state = rememberWindowState(placement = WindowPlacement.Floating)
    state.size= DpSize(900.dp,900.dp)

    Window(onCloseRequest = ::exitApplication,state, resizable = true, title = "Ebay Lexoffice Connector") {

        startKoin{
            modules(appModule)
        }
        App()

    }
}


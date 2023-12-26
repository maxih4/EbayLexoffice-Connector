import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import controller.LexofficeController
import controller.MailController
import controller.StorageController
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import java.awt.Dimension


fun main() = application {
    val appModule = module {
        single { StorageController() }
        single{ LexofficeController()}
        single{ MailController() }
    }
    val state = rememberWindowState(placement = WindowPlacement.Floating)
    //state.size= DpSize(900.dp,900.dp)

    Window(onCloseRequest = ::exitApplication,state, resizable = true, title = "Ebay Lexoffice Connector") {
        window.minimumSize= Dimension(900,900)
        startKoin{
            modules(appModule)
        }
        App()

    }
}


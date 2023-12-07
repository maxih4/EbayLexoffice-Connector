import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import controller.LexofficeController
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import storage.kvstore


fun main() = application {
    val appModule = module {
        single {kvstore()}
        single{ LexofficeController()}
    }

    Window(onCloseRequest = ::exitApplication) {
        startKoin{
            modules(appModule)
        }
        App()

    }
}

@Preview
@Composable
fun AppDesktopPreview() {

    App()
}
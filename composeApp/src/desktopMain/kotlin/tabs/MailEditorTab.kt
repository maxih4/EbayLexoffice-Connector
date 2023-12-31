package tabs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachEmail
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.koin.core.component.KoinComponent

object MailEditorTab : KoinComponent, Tab {
    private fun readResolve(): Any = PaymentTab
    override val options: TabOptions
        @Composable
        get() {

            val icon = rememberVectorPainter(Icons.Default.AttachEmail)

            return remember {

                TabOptions(
                    index = 3u,
                    title = "Mail Editor",
                    icon = icon

                )
            }

        }

    @Composable
    @Preview
    override fun Content() {

        Text("Not yet implemented")
    }
}
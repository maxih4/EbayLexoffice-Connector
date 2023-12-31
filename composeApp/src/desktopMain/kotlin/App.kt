import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.navigator.tab.*
import org.jetbrains.skiko.CursorManager
import tabs.HomeTab
import tabs.MailEditorTab
import tabs.PaymentTab
import tabs.SettingsTab
import java.awt.Component
import java.awt.Cursor


@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current


    BottomNavigationItem(
        selected = tabNavigator.current.key == tab.key,
        onClick = { tabNavigator.current = tab },
        icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) }

    )
}

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun App() {

    TabNavigator(
        HomeTab,
        tabDisposable = {
            TabDisposable(
                navigator = it,
                tabs = listOf(HomeTab, SettingsTab)
            )
        }
    ) { tabNavigator ->

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = tabNavigator.current.options.title) }
                )
            },
            content = { padding ->
                Column(modifier = Modifier.padding(padding)) {


                    CurrentTab()
                }

            },
            bottomBar = {
                BottomNavigation {
                    TabNavigationItem(HomeTab)
                    //TabNavigationItem(PaymentTab)
                    //TabNavigationItem(MailEditorTab)
                    TabNavigationItem(SettingsTab)

                }
            }
        )


    }

}
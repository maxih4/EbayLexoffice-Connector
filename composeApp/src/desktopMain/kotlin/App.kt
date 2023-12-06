import androidx.compose.foundation.*

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*


import androidx.compose.material.*

import androidx.compose.runtime.*


import androidx.compose.ui.Modifier

import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi

import cafe.adriel.voyager.navigator.tab.*


import components.HomeTab

import components.SettingsTab








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
                    tabs = listOf(HomeTab,SettingsTab)
                )
            }
        ) { tabNavigator ->

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = tabNavigator.current.options.title) }
                    )
                },
                content = {
                    padding->
                    Column(modifier=Modifier.padding(padding)){


                        CurrentTab()
                    }

                },
                bottomBar = {
                    BottomNavigation {
                        TabNavigationItem(HomeTab)
                        TabNavigationItem(SettingsTab)

                    }
                }
            )


        }


    }
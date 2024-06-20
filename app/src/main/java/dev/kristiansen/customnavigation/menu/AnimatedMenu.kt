package dev.kristiansen.customnavigation.menu

import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.Key.Companion.Home
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.kristiansen.customnavigation.R
import dev.kristiansen.customnavigation.screens.HomeScreen
import dev.kristiansen.customnavigation.screens.SearchFormScreen
import dev.kristiansen.customnavigation.screens.SearchResultScreen
import dev.kristiansen.customnavigation.screens.SearchScreen
import kotlinx.coroutines.launch

open class ScreenDestination(val route: String, val name: String) {
    object Home : ScreenDestination("Home", "Home")
    object Search : ScreenDestination("Search", "Search")
    object SearchForm : ScreenDestination("Search Form", "Search")
    object SearchResult : ScreenDestination("Search Result", "Search Result")
    object SearchDetails : ScreenDestination("Search Details", "Details")
    object Scanner : ScreenDestination("Scanner", "Scanner")

    companion object {
        fun mainDestinations(): List<ScreenDestination> {
            return listOf(Home, Search)
        }
    }
}

@Composable
fun ExampleUse() {
    val navController: NavHostController = rememberNavController()

    AnimatedMenu(
        navHostController = navController,
        navHost = { modifier: Modifier ->
            NavHost(
                navController = navController,
                startDestination = ScreenDestination.Home.route,
                modifier = Modifier
            ) {

                composable(route = ScreenDestination.Home.route) {
                    HomeScreen()
                }
                composable(route = ScreenDestination.Search.route) {
                    SearchScreen(
                        navigateToSearchFormScreen = { navController.navigate(route = ScreenDestination.SearchForm.route)},
                        backNavigation = { navController.navigateUp() }
                    )
                }
                composable(route = ScreenDestination.SearchForm.route) {
                    SearchFormScreen(
                        navigateToSearchResultScreen = { navController.navigate(route = ScreenDestination.SearchResult.route)}
                    )
                }
                composable(route = ScreenDestination.SearchResult.route) {
                    SearchResultScreen()
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimatedMenu(
    navHostController: NavHostController,
    navHost: @Composable (modifier: Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()

    val width = LocalConfiguration.current.screenWidthDp
    val drawerWidth = -(width.toFloat() * 1.5f)

    val corutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    Box(modifier) {
        var drawerState by remember {
            mutableStateOf(DrawerValue.Closed)
        }

        val anchors = DraggableAnchors {
            DrawerValue.Open at drawerWidth
            DrawerValue.Closed at 0f
        }
        val state = remember {
            AnchoredDraggableState(
                initialValue = DrawerValue.Closed,
                anchors = anchors,
                positionalThreshold = { distance: Float -> distance * 0.5f },
                animationSpec = spring(),
                velocityThreshold = { with(density) { 80.dp.toPx() } },
                confirmValueChange = { draw ->
                    drawerState = draw
                    true
                }

            )
        }

        val toggleDrawerState: () -> Unit = {
            corutineScope.launch {
                drawerState = if (drawerState == DrawerValue.Open) {
                    state.animateTo(DrawerValue.Closed)
                    DrawerValue.Closed
                } else {
                    state.animateTo(DrawerValue.Open)
                    DrawerValue.Open
                }
            }
        }

        MenuDrawer(
            menuItems = ScreenDestination.mainDestinations(),
            menuNavigation = { screen: ScreenDestination -> navHostController.navigate(screen.route) {
                popUpTo(route = screen.route) { inclusive = true}
            } },
            onDismiss = toggleDrawerState,
        )
        Box(
            modifier = Modifier
                .graphicsLayer {
                    this.translationX = state.requireOffset()

                    val scale = lerp(1f, 0.8f, state.requireOffset() / drawerWidth)
                    this.scaleX = scale
                    this.scaleY = scale

                    val cornerSize = lerp(0.dp, 32.dp, state.requireOffset() / drawerWidth)
                    this.clip = true
                    this.shape = RoundedCornerShape(if (cornerSize >= 0.dp) cornerSize else 0.dp)
                }
                .anchoredDraggable(state, Orientation.Horizontal),
        ) {
            Scaffold(
                topBar = {
                    CustomTopBar(
                        currentScreen = navBackStackEntry?.destination?.route ?: "",
                        canNavigateBack = !ScreenDestination.mainDestinations().map { it.route }.contains(navBackStackEntry?.destination?.route),
                        navigateUp = { navHostController.navigateUp() },
                        toggleDrawer = toggleDrawerState
                    )
                }
            ) {
                navHost(Modifier.padding(it))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    currentScreen: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    toggleDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(currentScreen) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "back"
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = toggleDrawer,
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color.Black
                ),
            ) {
                Icon(Icons.Outlined.Menu, contentDescription = "menu")
            }
        }
    )
}
package dev.kristiansen.customnavigation.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun MenuDrawer(
    menuItems: List<ScreenDestination>,
    menuNavigation: (ScreenDestination) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.CenterStart
    ) {
        LazyColumn(
        ) {
            items(menuItems) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Gray.copy(alpha = 0.8f),
                    modifier = modifier
                        .fillMaxWidth()
                        .clickable {
                            menuNavigation(it)
                            onDismiss()
                        }
                ) {
                    Text(
                        text = it.name,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}
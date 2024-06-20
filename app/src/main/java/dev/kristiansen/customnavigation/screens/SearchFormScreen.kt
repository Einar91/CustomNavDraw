package dev.kristiansen.customnavigation.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SearchFormScreen(
    navigateToSearchResultScreen: () -> Unit
) {
    Button(onClick = navigateToSearchResultScreen) {
        Text(text = "Go to search result")
    }
}
package com.kwanzatukule.features.delivery.landing.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kwanzatukule.features.core.presentation.KwanzaPreview
import com.kwanzatukule.features.core.presentation.LocalAuthenticationState
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(component: HomeComponent, title: String, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Open navigation menu",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                        )
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Open options menu",
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val authenticationState = LocalAuthenticationState.current
            AnimatedVisibility(visible = authenticationState.isSignOutInProgress) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            Text(
                text = "Welcome to Kwanza Tukule",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
            AnimatedVisibility(authenticationState.currentUser != null) {
                Text(
                    text = "You are signed in as ${authenticationState.currentUser?.displayName ?: "-"}.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = component::onSignInRequested,
                    modifier = Modifier.weight(1f),
                    content = { Text(text = "Sign In") },
                )
                OutlinedButton(
                    onClick = component::onSignOutRequested,
                    modifier = Modifier.weight(1f),
                    content = { Text(text = "Sign Out") },
                    enabled = !authenticationState.isSignOutInProgress,
                )
            }
        }
    }
}

@KwanzaPreview
@Composable
private fun HomeScreenPreview() {
    KwanzaTukuleTheme {
        HomeScreen(
            title = "Kwanza Tukule",
            component = HomeComponent.Fake,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

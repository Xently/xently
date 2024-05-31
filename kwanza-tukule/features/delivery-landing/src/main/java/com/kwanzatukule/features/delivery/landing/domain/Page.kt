package com.kwanzatukule.features.delivery.landing.domain

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.kwanzatukule.features.delivery.landing.R

enum class Page(val icon: ImageVector, @StringRes val title: Int) {
    Home(Icons.Default.Home, R.string.page_home),
    Profile(Icons.Default.Person, R.string.page_profile),
}
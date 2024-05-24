package com.kwanzatukule.features.sales.landing.domain

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Shop
import androidx.compose.ui.graphics.vector.ImageVector
import com.kwanzatukule.features.sales.landing.R

enum class Page(val icon: ImageVector, @StringRes val title: Int) {
    Dashboard(Icons.Default.Dashboard, R.string.page_dashboard),
    Catalogue(Icons.Default.Shop, R.string.page_catalogue),
    Routes(Icons.Default.Route, R.string.page_routes),
    Complaints(Icons.Default.Feedback, R.string.page_complaints),
}
package com.kwanzatukule.features.delivery.route.domain

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Map
import androidx.compose.ui.graphics.vector.ImageVector
import com.kwanzatukule.features.delivery.route.R

enum class Page(val icon: ImageVector, @StringRes val title: Int) {
    List(Icons.AutoMirrored.Filled.ViewList, R.string.page_list),
    Map(Icons.Default.Map, R.string.page_map),
}
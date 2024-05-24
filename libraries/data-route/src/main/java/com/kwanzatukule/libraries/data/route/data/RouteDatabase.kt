package com.kwanzatukule.libraries.data.route.data

import com.kwanzatukule.libraries.core.data.TransactionFacadeDatabase

interface RouteDatabase : TransactionFacadeDatabase {
    fun routeDao(): RouteDao
    fun routeSummaryDao(): RouteSummaryDao
}
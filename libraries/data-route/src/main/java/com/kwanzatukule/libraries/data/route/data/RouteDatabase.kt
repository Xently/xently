package com.kwanzatukule.libraries.data.route.data

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface RouteDatabase : TransactionFacadeDatabase {
    fun routeDao(): RouteDao
    fun routeSummaryDao(): RouteSummaryDao
}
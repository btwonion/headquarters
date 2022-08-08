package dev.nyon.headquarter.api.database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

val databaseScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
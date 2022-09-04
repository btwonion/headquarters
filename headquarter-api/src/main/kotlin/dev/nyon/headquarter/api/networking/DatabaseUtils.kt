package dev.nyon.headquarter.api.networking

import dev.nyon.headquarter.api.common.InternalHeadquarterAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@InternalHeadquarterAPI
val databaseScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
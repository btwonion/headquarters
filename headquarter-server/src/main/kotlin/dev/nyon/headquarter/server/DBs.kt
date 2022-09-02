package dev.nyon.headquarter.server

import dev.nyon.headquarter.api.distribution.Client
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

lateinit var clientRealm: Realm

fun CoroutineScope.configureRealmConfiguration() {
    launch {
        val clientConfiguration = RealmConfiguration.create(schema = setOf(Client::class))
        clientRealm = Realm.open(clientConfiguration)
    }
}
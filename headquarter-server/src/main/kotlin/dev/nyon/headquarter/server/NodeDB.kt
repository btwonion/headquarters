package dev.nyon.headquarter.server

import dev.nyon.headquarter.api.distribution.Node
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

lateinit var nodeRealm: Realm

fun CoroutineScope.configureRealmConfiguration() {
    launch {
        val configuration = RealmConfiguration.create(schema = setOf(Node::class))
        nodeRealm = Realm.open(configuration)
    }
}
package dev.nyon.headquarter.server

import dev.nyon.headquarter.api.distribution.Client
import dev.nyon.headquarter.api.player.NetworkPlayer
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

lateinit var playerRealm: Realm
lateinit var clientRealm: Realm

fun CoroutineScope.configureRealmConfiguration() {
    launch {
        val playerConfiguration = RealmConfiguration.create(schema = setOf(NetworkPlayer::class))
        playerRealm = Realm.open(playerConfiguration)

        val clientConfiguration = RealmConfiguration.create(schema = setOf(Client::class))
        clientRealm = Realm.open(clientConfiguration)
    }
}
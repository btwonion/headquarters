package dev.nyon.headquarters.app.user

import dev.nyon.headquarters.app.dataDir
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query

val userSettingDB = run {
    val config =
        RealmConfiguration.Builder(setOf(UserSettings::class)).directory("${dataDir.toAbsolutePath()}/db/")
            .name("headquarters_user_settings").build()
    Realm.open(config)
}

suspend fun updateUserSetting(realmCallback: MutableRealm.(settings: UserSettings) -> Unit) {
    userSettingDB.write {
        val foundSettings = this@write.query<UserSettings>("id == $0", 1).first().find()!!
        realmCallback(foundSettings)
    }
}
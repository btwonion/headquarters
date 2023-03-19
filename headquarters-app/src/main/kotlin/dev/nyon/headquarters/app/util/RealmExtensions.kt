package dev.nyon.headquarters.app.util

import dev.nyon.headquarters.app.profile.Profile
import dev.nyon.headquarters.app.profile.realm
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.ext.query

suspend fun updateProfile(id: String, realmCallback: MutableRealm.(profile: Profile) -> Unit) {
    realm.write {
        val foundProfile = this@write.query<Profile>("profileID == $0", id).first().find()!!
        realmCallback(foundProfile)
    }
}

suspend fun getProfile(id: String): Profile? =
    realm.query<Profile>("profileID == $0", id).first().find().also {
        it?.initLoaderProfile()
        it?.initMinecraftVersionPackage()
    }
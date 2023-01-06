package dev.nyon.headquarters.app.profile

import dev.nyon.headquarters.api.Profile
import io.realm.kotlin.types.RealmObject

class LocalProfile() : RealmObject {
    var name = ""
    var profileID = ""
    var modProfile: Profile.ModProfile? = null

    constructor(name: String, profileID: String) : this() {
        this.name = name
        this.profileID = profileID
        // TODO modprofile init
    }
}
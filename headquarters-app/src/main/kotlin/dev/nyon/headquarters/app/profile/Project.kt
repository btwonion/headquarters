package dev.nyon.headquarters.app.profile

import io.realm.kotlin.types.EmbeddedRealmObject

class Project() : EmbeddedRealmObject {
    var versionID: String = ""
    var projectID: String = ""
    var enabled: Boolean = true
}
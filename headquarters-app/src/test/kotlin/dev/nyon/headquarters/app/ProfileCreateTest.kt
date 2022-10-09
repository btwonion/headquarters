package dev.nyon.headquarters.app

import dev.nyon.headquarters.app.profile.local.createProfile
import dev.nyon.headquarters.app.profile.models.LocalProfile
import io.kotest.core.spec.style.FunSpec

class ProfileCreateTest : FunSpec({
    test("create") {
        initApp()
        createProfile(LocalProfile("test", "sdasd", "sadasd"))
    }
})
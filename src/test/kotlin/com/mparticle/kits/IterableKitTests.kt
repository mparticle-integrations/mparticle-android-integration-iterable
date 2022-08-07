package com.mparticle.kits

import org.junit.Assert
import org.junit.Test

class IterableKitTests {
    private val kit: KitIntegration
         get() = IterableKit()

    @Test
    @Throws(Exception::class)
    fun testGetName() {
        val name = kit.name
        Assert.assertTrue(!name.isNullOrEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun testClassName() {
        val factory = KitIntegrationFactory()
        val integrations = factory.knownIntegrations
        val className = kit.javaClass.name
        for (integration in integrations) {
            if (integration.value == className) {
                return
            }
        }
        Assert.fail("$className not found as a known integration.")
    }
}

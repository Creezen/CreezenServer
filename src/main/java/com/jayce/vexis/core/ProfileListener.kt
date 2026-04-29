package com.jayce.vexis.core

import jakarta.servlet.ServletContextEvent
import jakarta.servlet.ServletContextListener

class ProfileListener : ServletContextListener {

    override fun contextInitialized(sce: ServletContextEvent?) {
        val profile = System.getenv()["CreezenEnv"] ?: "LOCAL"
        println("profile: $profile")
        System.setProperty("spring.profiles.active", profile)
    }
}
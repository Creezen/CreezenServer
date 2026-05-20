package com.jayce.vexis.core

import com.jayce.vexis.foundation.Log
import jakarta.servlet.ServletContextEvent
import jakarta.servlet.ServletContextListener

class ProfileListener : ServletContextListener {

    private val log by lazy { Log(this::class.java) }

    override fun contextInitialized(sce: ServletContextEvent?) {
        val profile = System.getenv()["CreezenEnv"] ?: "LOCAL"
        log.d("profile: $profile")
        System.setProperty("spring.profiles.active", profile)
    }
}
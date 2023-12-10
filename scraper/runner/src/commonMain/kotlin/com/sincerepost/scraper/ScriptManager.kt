package com.sincerepost.scraper

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ScriptManager {

    fun getScript(function: String, config: ScrapeConfig): String {
        val resource = javaClass.getResourceAsStream("/scraper.js")
            ?.bufferedReader()?.readText()
            ?: throw IllegalStateException("File not found: scraper.js")
        return "${resource}\nreturn Scraper.${function}(${Json.encodeToString(config)});"
    }
}

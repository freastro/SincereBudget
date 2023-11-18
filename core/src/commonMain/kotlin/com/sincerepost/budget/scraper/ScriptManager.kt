package com.sincerepost.budget.scraper

import com.sincerepost.budget.account.Account
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ScriptManager {

    fun getScript(state: ScrapeState, account: Account): String {
        val scriptName = if (state == ScrapeState.LOGIN) {
            "login.js"
        } else {
            throw IllegalArgumentException("No script for $state")
        }

        val resource = javaClass.getResourceAsStream("/com/sincerepost/scraper/$scriptName")
            ?.bufferedReader()?.readText()
            ?: throw IllegalStateException("Script not found for $state")
        return "return ${resource}(${Json.encodeToString(account)})"
    }
}

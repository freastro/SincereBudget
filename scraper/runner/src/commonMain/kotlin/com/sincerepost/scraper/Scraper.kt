package com.sincerepost.scraper

class Scraper(val vendorUrl: String, val config: ScrapeConfig, val webKit: WebKit) {

    suspend fun login() {
        webKit.loadUrl(vendorUrl)
    }

    suspend fun listAccounts(): Any {
        val script = ScriptManager.getScript("listAccounts", config)
        return webKit.evaluateJavaScript(script)
    }
}

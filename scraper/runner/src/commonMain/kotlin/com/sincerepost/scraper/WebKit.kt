package com.sincerepost.scraper

interface WebKit {

    fun close()

    suspend fun evaluateJavaScript(script: String): String

    fun getUrl(): String

    fun loadUrl(url: String)
}

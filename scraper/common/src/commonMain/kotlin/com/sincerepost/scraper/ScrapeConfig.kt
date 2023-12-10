package com.sincerepost.scraper

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
sealed interface ScrapeConfig {

    val currencyRegex: String
}

package com.sincerepost.scraper

import kotlinx.serialization.Serializable

@Serializable
data class JvmScrapeConfig(override val currencyRegex: String) : ScrapeConfig

package com.sincerepost.scraper.jvm

import com.sincerepost.scraper.WebKit
import org.openqa.selenium.chrome.ChromeDriver

class Selenium : WebKit {

    private val driver = ChromeDriver()

    override fun close() = driver.quit()

    override suspend fun evaluateJavaScript(script: String): String {
        return driver.executeScript(script)?.toString() ?: ""
    }

    override fun getUrl(): String = driver.currentUrl

    override fun loadUrl(url: String) = driver.get(url)
}

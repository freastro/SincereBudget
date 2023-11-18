package com.sincerepost.budget.jvm

import com.sincerepost.budget.WebKit
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

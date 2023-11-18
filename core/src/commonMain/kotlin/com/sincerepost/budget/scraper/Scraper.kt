package com.sincerepost.budget.scraper

import com.sincerepost.budget.WebKit
import com.sincerepost.budget.account.Account

class Scraper(val account: Account, val webKit: WebKit) {

    private var state: ScrapeState? = null

    suspend fun scrape(): ScrapeState {
        if (state == null) {
            webKit.loadUrl(account.vendorUrl)
            state = ScrapeState.LOGIN
        }

        if (state == ScrapeState.LOGIN) {
            val script = ScriptManager.getScript(ScrapeState.LOGIN, account)
            webKit.evaluateJavaScript(script)
        }
        return ScrapeState.SUCCESS
    }
}

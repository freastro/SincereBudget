package scraper

import com.sincerepost.scraper.ScrapeConfig
import org.w3c.dom.Element

abstract class Scraper<R>(protected val config: ScrapeConfig) {

    abstract fun scrape(body: Element): R
}

package scraper

import com.sincerepost.scraper.ScrapeConfig
import org.w3c.dom.Node

abstract class Scraper<R>(protected val config: ScrapeConfig) {
    abstract fun scrape(start: Node, selector: String): Result<R>
}

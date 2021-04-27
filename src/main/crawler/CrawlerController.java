package main.crawler;

import java.io.File;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class CrawlerController {
    public void crawl() throws Exception {
        File crawlStorage = new File("src/test/resources/crawler");
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorage.getAbsolutePath());
        //config.setMaxDepthOfCrawling(10);
        config.setMaxPagesToFetch(100);
        config.setMaxOutgoingLinksToFollow(2000);
        config.setPolitenessDelay(3000);
        config.setIncludeHttpsPages(true);
        config.setUserAgentString("Soccer stats demo (Private educational project)");

        int numCrawlers = 4;

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed("https://www.srgoool.com.br/classificacao/Brasileirao/Serie-A/2020");
        //controller.addSeed("https://www.ogol.com.br");

        /* Creates the output file. */
        File resource = new File(System.getProperty("user.dir") + "/temp/out.json");
        resource.createNewFile();

        CrawlerStatistics stats = new CrawlerStatistics();
        CrawlController.WebCrawlerFactory<Crawler> factory = () -> new Crawler(stats);

        controller.start(factory, numCrawlers);
        System.out.printf("Crawled %d pages %n", stats.getProcessedPageCount());
        System.out.printf("Total Number of outbound links = %d %n", stats.getTotalLinksCount());
    }
}

package main.crawler;

import java.io.File;
import java.io.FileWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
        config.setPolitenessDelay(10000);
        config.setIncludeHttpsPages(true);
        config.setUserAgentString("Soccer stats demo (Private educational project)");

        int numCrawlers = 1;

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        //controller.addSeed("https://www.srgoool.com.br/");
        //controller.addSeed("https://www.ogol.com.br/");

        controller.addSeed("https://www.srgoool.com.br/classificacao/Bundesliga/Unica/2020");
        controller.addSeed("https://www.srgoool.com.br/classificacao/Copa-Suruga/Unica/2020");
        controller.addSeed("https://www.srgoool.com.br/classificacao/La-Liga/Unica/2020");
        controller.addSeed("https://www.srgoool.com.br/classificacao/Lega-Serie-A/Unica/2020");
        controller.addSeed("https://www.srgoool.com.br/classificacao/Ligue-1/Unica/2020");
        controller.addSeed("https://www.srgoool.com.br/classificacao/Premier-League/Unica/2020");
        
        //controller.addSeed("https://www.ogol.com.br/edition.php?id_edicao=140440");
        //controller.addSeed("https://www.ogol.com.br/edition.php?id_edicao=142460");
        //controller.addSeed("https://www.ogol.com.br/edition.php?id_edicao=139360");
        //controller.addSeed("https://www.ogol.com.br/edition.php?id_edicao=140824");
        //controller.addSeed("https://www.ogol.com.br/edition.php?id_edicao=142502");
        //controller.addSeed("https://www.ogol.com.br/edition.php?id_edicao=142587");
        //controller.addSeed("https://www.ogol.com.br/edition.php?id=140062");
        //controller.addSeed("https://www.ogol.com.br/edition.php?id=149653");
        //controller.addSeed("https://www.ogol.com.br/edition.php?id_edicao=140315");
        //controller.addSeed("https://www.ogol.com.br/edition.php?id_edicao=140084");
        //controller.addSeed("https://www.ogol.com.br/edition.php?id_edicao=140394");
        //controller.addSeed("https://www.ogol.com.br/edition.php?id_edicao=140332");
        //controller.addSeed("https://www.ogol.com.br/edition.php?id_edicao=140351");
        //controller.addSeed("https://www.ogol.com.br/edition.php?id_edicao=140579");
        //controller.addSeed("https://www.ogol.com.br/edition.php?id=146096");
        //controller.addSeed("https://www.ogol.com.br/edition.php?id=140958");
        //controller.addSeed("https://www.ogol.com.br/edition.php?id_edicao=140603");
        //controller.addSeed("https://www.ogol.com.br/edition.php?id_edicao=140443");
        //controller.addSeed("https://www.ogol.com.br/edition.php?id_edicao=140434");

        /* Creates the output file. */
        File resource = new File(System.getProperty("user.dir") + "/temp/out.json");
        resource.createNewFile();

        System.out.println("File created");

        /* Adds the base JSONArray to this output file. */
        JSONObject obj = new JSONObject();
        JSONArray campeonatos = new JSONArray();

        obj.put("campeonatos", campeonatos);

        FileWriter writer = new FileWriter(resource);
        writer.write(obj.toJSONString());
        writer.flush();
        writer.close();

        CrawlerStatistics stats = new CrawlerStatistics();
        CrawlController.WebCrawlerFactory<Crawler> factory = () -> new Crawler(stats);

        System.out.println("Going to start");

        controller.start(factory, numCrawlers);
        System.out.printf("Crawled %d pages %n", stats.getProcessedPageCount());
        System.out.printf("Total Number of outbound links = %d %n", stats.getTotalLinksCount());
    }
}

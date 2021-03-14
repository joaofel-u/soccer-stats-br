package main.app;

import main.crawler.Crawler;
import edu.uci.ics.crawler4j.crawler.*;

class Main {
    public static void main(String args[]) {
        Crawler myCrawler = new Crawler();
        CrawlConfig config = new CrawlConfig();

        System.out.println(config.getClass().toString());
        
        System.out.println("Hello world!");
    
        myCrawler.hello();
    }
}
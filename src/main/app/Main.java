package main.app;

import main.crawler.CrawlerController;

class Main {
    public static void main(String args[]) {
        CrawlerController myCrawler = new CrawlerController();
    
        try {
            myCrawler.crawl();
        } catch (Exception ex) {
            System.out.println("An exception occured");
            ex.printStackTrace();
        }
    }
}
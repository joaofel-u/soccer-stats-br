package main.crawler;

public class CrawlerStatistics {
    private int processedPageCount = 0;
    private int totalLinksCount = 0;
    
    public void incrementProcessedPageCount(int pageCount) {
        this.processedPageCount += pageCount;
    }
    
    public void incrementTotalLinksCount(int linksCount) {
        this.totalLinksCount += linksCount;
    }

    public int getProcessedPageCount() {
        return (this.processedPageCount);
    }

    public int getTotalLinksCount() {
        return (this.totalLinksCount);
    }
}

package main.crawler;

import java.util.Set;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import main.parser.Parser;

public class Crawler extends WebCrawler {

    private final static Pattern EXCLUSIONS = Pattern.compile(".*(\\.(css|gif|jpg|png|mp3|mp4|zip|gz|csv))$");
    private CrawlerStatistics stats;
    private boolean extractedMenu = false;

    public Crawler(CrawlerStatistics stats) {
        this.stats = stats;
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
    }

    public boolean shouldVisitOGOL(String url) {
        boolean result;

        result = url.contains("edicao") || url.contains("edition");

        return (result);
    }

    public boolean shouldVisitSRGOOL(String url) {
        boolean result;

        result = !EXCLUSIONS.matcher(url).matches() &&
                url.contains("classificacao") &&
                url.contains("2020") &&
                !url.contains("simulador") &&
                !url.contains("ranking");

        return (result);
    }

    public boolean shouldVisitUrl(String url) {
        boolean result;

        /* Removes www from string to make url checking easier. */
        String temp = url.replace("www.", "");

        if (temp.startsWith("https://srgoool.com.br") || temp.startsWith("http://srgoool.com.br"))
            result = shouldVisitSRGOOL(url);
        else if (temp.startsWith("https://ogol.com.br") || temp.startsWith("http://ogol.com.br"))
            result = shouldVisitOGOL(url);
        else
            result = true;

        return (result);
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String urlString = url.getURL().toLowerCase();
        boolean result = shouldVisitUrl(urlString);

        if (result)
            System.out.println("Should visit " + urlString);
        else
            System.out.println("Should not visit " + urlString);

        return (result);
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();

        this.stats.incrementProcessedPageCount(1);
        System.out.println("Visiting " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String title = htmlParseData.getTitle();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();
            int total_links;
            int links_set_size = 0;

            if (url.contains("classificacao"))
            {
                String pageSource;
                Select dropYear;

                WebDriver driver = new ChromeDriver();
                driver.get(url);

                /* Extracts menu content in the first page of SrGooool */
                if (!this.extractedMenu) {
                    WebElement element = driver.findElements(By.className("dropdown")).get(2);
                    Actions actions = new Actions(driver);

                    actions.moveToElement(element).click().perform();

                    for (WebElement element2 : element.findElements(By.className("select-menu-div"))) {
                        for (WebElement element3 : element2.findElements(By.className("select-menu"))) {
                            dropYear = new Select(element3);

                            WebDriverWait wait = new WebDriverWait(driver, 2);
                            try {
                                wait.until(ExpectedConditions.elementToBeClickable(element3));

                                dropYear.selectByVisibleText("2020");
                                System.out.println("Changed selection");

                                actions.moveToElement(element).click().perform();

                                Thread.sleep(1000);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    pageSource = driver.getPageSource();

                    for (String link : Parser.getExternalLinks(pageSource, "https://" + page.getWebURL().getDomain())) {
                        if (shouldVisitUrl(link)) {
                            scheduleURL(link, 0);
                            links_set_size++;

                            System.out.println("Must visit " + link);
                        } else {
                            System.out.println("Shouldn't visit " + link);
                        }
                    }

                    this.extractedMenu = true;
                } else {
                    System.out.println("Getting page source");

                    pageSource = driver.getPageSource();
                }

                driver.quit();

                Parser.printTable(pageSource, url);
            }

            total_links = links.size() + links_set_size;

            this.stats.incrementTotalLinksCount(total_links);
            logger.info("Found " + total_links + " new links!");
        }
    }

    /**
     * @brief Includes a string based Url in the Crawler frontier.
     *
     * @param url Base String to be converted to an URL.
     */
    public void scheduleURL(String url, int depth) {
        /* Checks for empty string. */
        if (url.length() == 0)
            return;

        WebURL wurl = new WebURL();
        int docId = this.myController.getDocIdServer().getDocId(url);

        /* Verifica se esse endereco ja nao passou pelo crawler. */
        if (docId >= 0)
            return;

        /* Gera um novo docId para essa URL. */
        docId = this.myController.getDocIdServer().getNewDocID(url);

        wurl.setURL(url);
        wurl.setDocid(docId);
        //wurl.setDepth((short) depth);

        this.myController.getFrontier().schedule(wurl);
    }
}

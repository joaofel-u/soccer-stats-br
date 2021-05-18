package main.crawler;

import java.util.Set;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import ch.qos.logback.core.joran.conditional.ElseAction;
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

        result = (url.contains("edicao") || url.contains("edition") || url.contains("associacao")) &&
                (!url.contains("transfer") && !url.contains("comparacao") && !url.contains("matches") && !url.contains("winner") &&
                !url.contains("arbitro") && !url.contains("top") && !url.contains("photo") && !url.contains("video") &&
                !url.contains("mapa") && !url.contains("stats") && !url.contains("jogo") && !url.contains("agenda") &&
                !url.contains("calendario") && !url.contains("colaborador") && !url.contains("foto") && !url.contains("equipa") &&
                !url.contains("team") && !url.contains("fase") && !url.contains("grupo"));

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

        if (temp.contains("noticia") || temp.contains("twitter") || temp.contains("instagram") || temp.contains("facebook") || temp.contains("news"))
            return (false);

        if (temp.startsWith("https://srgoool.com.br") || temp.startsWith("http://srgoool.com.br"))
            result = shouldVisitSRGOOL(url);
        else if (temp.startsWith("https://ogol.com.br") || temp.startsWith("http://ogol.com.br"))
            result = shouldVisitOGOL(url);
        else
            result = (temp.contains("classificacao") || temp.contains("campeonato") || temp.contains("gol") || temp.contains("futebol"));

        return (result);
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String urlString = url.getURL().toLowerCase();
        boolean result = shouldVisitUrl(urlString);

        // if (result)
        //     System.out.println("Should visit " + urlString);
        // else
        //     System.out.println("Should not visit " + urlString);

        return (result);
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        String pageSource;

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

            System.out.println("Proceeding");
        
            /* Extracts links from srgoool homepage. */
            if (url.compareTo("https://www.srgoool.com.br/") == 0) {
                String domain = page.getWebURL().getDomain();
                System.out.println("-----------------------------------------------------------");
                System.out.println("Extracting links from srgooooll");
                extractSrGooolLinks(url, domain);
                System.out.println("-----------------------------------------------------------");
                return;
            } else if (url.compareTo("https://www.ogol.com.br/") == 0) {
                String domain = page.getWebURL().getDomain();
                System.out.println("-----------------------------------------------------------");
                System.out.println("Extracting links from ogol");
                extractOGolLinks(url, domain);
                System.out.println("-----------------------------------------------------------");
                return;
            }

            WebDriver driver = new ChromeDriver();
            driver.get(url);

            System.out.println("Getting page source");

            /* Removes www from string to make url checking easier. */
            String temp = url.replace("www.", "");

            if (temp.startsWith("https://srgoool.com.br") || temp.startsWith("http://srgoool.com.br")) {
                pageSource = driver.getPageSource();
                driver.quit();

                Parser.parseSrGoool(pageSource, url);
            }
            else if (temp.startsWith("https://ogol.com.br") || temp.startsWith("http://ogol.com.br")) {
                /* Extracts combo box. */
                WebElement topDiv = driver.findElement(By.className("top"));
                WebElement combo = topDiv.findElement(By.className("combo"));
                WebElement select = combo.findElement(By.tagName("select"));
                Select dropYear = new Select(select);
                Actions actions = new Actions(driver);

                JavascriptExecutor j = (JavascriptExecutor) driver;

                System.out.println("Before scripts");

                j.executeScript("let options = arguments[0].options; n" +
                                "for (let opt, j = 0; opt = options[j]; j++) { n" +
                                "if (opt.text === 2020) { n" +
                                "arguments[0].selectedIndex = j; n" +
                                "break; } }", select);

                System.out.println("After script");

                //actions.moveToElement(select).click().perform();

                /* Select correct edition of competition. */
                //WebDriverWait wait = new WebDriverWait(driver, 2);
                try {
                    //wait.until(ExpectedConditions.elementToBeClickable(select));
                    System.out.println("Before first");
                    dropYear.selectByIndex(2);
                    System.out.println(dropYear.getFirstSelectedOption().getText());
                    System.out.println("After first");

                    dropYear.selectByVisibleText("2020");
                    System.out.println("Changed selection");

                    //actions.moveToElement(element).click().perform();

                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                pageSource = driver.getPageSource();
                driver.quit();

                Parser.parseOGol(pageSource, url);
            }
            else {
                driver.quit();
                System.out.println("Not parsing " + url);
            }

            this.stats.incrementTotalLinksCount(links.size());
            logger.info("Found " + links.size() + " new links!");
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

        this.myController.getFrontier().schedule(wurl);
    }

    public void extractSrGooolLinks(String url, String domain) {
        String pageSource;
        Select dropYear;
        WebDriver driver;
        Set<String> links;
        int links_set_size = 0;

        System.out.println("Extracting links from srgoool main page...");

        driver = new ChromeDriver();
        driver.get(url);

        //WebElement popup = driver.findElements(By.className("fechar")).get(0);

        /* Closes annoying popup. */
        driver.navigate().refresh();

        WebElement element = driver.findElements(By.className("dropdown-toggle")).get(2);
        Actions actions = new Actions(driver);

        // actions.moveToElement(popup).perform();
        // System.out.println("Moved");
        // actions.moveToElement(popup).click().perform();
        // System.out.println("Clicked");

        /* Opens the dropdown menu. */
        actions.moveToElement(element).click().perform();
        for (WebElement element2 : driver.findElements(By.className("select-menu-div"))) {
            System.out.println("Teste");
            for (WebElement element3 : element2.findElements(By.className("select-menu"))) {
                dropYear = new Select(element3);
                System.out.println("Here");
                WebDriverWait wait = new WebDriverWait(driver, 2);
                try {
                    //wait.until(ExpectedConditions.elementToBeClickable(element3));

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

        /* Extracts links from Srgooll. */
        links = Parser.getExternalLinks(pageSource, "https://" + domain);

        for (String link : links) {
            if (shouldVisitUrl(link)) {
                System.out.println("Should visit " + link);
                scheduleURL(link, 0);
                links_set_size++;
            }
        }

        System.out.println("Debug");

        this.stats.incrementTotalLinksCount(links_set_size);
        logger.info("Found " + links_set_size + " new links!");
    }

    public void extractOGolLinks(String url, String domain) {
        String pageSource;
        Select dropYear;
        WebDriver driver;
        Set<String> links;
        int links_set_size = 0;

        System.out.println("Extracting links from OGol main page...");

        driver = new ChromeDriver();
        driver.get(url);

        /* Closes annoying popup. */
        // driver.navigate().refresh();

        WebElement element = driver.findElements(By.className("cbp-hrmenu")).get(0);
        WebElement element2 = element.findElement(By.linkText("COMPETIÇÕES"));

        Actions actions = new Actions(driver);

        // /* Opens the dropdown menu. */
        actions.moveToElement(element2).click().perform();

        // for (WebElement element2 : element.findElements(By.className("select-menu-div"))) {
        //     for (WebElement element3 : element2.findElements(By.className("select-menu"))) {
        //         dropYear = new Select(element3);

        //         WebDriverWait wait = new WebDriverWait(driver, 2);
        //         try {
        //             wait.until(ExpectedConditions.elementToBeClickable(element3));

        //             dropYear.selectByVisibleText("2020");
        //             System.out.println("Changed selection");

        //             actions.moveToElement(element).click().perform();

        //             Thread.sleep(1000);
        //         } catch (Exception ex) {
        //             ex.printStackTrace();
        //         }
        //     }
        // }

        pageSource = driver.getPageSource();

        /* Extracts links from Srgooll. */
        links = Parser.getExternalLinks(pageSource, "https://" + domain);

        for (String link : links) {
            //System.out.println("Identified " + link);
            if (shouldVisitUrl(link)) {
                scheduleURL(link, 0);
                links_set_size++;
            }
        }

        this.stats.incrementTotalLinksCount(links_set_size);
        logger.info("Found " + links_set_size + " new links!");
    }
}

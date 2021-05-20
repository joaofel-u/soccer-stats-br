package main.crawler;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import main.parser.Parser;

public class Crawler extends WebCrawler {

    public static String OGolHome = "https://www.ogol.com.br/compet.php?gen=1&tipo=0&ambito=0&nivel=0&esc=0&mod=1&formato=0&continente=4&idpais=6&stats=0";

    private final static Pattern EXCLUSIONS = Pattern.compile(".*(\\.(css|gif|jpg|png|mp3|mp4|zip|gz|csv))$");
    private CrawlerStatistics stats;

    public Crawler(CrawlerStatistics stats) {
        this.stats = stats;
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
    }

    public boolean shouldVisitOGOL(String url) {
        boolean result;

        result = (url.contains("edicao") || url.contains("edition") || url.contains("associacao")) || url.contains("competicao") &&
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

        if (urlString.contains("captcha"))
            System.out.println("Recaptcha...");

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

        try {
            if (url.contains("captcha")) {
                System.out.println("Droga");
                Thread.sleep(10 * 1000);
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

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
                extractOGolLinks(url, domain, page);
                System.out.println("-----------------------------------------------------------");
                return;
            }

            WebDriver driver = new ChromeDriver();
            driver.get(url);

            try {
                int random = ThreadLocalRandom.current().nextInt(5, 16);
                System.out.println("Sleeping a little");
                Thread.sleep(random * 1000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            System.out.println("Getting page source");

            /* Removes www from string to make url checking easier. */
            String temp = url.replace("www.", "");

            if (temp.startsWith("https://srgoool.com.br") || temp.startsWith("http://srgoool.com.br")) {
                pageSource = driver.getPageSource();
                driver.quit();

                Parser.parseSrGoool(pageSource, url);
            }
            else if (temp.startsWith("https://ogol.com.br") || temp.startsWith("http://ogol.com.br")) {
                
                // COMPETITION PAGE
                if (url.contains("competicao")) {
                    WebElement edition = driver.findElement(By.id("page_main")).findElement(By.tagName("table")).findElement(By.tagName("a"));
                    System.out.println("Competicao");
                    scheduleURL(edition.getAttribute("href"), 0);
                    driver.quit();
                    return;
                } else if (url.contains("associacao")) { // ASSOCIATION PAGE
                    WebElement edition = driver.findElement(By.id("page_main")).findElement(By.className("box")).findElement(By.className("nivel1")).findElement(By.tagName("a"));
                    System.out.println("Associacao");
                    scheduleURL(edition.getAttribute("href"), 0);
                    driver.quit();
                    return;
                }
                
                /* Extracts combo box. */
                WebElement topDiv = driver.findElement(By.className("top"));
                WebElement factSheet = topDiv.findElement(By.className("factsheet"));
                WebElement combo = topDiv.findElement(By.className("combo"));
                WebElement select = combo.findElement(By.tagName("select"));
                WebElement comp = factSheet.findElement(By.className("name"));

                /* Is it a desired page? */
                if (factSheet.findElements(By.className("micrologo_and_text")).size() == 0) {
                    System.out.println(comp.getText() + " is not a Brazil competition");
                    driver.quit();
                    return;
                }

                WebElement org = factSheet.findElement(By.className("micrologo_and_text")).findElement(By.className("text"));
                if (!(org.getText().contains("Brasil")))
                {
                    System.out.println(comp.getText() + " is not a Brazil competition");
                    driver.quit();
                    return;
                }

                /* Already 2020 edition? */
                if (!(comp.getText().contains("2020"))) {
                    System.out.println("Changing selection for " + comp.getText());
                    JavascriptExecutor j = (JavascriptExecutor) driver;

                    j.executeScript("let options = arguments[0].options; \n" +
                                    "for (let opt, j = 0; opt = options[j]; j++) { \n" +
                                    "if (opt.text === '2020') { \n" +
                                    "arguments[0].selectedIndex = j; \n" +
                                    "arguments[0].onchange(); \n" +
                                    "break; } }", select);

                    scheduleURL(driver.getCurrentUrl(), 0);

                    driver.quit();
                    return;
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

    public void extractOGolLinks(String url, String domain, Page page) {
        HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
        String pageSource;
        Select dropYear;
        WebDriver driver;
        Actions actions;
        Set<String> links;
        int links_set_size = 0;

        System.out.println("Extracting links from OGol main page...");

        driver = new ChromeDriver();
        actions = new Actions(driver);
        driver.get(Crawler.OGolHome);

        // WebElement box = driver.findElement(By.className("box"));

        // for (WebElement element : box.findElements(By.className("micrologo_and_text"))) {
        //     for (WebElement div : element.findElements(By.className("text"))) {
        //         WebElement link;

        //         if (div.getText() == "") {
        //             System.out.println("Continue");
        //             continue;
        //         }

        //         if (div.getText().contains("Extinto") || div.getText().contains("Sub") ||
        //             div.getText().contains("Aspirante") || div.getText().contains("Júnior"))
        //             continue;

        //         link = div.findElement(By.tagName("a"));

        //         WebDriver driver2 = new ChromeDriver();
        //         String teste = link.getAttribute("href");
        //         System.out.println(teste);
        //         driver2.get(teste);
        //         /* Gets new url. */
        //         //driver.get("https://www.ogol.com.br/" + link.getAttribute("href"));

        //         // COMPETITION PAGE
        //         if (driver2.getCurrentUrl().contains("competicao")) {
        //             WebElement edition = driver2.findElement(By.tagName("table")).findElement(By.tagName("a"));
        //             System.out.println(edition.getAttribute("href"));
        //         } else if (driver2.getCurrentUrl().contains("associacao")) { // ASSOCIATION PAGE
        //             WebElement edition = driver2.findElement(By.className("nivel1"));
        //             System.out.println(edition.getAttribute("href"));
        //         } else {
        //             System.out.println("Unwanted page");
        //         }

        //         // Going back to the previous page.
        //         //driver.navigate().back();
        //         driver2.quit();
        //     }
        // }

        pageSource = driver.getPageSource();

        /* Extracts links from Srgooll. */
        links = Parser.getExternalLinks(pageSource, "https://" + domain);

        for (String link : links) {
            //String url2 = link.getURL();
           // System.out.println("Identified " + link);
            if (shouldVisitUrl(link)) {
                scheduleURL(link, 0);
                links_set_size++;
            }
        }

        this.stats.incrementTotalLinksCount(links_set_size);
        logger.info("Found " + links_set_size + " new links!");
    }
}

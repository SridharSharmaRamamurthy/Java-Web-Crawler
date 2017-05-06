package com.wipro.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class RealCrawler extends WebCrawler {

    static final Logger logger = LoggerFactory.getLogger(RealCrawler.class);

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|zip|gz|xml))$");

    private final static String LINK_SELECTOR = "body a[href]";

    private final static String IMAGE_SELECTOR = "body img";
  
    private static String domain;

    private static String host;

    static List<String> pageLinksList= new ArrayList<String>();
    static List<String> outsideLinksList= new ArrayList<String>();
    static List<String> filesLinksList= new ArrayList<String>();
    
    /* (non-Javadoc)
     * @see edu.uci.ics.crawler4j.crawler.WebCrawler#shouldVisit(edu.uci.ics.crawler4j.crawler.Page, edu.uci.ics.crawler4j.url.WebURL)
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches() && href.startsWith(domain);
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.crawler4j.crawler.WebCrawler#visit(edu.uci.ics.crawler4j.crawler.Page)
     */
    @Override
    public void visit(Page page) {

        if (!(page.getParseData() instanceof HtmlParseData)) {
            return;
        }

        String url = normalizeUrl(page.getWebURL().getURL(), domain);

        logger.info("visiting: " + url);
        addPageLinksList(url);

        HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
        String html = htmlParseData.getHtml();

        Document doc = Jsoup.parse(html);
        doc.setBaseUri(url);

        String link;
        Elements elements;

        elements = doc.select(LINK_SELECTOR);
        for (Element element : elements) {
            link = normalizeUrl(element.attr("href"), domain);
            if (isExternalLink(link) && link.length() > 0) {
                addOutsideLinksList(link);
            }
        }

        elements = doc.select(IMAGE_SELECTOR);
        for (Element element : elements) {
            link = normalizeUrl(element.attr("src"), domain);
            if (link.length() > 0) {
                addFilesLinksList(link);
            }
        }        

    }

    /**
     * @param link
     */
    private void addFilesLinksList(String link) {
		// TODO Auto-generated method stub
    	filesLinksList.add(link);
		
	}

	/**
	 * @param link
	 */
	private void addOutsideLinksList(String link) {
		// TODO Auto-generated method stub
		outsideLinksList.add(link);
		
	}

	/**
	 * @param url
	 */
	private void addPageLinksList(String url) {
		// TODO Auto-generated method stub
		pageLinksList.add(url);
	}

	/**
	 * @param link
	 * @param domain
	 * @return
	 */
	private String normalizeUrl(String link, String domain) {

        if(link.startsWith("mailto:")) {
            return "";
        }

        URI uri;
        try {

            uri = new URI(link);
            if (!uri.isAbsolute()) {
                uri = new URI(domain + "/")
                        .resolve(uri)
                        .normalize();
            }

            String uriStr = uri.toString();

            // remove fragment
            if(uri.getFragment() != null) {
                int index = uriStr.indexOf("#");
                if (index == -1) { // should't occur
                    return uriStr;
                }
                return uriStr.substring(0, index);
            }

            return uriStr;

        } catch (URISyntaxException e) {}
        
        return "";
    }

    /**
     * @param link
     * @return
     */
    private boolean isExternalLink(String link) {

        if (link.equals("")) {
            return false;
        }

        URI uri;

        try {
            uri = new URI(link);
            if(Objects.equals(uri.getHost(), RealCrawler.host)) {
                return false;
            }
            return true;

        } catch (URISyntaxException e) {}

        return false;
    }   
    

	/**
	 * @param crawlUrl
	 * @throws URISyntaxException
	 */
	public static void init(String crawlUrl) throws URISyntaxException {
		// TODO Auto-generated method stub
		domain = crawlUrl;
        host = new URI(RealCrawler.domain).getHost();
		
	}

	/**
	 * @return
	 */
	public static String getFinalData() {
		// TODO Auto-generated method stub
		return "Response{" + "\n" +
        "pageLinks=" + pageLinksList + "\n" +
        ", outsideLinks=" + outsideLinksList + "\n" +
        ", filesLinks=" + filesLinksList + "\n" +
        '}';
	}
}

package com.wipro.crawler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.servlet.ServletOutputStream;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * @author ramasrid
 *
 */
/**
 * @author ramasrid
 *
 */
@Controller
public class WelcomeController {

	static final Logger logger = LoggerFactory.getLogger(WelcomeController.class);	

	private Integer crawlers = 1;	

	private Integer delay = 200;

	// inject via application.properties
	@Value("${welcome.message:test}")
	private String message = "Hello World";

	@RequestMapping("/")
	public String welcome(Map<String, Object> model) {
		model.put("message", this.message);
		return "welcome";
	}

	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/generateSiteMap")
	@ResponseBody
	public String generateSiteMap(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.info("Inside GenerateSiteMap Method");
		String crawlUrl = request.getParameter("crawlurl");
		String maxPages= request.getParameter("maxPages");
		if (StringUtils.isEmpty(crawlUrl) || !validateCrawlUrl(crawlUrl) || StringUtils.isEmpty(maxPages)) {
			model.put("isError", true);			
			return "welcome";
		}
		continueCrawl(crawlUrl,response, maxPages);
		model.put("message", this.message);
		return "welcome";
	}

	/**
	 * @param crawlUrl
	 * @param response
	 * @param maxpages
	 * @throws Exception
	 */
	private void continueCrawl(String crawlUrl, HttpServletResponse response, String maxpages) throws Exception {
		File targetClassesDir = new File(
				WelcomeController.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		logger.debug("Inside continueCrawl Method");
		logger.info("Started with Download the XML File");
		File targetDir = targetClassesDir.getParentFile();
		CrawlConfig config = new CrawlConfig();
		config.setPolitenessDelay(delay);
		config.setCrawlStorageFolder(targetDir.toString());
		config.setMaxPagesToFetch(Integer.parseInt(maxpages));
		config.setFollowRedirects(true);
		RealCrawler.init(crawlUrl);
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		controller.addSeed(crawlUrl);
		controller.start(RealCrawler.class, crawlers);
		String finalData = RealCrawler.getFinalData();
		Document xmlDocument = createXMl(finalData);
		downloadXML(xmlDocument, response);
		logger.info("Done with Download the XML File");
	}

	/**
	 * @param doc
	 * @param response
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 * @throws TransformerFactoryConfigurationError
	 */
	private void downloadXML(Document doc, HttpServletResponse response)
			throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
		logger.debug("Inside downloadXML Method");
		DOMSource source = new DOMSource(doc);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Result outputTarget = new StreamResult(outputStream);
		TransformerFactory.newInstance().newTransformer().transform(source, outputTarget);
		InputStream fis = new ByteArrayInputStream(outputStream.toByteArray());
		ServletOutputStream os;
		String fileName = "site-map.xml";
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		try {
			os = response.getOutputStream();
			byte[] bufferData = new byte[1024];
			int read = 0;
			while ((read = fis.read(bufferData)) != -1) {
				os.write(bufferData, 0, read);
			}
			os.flush();
			os.close();
			fis.close();

		} catch (IOException e) {			
			logger.error("Error in downloading XML"+ e);
		}
	}

	/**
	 * @param finalData
	 * @return
	 * @throws ParserConfigurationException
	 */
	private Document createXMl(String finalData) throws ParserConfigurationException {
		logger.debug("Inside createXMl Method");
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		// root elements
		Document doc = docBuilder.newDocument();
		Element root = doc.createElement("sitemap");
		root.appendChild(buildXml(doc, RealCrawler.pageLinksList, "pageLinks"));

		root.appendChild(buildXml(doc, RealCrawler.filesLinksList, "fileLinks"));

		root.appendChild(buildXml(doc, RealCrawler.outsideLinksList, "outsideSiteLinks"));
		doc.appendChild(root);
		return doc;

	}

	/**
	 * @param doc
	 * @param list
	 * @param field
	 * @return
	 */
	private Node buildXml(Document doc, List<String> list, String field) {
		logger.debug("Inside buildXml Method");
		Element el = doc.createElement(field);
		for (String link : list) {
			el.appendChild(buildFieldItem(doc, link));
		}

		return el;
	}

	/**
	 * @param doc
	 * @param link
	 * @return
	 */
	private Node buildFieldItem(Document doc, String link) {
		logger.debug("Inside buildFieldItem Method");
		Element url = doc.createElement("url");
		url.setTextContent(link);
		return url;
	}

	/**
	 * @param crawlUrl
	 * @return
	 */
	private boolean validateCrawlUrl(String crawlUrl) {
		logger.debug("Inside validateCrawlUrl Method");
		String[] schemes = { "http", "https" }; // DEFAULT schemes = "http","https", "ftp"
		UrlValidator urlValidator = new UrlValidator(schemes);
		if (!urlValidator.isValid(crawlUrl)) {
			logger.error("Invalid Url in Crawl Form");
			return false;
		}
		return true;
	}

}
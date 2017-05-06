
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.validation.constraints.AssertTrue;

import org.springframework.boot.test.SpringApplicationConfiguration; 
import com.wipro.crawler.SpringBootWebApplication;
import com.wipro.crawler.WelcomeController;



@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringBootWebApplication.class)
@WebAppConfiguration
public class WelcomeControllerTest {
	
	@Autowired
    private WebApplicationContext wac;
	
	private MockMvc mockMvc;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}
	
	@Test
	public void testEmptyParams(){		
		try {
			String fileName = "site-map.xml";
			MvcResult result = this.mockMvc.perform(post("/generateSiteMap").param("crawlurl", "").param("maxPages", "2"))
			.andExpect(status().isOk()).andExpect(header().string("Content-Disposition", "attachment; filename=\"" + fileName + "\"")).andReturn();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testInValidDownload(){		
		try {
			String fileName = "site-map.xml";
			MvcResult result = this.mockMvc.perform(post("/generateSiteMap").param("crawlurl", "http://wiprodigital.com.commmm").param("maxPages", "2"))
			.andExpect(status().isOk()).andExpect(header().string("Content-Disposition", "attachment; filename=\"" + fileName + "\"")).andReturn();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testValidDownload(){		
		try {
			String fileName = "site-map.xml";
			MvcResult result = this.mockMvc.perform(post("/generateSiteMap").param("crawlurl", "http://wiprodigital.com").param("maxPages", "2"))
			.andExpect(status().isOk()).andExpect(header().string("Content-Disposition", "attachment; filename=\"" + fileName + "\"")).andReturn();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
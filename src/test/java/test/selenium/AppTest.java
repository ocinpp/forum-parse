package test.selenium;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	class Thread {
		public String id;
		public String title;
		public String author;
	}
	
	class Post {
		public String id;
		public String title;
		public String threadId;		
		public String author;
		public String message;
		public String date;
		
		public String toString() {
			return 	"ID: " + id + "\n" +
					"Title: " + title + "\n" +
					"Thread ID: " + threadId + "\n" +
					"Author: " + author + "\n" + 
					"Date: " + date + "\n" +
					"Message: " + message;
		}
	}
	
	public void testThread() {
		
	}
	
	/**
	 * Rigourous Test :-)
	 */
	public void testPost() {
//		 WebDriver driver = new FirefoxDriver();
//		 driver.get("http://www.discuss.com.hk/viewthread.php?tid=25680132&extra=page%3D1");
		// System.out.println(driver.getTitle());
		// WebElement e =
		// driver.findElement(By.xpath("//*[@id=\"mainbody\"]/tbody/tr/td/div/table/tbody/tr/td[1]/form[1]//div[1]"));
		// System.out.println(e.getText());
		//
		// String pageSource = driver.getPageSource();
		//
//		 driver.close();

		try {
			File input = new File("C:/temp/test-2.txt");
			Document doc = Jsoup.parse(input, "UTF-8", "");
		
//			Document doc = Jsoup.parse(driver.getPageSource(), "");
			
			System.out.println(doc.select(".pages_btns .pages").select("em").text());
			System.out.println(doc.select(".pages_btns .pages").select("a").size());
			
			Elements elements = doc.getElementsByClass("viewthread");
			
			List<Post> posts = new ArrayList<Post>();
			
			String title = elements.first().select("h1").text();
			
			for (Element element : elements) {
				Post p = new Post();
				
				p.title = title;
				
				String postid = element.select(".postauthor > cite > div").first().attr("id").replaceAll("userinfo", "");
//				System.out.println(postid);
				p.id = postid;
				
				String author = element.select(".postauthor > cite > a").first().text();
//				System.out.println(author);
				p.author = author;
				
				String date = element.select(".postcontent .postinfo ").first().ownText().replaceAll("發表於 ", "");
//				System.out.println(author);
				p.date = date;
				
				String postmessage = element.select("#postorig_" + postid).first().ownText();
//				System.out.println(postmessage);
				p.message = postmessage;				
				
				posts.add(p);
			}
			
			System.out.println(posts);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

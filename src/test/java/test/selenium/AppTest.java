package test.selenium;

import java.net.URL;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import test.ForumTopicParser;

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
	
	public void testThread() throws Exception {
		ForumTopicParser sut = new ForumTopicParser();
		URL testURL = new URL("http://www.dummy-site-for-testing-test-test.com");
		//ForumTopic t = sut.extractTopic(testURL);
		//Assert.assertEquals(80, t.getPosts().size());
		//System.out.println(t.getPosts());
		String json = sut.topic2Json(sut.extractTopic(testURL));
		Assert.assertNotNull(json);
		System.out.println(json);
	}
	
}

package test.selenium;

import java.net.URL;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import test.ForumThread;
import test.ForumThreadParser;

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
		ForumThreadParser sut = new ForumThreadParser();
		URL testURL = new URL("http://www.discuss.com.hk/viewthread.php?tid=25744687&extra=page%3D5");
		//URL testURL = new URL("http://www.dummy-site-for-testing-test-test.com");
		ForumThread t = sut.extractThread(testURL);
		Assert.assertEquals(187, t.getPosts().size());
	}
	
}

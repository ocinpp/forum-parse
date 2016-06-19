package test;

import java.net.URL;
import java.util.List;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import model.ForumTopic;
import parser.DForumTopicParser;

/**
 * Unit test 
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
	
	public void testExtractTopic() throws Exception {
		DForumTopicParser sut = new DForumTopicParser();
		URL testUrl = new URL("http://www.xxxx.com.hk/viewthread.php?tid=25781237");
		//ForumTopic t = sut.extractTopic(testURL);
		//Assert.assertEquals(80, t.getPosts().size());
		//System.out.println(t.getPosts());
		String json = sut.object2Json(sut.extractTopicByUrl(testUrl));
		Assert.assertNotNull(json);
		//System.out.println(json);
	}
	
	public void testExtractTopics() throws Exception {
		DForumTopicParser sut = new DForumTopicParser();
		URL testUrl = new URL("http://www.xxxx.com.hk/forumdisplay.php?fid=40");
		List<ForumTopic> topics = sut.extractTopics(testUrl);
//		for (ForumTopic topic : topics) {
//			System.out.println(sut.topic2Json(topic));
//		}		
		System.out.println(sut.object2Json(topics));
	}
}

package test;

import java.net.URL;
import java.util.List;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import model.ForumTopic;
import parser.BForumTopicParser;

/**
 * Unit test
 */
public class AppTest2 extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest2(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest2.class);
	}

	public void testGetAllPages() throws Exception {
		BForumTopicParser sut = new BForumTopicParser();
		URL testUrl = new URL("http://www.xxxx.com/forum.php?mod=viewthread&tid=17678589&extra=page%3D2");
		List<URL> l = sut.getAllPages(testUrl);
		Assert.assertEquals(3, l.size());
		
		testUrl = new URL("http://www.xxxx.com/forum.php?mod=viewthread&tid=17244812&extra=page%3D3");
		l = sut.getAllPages(testUrl);
		Assert.assertEquals(8, l.size());
		
		testUrl = new URL("http://www.xxxx.com/forum.php?mod=viewthread&tid=17423157&extra=page%3D2");
		l = sut.getAllPages(testUrl);
		Assert.assertEquals(1, l.size());
		
		testUrl = new URL("http://www.xxxx.com/forum.php?mod=viewthread&tid=16513571&extra=page%3D1");
		l = sut.getAllPages(testUrl);
		Assert.assertEquals(121, l.size());		
	}
	
	public void testExtractTopic() throws Exception {
		BForumTopicParser sut = new BForumTopicParser();
		URL testUrl = new URL("http://www.xxxx.com/forum.php?mod=viewthread&tid=17382681&extra=page%3D2");
		ForumTopic t = sut.extractTopicByUrl(testUrl);
		Assert.assertEquals(53, t.getPosts().size());
		String json = sut.object2Json(t);
		Assert.assertNotNull(json);
		//System.out.println(json);
	}
	
	public void testExtractTopics() throws Exception {
		BForumTopicParser sut = new BForumTopicParser();
		URL testUrl = new URL("http://www.xxxx.com/forum.php?mod=forumdisplay&fid=47");
		List<ForumTopic> topics = sut.extractTopics(testUrl);
//		for (ForumTopic topic : topics) {
//			System.out.println(sut.topic2Json(topic));
//		}		
		System.out.println(sut.object2Json(topics));
	}
}

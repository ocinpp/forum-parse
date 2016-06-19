package parser;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Stopwatch;

import model.ForumPost;
import model.ForumTopic;

public class DForumTopicParser {

	private static final int POST_IN_PAGE = 15;
	private static final String PAGE_PARAM = "page";
	private static final String FORUM_NAME = "Forum1";
	private static final String FORUM_CHARSET = "Big5-HKSCS";
	private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36";
	private static final int SLEEP_BETWEEN_TOPICS = 2000;
	private static final int SLEEP_BETWEEN_PAGES = 0;
	
	private static final String FORUM_BASE_URL = "http://www.discuss.com.hk/";
	private static final String USER_URL = "space.php\\?uid=";
	private static final String PATTERN_EDIT_BY = "(?s)\\[ 本帖最後由.*?編輯 ]";
	private static final String DATE_FORMAT_LAST_POST = "yyyy-M-d hh:mm a";
	
	private static final int LAST_POST_DAYS_AGO = 10;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Extract the topics in the page
	 * @param url URL of the board without page number
	 * @return
	 * @throws Exception
	 */
	public List<ForumTopic> extractTopics(URL baseUrl) throws Exception {
		Stopwatch stopwatch = Stopwatch.createStarted();
		
		List<ForumTopic> topics = new ArrayList<ForumTopic>();
		
		// starts from page 1 until last post date is more than x days ago
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DAY_OF_MONTH, -1 * LAST_POST_DAYS_AGO);
		LocalDateTime limitDate = LocalDateTime.of(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		
		int i = 0;
		
		boolean finish = false;
		// get required topics
		while (!finish) {
			i++;
			URL checkURL = new URL(baseUrl + "&" + PAGE_PARAM + "=" + i);
			String content = null;
			
			try {
				content = this.getContentAsString(checkURL, Charset.forName(FORUM_CHARSET));
			} catch (Exception e) {
				logger.info(e.getMessage(), e);
			}
			
			if (content != null) {
				Document doc = Jsoup.parse(content);
				Elements elements = doc.select("div.mainbox.threadlist");
				elements = elements.select(".tsubject");
				
//				List<URL> topicUrls = new ArrayList<URL>();				
				for (Element element : elements) {
					Element p = element.parent().parent().parent();
					String checkId = p.attr("id");
					if (checkId.startsWith("normalthread")) {
						ForumTopic t = new ForumTopic();
//						topicUrls.add(new URL(new URL(FORUM_BASE_URL), element.select("a").attr("href")));
						String id = checkId.replaceAll("normalthread_", "");
						String subject = p.select(".tsubject > a").first().ownText();
						String author = p.select(".author cite a").first().ownText();
						String date = p.select(".author > em").first().ownText();
						String reply = p.select(".nums > strong").first().ownText();
						String view = p.select(".nums > em").first().ownText();
						
						// <span title="2016-5-10 08:32 PM">
						// or
						// 2016-5-9 10:23 PM
						Element lastpostElement = p.select(".lastpost > em > a > span").first();
						String lastpost = null;
						if (lastpostElement != null) {
							lastpost = lastpostElement.attr("title");
						} else {
							lastpost = p.select(".lastpost > em > a").first().ownText();
						}
						
						t.setId(id);
						t.setSubject(subject);
						t.setAuthor(author);
						t.setTopicDate(date);
						t.setReplyCount(new Integer(reply));
						t.setViewCount(new Integer(view));
						t.setLastPost(lastpost);
						t.setUrl((new URL(new URL(FORUM_BASE_URL), element.select("a").attr("href")).toString()));						
						
						// Sample: 2016-5-14 04:25 AM
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_LAST_POST).withLocale(Locale.ENGLISH);						
						
						// break if last post more than x days
						// else add to list											
						
						if (LocalDateTime.parse(lastpost, formatter).isBefore(limitDate)) {
							finish = true;
							break;
						} else {
							topics.add(t);
						}
					}
				}
			} else {
				finish = true;
			}
		}
				
		//		List<ForumTopic> topics = new ArrayList<ForumTopic>();
		//		for (URL topicUrl : topicUrls) {
		//			ForumTopic t = extractTopic(topicUrl);
		//			topics.add(t);
		//			Thread.sleep(SLEEP_BETWEEN_TOPICS);
		//		}
				
		logger.info("Topics to load:\t" + topics.size());
		
		// extract topics
		for (ForumTopic topic : topics) {
			extractTopic(topic);
			Thread.sleep(SLEEP_BETWEEN_TOPICS);
		}
		
		stopwatch.stop();
		logger.info("Total elapsed time (milliseconds):\t" + stopwatch.elapsed(TimeUnit.MILLISECONDS));
		logger.info("Total num of pages:\t" + i);
		logger.info("Total num of topics:\t" + topics.size());
		
		return topics;
	}
	
	/**
	 * Extract the entire topic content with the given URL
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public void extractTopic(ForumTopic topic) throws Exception {
		Stopwatch stopwatch = Stopwatch.createStarted();
		
		List<URL> pages = new ArrayList<URL>();
		pages.addAll(this.getAllPages(new URL(topic.getUrl())));
		List<ForumPost> p = new ArrayList<ForumPost>();
		for (URL page : pages) {
			try {
				p.addAll(this.parsePage(page));
				Thread.sleep(SLEEP_BETWEEN_PAGES);
			} catch (Exception e) {
				logger.error("Error in page (" + topic.getId() + ", " + topic.getSubject() + ", " + page);
			}
		}
		
		topic.setPosts(p);
		
		stopwatch.stop();
		logger.info("Elapsed time (milliseconds) (" + topic.getId() + ", " + topic.getSubject() + ", " + pages.size() + ", " + p.size() + "):\t" + stopwatch.elapsed(TimeUnit.MILLISECONDS));		
	}
	
	/**
	 * Extract the entire topic content with the given URL
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public ForumTopic extractTopicByUrl(URL url) throws Exception {
		List<URL> pages = new ArrayList<URL>();
		pages.addAll(this.getAllPages(url));
		List<ForumPost> p = new ArrayList<ForumPost>();
		for (URL page : pages) {
			p.addAll(this.parsePage(page));			
		}
		ForumTopic t = new ForumTopic();
		t.setPosts(p);
		return t;
	}
	
	public String object2Json(Object t) throws Exception {
		// https://github.com/FasterXML/jackson-databind/
		ObjectMapper mapper = new ObjectMapper();
		
		// to enable standard indentation ("pretty-printing"):
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		// to prevent exception when encountering unknown property:
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		// to allow coercion of JSON empty String ("") to null Object value:
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		
		return mapper.writeValueAsString(t);
	}
	
	/**
	 * With the given URL (first page), parse the content and get all links to the remaining pages 
	 * by calculating the number of pages using the total
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public List<URL> getAllPages(URL url) throws Exception {
		//big5?
		//Document doc = Jsoup.connect(url.toString()).userAgent(USER_AGENT).get();
		
		String content = null;
		try {
			content = this.getContentAsString(url, Charset.forName(FORUM_CHARSET));
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		
		Document doc = Jsoup.parse(content);
		
		List<URL> links = new ArrayList<URL>();
		
		String sTotalMessages = doc.select(".pages_btns .pages").select("em").html().replaceAll("&nbsp;", "");
		int totalMessages = 0;
		try {
			totalMessages = Integer.parseInt(sTotalMessages);
		} catch (NumberFormatException e) {
			//logger.error("Failed to parse message total number: " + url);
			// assume 1 message if the count is not found			
			totalMessages = 1;
		}
		
		int numOfPages = totalMessages / POST_IN_PAGE + ((totalMessages % POST_IN_PAGE > 0) ? 1 : 0);
		
		for (int i = 0; i < numOfPages; i++) {
			URL pageUrl = new URL(url.toString() + "&" + PAGE_PARAM + "=" + (i + 1));
			links.add(pageUrl);
		}
		
		return links;
	}
	
	private String getContentAsString(URL url, Charset charset) throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		String html = null;
		
		try {
			HttpGet httpget = new HttpGet(url.toString());
			httpget.addHeader("User-Agent", USER_AGENT);
			httpclient = HttpClients.createDefault();
			response = httpclient.execute(httpget);
			html = EntityUtils.toString(response.getEntity(), charset);
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return html;
	}
	
	/**
	 * Parse the page and produce a list of ForumPost
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public List<ForumPost> parsePage(URL url) throws Exception {
		Document doc = Jsoup.parse(this.getContentAsString(url, Charset.forName(FORUM_CHARSET)));
		
		// get all elements with class "viewthread"
		Elements elements = doc.getElementsByClass("viewthread");

		List<ForumPost> posts = new ArrayList<ForumPost>();

		String baordName = doc.select("#topbar #topbar_wrapper #topbar_nav .topbar_gid > a").first().select("img").attr("alt");
		String tidHref = doc.select("#topbar #topbar_wrapper #topbar_nav .topbar_tid > a").first().attr("href");
		String tid = tidHref.replaceAll("viewthread\\.php\\?tid=", "");
		
		Element titleElement = elements.first().select("h1").first();
		
		// cannot read title due to access right
		if (titleElement == null) {
			return posts;
		}
		
		String title = titleElement.text();
		
		for (Element element : elements) {
			ForumPost p = new ForumPost(FORUM_NAME);
			p.setBoard(baordName);
			p.setTopicId(tid);
			p.setTopicTitle(title);
			p.setUrl(url.toString());

			String postId = element.select(".postauthor > cite > div").first().attr("id").replaceAll("userinfo", "");
			p.setId(postId);
			
			String author = element.select(".postauthor > cite > a").first().text();
			p.setAuthor(author);
			
			String authorIdUrl = element.select(".postauthor > cite > a").first().attr("href");
			if (authorIdUrl != null) {
				String authorId = authorIdUrl.replaceAll(USER_URL, "");
				p.setAuthorId(authorId);
			}
			

			// need to handle &nbsp; after the date
			String date = element.select(".postcontent .postinfo").first().ownText().replaceAll("     ", "").replaceAll("發表於 ", "");
			p.setPostDate(date);

			Elements postContent = element.select("#postorig_" + postId);
			
			if (!postContent.isEmpty()) {
				String postmessage = element.select("#postorig_" + postId).first().html();
	
				Document postMessageElements = Jsoup.parse(postmessage);
	
				// remove the content in  <div class="quote">
				postMessageElements.select(".quote").remove();
	
				// get the text only
				String messageText = postMessageElements.text();
				
				// remove [ 本帖最後由 xxxx 於 xxxx-x-x xx:xx xx 編輯 ] message
				// match line breaks by (?s)
				messageText = messageText.replaceAll(PATTERN_EDIT_BY, "").trim();
				p.setMessage(messageText);
			}
			posts.add(p);
		}

		return posts;
	}

}

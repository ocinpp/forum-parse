package test;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ForumTopicParser {

	private static final int POST_IN_PAGE = 15;
	private static final String PAGE_PARAM = "page";
	private static final String FORUM_NAME = "Forum1";
	private static final String FORUM_CHARSET = "Big5-HKSCS";
	private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36";
	
	/**
	 * Extract the entire topic content with the given URL
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public ForumTopic extractTopic(URL url) throws Exception {
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
	
	public String topic2Json(ForumTopic t) throws Exception {
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
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public List<URL> getAllPages(URL url) throws Exception {
		//big5?
		Document doc = Jsoup.connect(url.toString()).userAgent("Mozilla").get();
		
		List<URL> links = new ArrayList<URL>();
		
		int totalMessages = Integer.parseInt(doc.select(".pages_btns .pages").select("em").html().replaceAll("&nbsp;", ""));
		int numOfPages = totalMessages / POST_IN_PAGE + 1;
		
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

		String baordName = doc.select("#topbar #topbar_wrapper #topbar_nav .topbar_gid").select("a").select("img").attr("alt");
		String title = elements.first().select("h1").text();
		String tidHref = doc.select("#topbar #topbar_wrapper #topbar_nav .topbar_tid").select("a").attr("href");
		String tid = tidHref.replaceAll("viewthread\\.php\\?tid=", "");
		
		for (Element element : elements) {
			ForumPost p = new ForumPost(FORUM_NAME);
			p.setBoard(baordName);
			p.setTopicId(tid);
			p.setTitle(title);
			p.setTopicUrl(url.toString());

			String postId = element.select(".postauthor > cite > div").first().attr("id").replaceAll("userinfo", "");
			p.setId(postId);
			
			String author = element.select(".postauthor > cite > a").first().text();
			p.setAuthor(author);

			// need to handle &nbsp; after the date
			String date = element.select(".postcontent .postinfo ").first().ownText().replaceAll("     ", "").replaceAll("發表於 ", "");
			p.setDate(date);

			Elements postContent = element.select("#postorig_" + postId);
			
			if (!postContent.isEmpty()) {
				String postmessage = element.select("#postorig_" + postId).first().html();
	
				Document postMessageElements = Jsoup.parse(postmessage);
	
				// remove the content in  <div class="quote">
				postMessageElements.select(".quote").remove();
	
				// get the text only
				String messageText = postMessageElements.text();
				
				// remove [ 本帖最後由 xxxx 於 xxxx-x-x xx:xx xx 編輯 ] message
				messageText = messageText.replaceAll("(?s)\\[ 本帖最後由.*?編輯 ]", "").trim();
				p.setMessage(messageText);
			}
			posts.add(p);
		}

		return posts;
	}

}

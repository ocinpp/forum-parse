package test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ForumThreadParser {

	private static final int POST_IN_PAGE = 15;
	private static final String PAGE_PARAM = "page";
	
	/**
	 * Extract the entired thread content with the given URL
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public ForumThread extractThread(URL url) throws Exception {
		List<URL> pages = new ArrayList<URL>();
//		pages.add(url);
		pages.addAll(this.getAllPages(url));
		List<ForumPost> p = new ArrayList<ForumPost>();
		for (URL page : pages) {
			p.addAll(this.parsePage(page));
		}
		ForumThread t = new ForumThread();
		t.setPosts(p);
		return t;
	}
	
	/**
	 * With the given URL (first page), parse the content and get all links to the remaining pages
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public List<URL> getAllPages(URL url) throws Exception {
		Document doc = Jsoup.connect(url.toString()).userAgent("Mozilla").get();
//		Elements elements = doc.select(".pages_btns .pages").select("a").not(".next .last");
//		List<URL> links = new ArrayList<URL>();
//		for (Element element : elements) {
//			String href = element.absUrl("href");
//			links.add(new URL(href));
//		}
		
		List<URL> links = new ArrayList<URL>();
		
		int totalMessages = Integer.parseInt(doc.select(".pages_btns .pages").select("em").html().replaceAll("&nbsp;", ""));
		int numOfPages = totalMessages / POST_IN_PAGE + 1;
		
		for (int i = 0; i < numOfPages; i++) {
			URL pageUrl = new URL(url.toString() + "&" + PAGE_PARAM + "=" + (i + 1));
			links.add(pageUrl);
		}
		
		return links;
	}
	
	/**
	 * Parse the page and produce a list of ForumPost
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public List<ForumPost> parsePage(URL url) throws Exception {
		Document doc = Jsoup.connect(url.toString()).userAgent("Mozilla").get();

//		int totalMessages = Integer.parseInt(doc.select(".pages_btns .pages").select("em").html().replaceAll("&nbsp;", ""));
//		int totalViewablePages = doc.select(".pages_btns .pages").select("a").size();

		// get all elements with class "viewthread"
		Elements elements = doc.getElementsByClass("viewthread");

		List<ForumPost> posts = new ArrayList<ForumPost>();

		String title = elements.first().select("h1").text();
		String tidHref = doc.select("#topbar #topbar_wrapper #topbar_nav .topbar_tid").select("a").attr("href");
		String tid = tidHref.replaceAll("viewthread\\.php\\?tid=", "");
		
		for (Element element : elements) {
			ForumPost p = new ForumPost();
			p.setThreadId(tid);
			p.setTitle(title);

			String postId = element.select(".postauthor > cite > div").first().attr("id").replaceAll("userinfo", "");
			p.setId(postId);
			
			String author = element.select(".postauthor > cite > a").first().text();
			p.setAuthor(author);

			String date = element.select(".postcontent .postinfo ").first().ownText().replaceAll("發表於 ", "");
			p.setDate(date);

			Elements postContent = element.select("#postorig_" + postId);
			
			if (!postContent.isEmpty()) {
				String postmessage = element.select("#postorig_" + postId).first().html();
				// System.out.println(postmessage);
	
				Document postMessageElements = Jsoup.parse(postmessage);
	
				// remove the content in  <div class="quote">
				postMessageElements.select(".quote").remove();
	
				// get the text only
				String messageText = postMessageElements.text();
				
				// remove [ 本帖最後由 xxxx 於 xxxx-x-x xx:xx xx 編輯 ] message
				messageText = messageText.replaceAll("(?s)\\[ 本帖最後由.*?編輯 ]", "");
				p.setMessage(messageText);
			}
			
			posts.add(p);
		}

		return posts;
	}

}

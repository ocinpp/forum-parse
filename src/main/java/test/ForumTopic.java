package test;

import java.util.Collections;
import java.util.List;

public class ForumTopic {

	private String author;
	private String topicDate;
	private Integer replyCount;
	private Integer viewCount;
	private String lastPost;
	private String url;
	private List<ForumPost> posts;
	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTopicDate() {
		return topicDate;
	}
	public void setTopicDate(String topicDate) {
		this.topicDate = topicDate;
	}
	public Integer getReplyCount() {
		return replyCount;
	}
	public void setReplyCount(Integer replyCount) {
		this.replyCount = replyCount;
	}
	public Integer getViewCount() {
		return viewCount;
	}
	public void setViewCount(Integer viewCount) {
		this.viewCount = viewCount;
	}
	public String getLastPost() {
		return lastPost;
	}
	public void setLastPost(String lastPost) {
		this.lastPost = lastPost;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<ForumPost> getPosts() {
		return Collections.unmodifiableList(posts);
	}
	public void setPosts(List<ForumPost> posts) {
		this.posts = posts;
	}
}

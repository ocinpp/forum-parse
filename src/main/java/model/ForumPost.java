package model;

public class ForumPost {

	private String id;
	private String forum;
	private String board;
	private String topicId;
	private String topicTitle;	
	private String url;
	private String author;
	private String authorId;
	private String message;
	private String postDate;
	
	public ForumPost(String forum) {
		this.forum = forum;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTopicTitle() {
		return topicTitle;
	}

	public void setTopicTitle(String title) {
		this.topicTitle = title;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPostDate() {
		return postDate;
	}

	public void setPostDate(String postDate) {
		this.postDate = postDate;
	}

	public String getBoard() {
		return board;
	}

	public void setBoard(String board) {
		this.board = board;
	}

	public String getForum() {
		return forum;
	}

	public void setForum(String forum) {
		this.forum = forum;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String toString() {
		return 	"Post Id: " + id + "\n" +
				"Title: " + topicTitle + "\n" +
				"Forum: " + forum + "\n" +
				"Board: " + board + "\n" +
				"Topic Id: " + topicId + "\n" +
				"Author: " + author + "\n" + 
				"Date: " + postDate + "\n" +
				"Message: " + message;
	}

}

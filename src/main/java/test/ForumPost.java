package test;

public class ForumPost {

	private String id;
	private String title;
	private String forum;
	private String board;
	private String topicId;
	private String topicUrl;
	private String author;
	private String message;
	private String date;
	
	public ForumPost(String forum) {
		this.forum = forum;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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
	
	public String getTopicUrl() {
		return topicUrl;
	}

	public void setTopicUrl(String topicUrl) {
		this.topicUrl = topicUrl;
	}

	public String toString() {
		return 	"Post Id: " + id + "\n" +
				"Title: " + title + "\n" +
				"Forum: " + forum + "\n" +
				"Board: " + board + "\n" +
				"Topic Id: " + topicId + "\n" +
				"Author: " + author + "\n" + 
				"Date: " + date + "\n" +
				"Message: " + message;
	}
}

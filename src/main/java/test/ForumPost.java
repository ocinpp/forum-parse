package test;

public class ForumPost {

	private String postId;
	private String title;
	private String threadId;		
	private String author;
	private String message;
	private String date;
	
	public String getId() {
		return postId;
	}

	public void setId(String id) {
		this.postId = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
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

	public String toString() {
		return 	"ID: " + postId + "\n" +
				"Title: " + title + "\n" +
				"Thread ID: " + threadId + "\n" +
				"Author: " + author + "\n" + 
				"Date: " + date + "\n" +
				"Message: " + message;
	}
	
}

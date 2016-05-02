package test;

import java.util.Collections;
import java.util.List;

public class ForumThread {

	private List<ForumPost> posts;
	
	public List<ForumPost> getPosts() {
		return Collections.unmodifiableList(posts);
	}
	public void setPosts(List<ForumPost> posts) {
		this.posts = posts;
	}
}

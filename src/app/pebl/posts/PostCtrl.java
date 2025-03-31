package app.pebl.posts;

import app.pebl.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.io.IOException;

public class PostCtrl extends VBox {
	@FXML Label lblUsername;
	@FXML Text txtBody;
	@FXML Label lblSkips;
	@FXML Button btnSkip;
	private Post post;

	public void skip() {
		//add skip to post and update label
		System.out.println("add skip");
	}

	public void setPost(Post newPost) {
		//update fields
		lblUsername.setText(newPost.getSender());
		txtBody.setText(newPost.getContent());
		lblSkips.setText(Integer.toString(newPost.getSkips()));

		//link post object
		this.post = newPost;
	}

	public void setPost(String sender, String content, int skips) {
		//update fields
		lblUsername.setText(sender);
		txtBody.setText(content);
		lblSkips.setText(Integer.toString(skips));
	}

	public void showProfile() throws IOException {
		//open new profile window with user
		Main.initProfile(post.getSender()).show();
	}
}
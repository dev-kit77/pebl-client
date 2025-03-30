package app.pebl.posts;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class PostCtrl extends AnchorPane {
	@FXML Label lblDisplayname;
	@FXML Label lblUsername;
	@FXML Text txtBody;
	@FXML ToggleButton btnSkip;

	public void toggleSkip() {
		if (btnSkip.isSelected()) {
			//add skip to post
			System.out.println("add skip");
		}
		else {
			//remove skip from post
			System.out.println("remove skip");
		}
	}

	public void setPost(Post post) {
		//lblDisplayname.setText(post.getDisplayname);
		lblUsername.setText(post.getSender());
		txtBody.setText(post.getContent());
	}

	public void setPost(String displayname, String sender, String content) {
		lblDisplayname.setText(displayname);
		lblUsername.setText(sender);
		txtBody.setText(content);
	}
}
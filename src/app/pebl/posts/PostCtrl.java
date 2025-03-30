package app.pebl.posts;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class PostCtrl extends AnchorPane {
	@FXML Label lblUsername;
	@FXML Text txtBody;
	@FXML Label lblSkips;
	@FXML ToggleButton btnSkip;

	public void toggleSkip() {
		if (btnSkip.isSelected()) {
			//add skip to post and update label
			System.out.println("add skip");
		}
		else {
			//remove skip from post and update label
			System.out.println("remove skip");
		}
	}

	public void setPost(Post post) {
		//update fields
		lblUsername.setText(post.getSender());
		txtBody.setText(post.getContent());
		lblSkips.setText(Integer.toString(post.getSkips()));
	}

	public void setPost(String sender, String content, int skips) {
		//update fields
		lblUsername.setText(sender);
		txtBody.setText(content);
		lblSkips.setText(Integer.toString(skips));
	}


}
package app.pebl.profile;

import app.pebl.Controller;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ProfileCtrl extends Controller {
	User displayUser;

	@FXML
	Label lblUsername;
	@FXML
	Label lblStatus;
	@FXML
	Label lblFollowers;
	@FXML
	Label lblFollowing;
	@FXML
	Label lblSkips;
	@FXML
	Label lblAge;
	@FXML
	Label lblGender;
	@FXML
	VBox feedUserPosts;

	public void refresh() {
		//update fields
		lblUsername.setText(displayUser.getUsername());
		lblStatus.setText("\"" + displayUser.getStatus() + "\"");
		lblFollowers.setText(displayUser.getFollowers().toString() + " Followers");
		lblFollowing.setText(displayUser.getFollowing().toString() + " Following");
		lblSkips.setText(displayUser.getSkips() + " Skips");
		lblAge.setText("Age " + displayUser.getAge());

		//set swedish gender (dont ask)
		if (displayUser.getGender()) {
			lblGender.setText("Gendered");
		}
		else {
			lblGender.setText("No Gender");
		}

		//get user posts then add to feed
		Task<Void> task = new Task<>() {
			@Override public Void call() {
				return null;
			};
		};
	}

	public void setUser(User newUser) {
		displayUser = newUser;
		this.refresh();
	}
}

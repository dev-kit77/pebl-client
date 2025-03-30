package app.pebl.profile;

import app.pebl.Config;
import app.pebl.Controller;
import app.pebl.Main;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

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
	@FXML
	MenuItem edit;

	public void refresh() {
		//get user data
		Task<Void> userRefresh = new Task<>() {
			@Override public Void call() {
				//code to do this goes here
				return null;
			};
		};

		//get user posts from server
		Task<Void> profileRefresh = new Task<>() {
			@Override public Void call() {
				//code to do this goes here
				return null;
			};
		};

		//run tasks
		Main.getExecutor().execute(userRefresh);
		Main.getExecutor().execute(profileRefresh);

		//update fields
		lblUsername.setText(displayUser.getUsername());
		lblStatus.setText("\"" + displayUser.getStatus() + "\"");
		lblFollowers.setText(displayUser.getFollowers().size() + " Followers");
		lblFollowing.setText(displayUser.getFollowing().size() + " Following");
		lblSkips.setText(displayUser.getSkips() + " Skips");
		lblAge.setText("Age " + displayUser.getAge());

		//set swedish gender (dont ask)
		if (displayUser.getGender()) {
			lblGender.setText("Gendered");
		}
		else {
			lblGender.setText("No Gender");
		}

		//add posts to Vbox here
	}

	public void handleEdit() throws IOException {
		Stage signup = new Stage();
		signup.setTitle("Sign Up");
		signup.setScene(new Scene(Main.getFXML("edit").load()));
		signup.initOwner(layoutParent.getScene().getWindow());
		signup.initModality(Modality.APPLICATION_MODAL);
		signup.showAndWait();

		this.refresh();
	}

	public void setUser(User newUser) {
		displayUser = newUser;

		//hide/show edit profile button
		if (displayUser.equals(Config.getInstance().getCurrentUser())) {
			edit.setVisible(true);
		}
		else {
			edit.setVisible(false);
		}

		//refresh data
		this.refresh();
	}

	public void showProfile() throws IOException {
		Main.initProfile(displayUser).show();
	}

	public void showConnections() throws IOException {
		Main.initConnections(displayUser).show();
	}

	public void showLeaderboard() throws IOException {

	}
}

package app.pebl.profile;

import app.pebl.Config;
import app.pebl.Controller;
import app.pebl.Main;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
	@FXML
	MenuItem follow;
	@FXML
	Button btnFollow;
	private boolean followed;

	public void refresh() {
		//get user data
		Task<Void> profileRefresh = new Task<>() {
			@Override public Void call() {
				try {
					displayUser = Main.getProfile(displayUser.getUsername());
				} catch (Exception e) {
					//print stack trace to console
					e.printStackTrace();

					//update GUI
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							//show general error
							showError("Exception in pebl client", e.getMessage());

							//exit program
							Platform.exit();
						}
					});
				}

				//update gui
				Platform.runLater(new Runnable() {
					@Override public void run() {
						//update fields
						lblUsername.setText(displayUser.getUsername());

						//check if user has status
						if (displayUser.getStatus() != null) {
							//show label
							lblStatus.setVisible(true);

							//set status
							lblStatus.setText("\"" + displayUser.getStatus() + "\"");
						}
						else {
							//hide label
							lblStatus.setVisible(false);
						}

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

						if (displayUser.getFollowers().contains(displayUser.getUsername())) {
							//update buttons
							btnFollow.setText("Unfollow");
							follow.setText("Unfollow");

							//update variable
							followed = true;
						}
						else {
							//update buttons
							btnFollow.setText("Follow");
							follow.setText("Follow");

							//update variable
							followed = false;
						}
					}
				});

				//empty return
				return null;
			}
		};

		//run tasks
		Main.getExecutor().execute(profileRefresh);
	}

	public void handleEdit() throws IOException {
		Stage signup = new Stage();
		signup.setTitle("Edit Profile");
		signup.setScene(new Scene(Main.getFXML("edit").load()));
		signup.initOwner(layoutParent.getScene().getWindow());
		signup.initModality(Modality.APPLICATION_MODAL);
		signup.showAndWait();

		//refresh profile
		this.refresh();
	}

	public void setUser(User newUser) {
		displayUser = newUser;

		//hide/show edit profile button
		if (displayUser.getUsername().equals(Config.getInstance().getCurrentUser().getUsername())) {
			//current user
			edit.setVisible(true);
			follow.setVisible(false);
			btnFollow.setVisible(false);
		}
		else {
			//other user
			edit.setVisible(false);
			follow.setVisible(true);
			btnFollow.setVisible(true);
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
		Main.initLeaderboard(displayUser).show();
	}

	public void toggleFollow(ActionEvent event) {
		if (followed) {
			btnFollow.setText("Follow");
			follow.setText("Follow");
			followed = false;
		}
		else {
			btnFollow.setText("Unfollow");
			follow.setText("Unfollow");
			followed = true;
		}

		//set user follow status

		refresh();
	}


}

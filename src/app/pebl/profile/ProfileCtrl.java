package app.pebl.profile;

import app.pebl.util.Config;
import app.pebl.util.Controller;
import app.pebl.Main;
import app.pebl.data.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfileCtrl extends Controller {
	//fxml imports
	@FXML private Label lblUsername;
	@FXML private Label lblStatus;
	@FXML private Label lblFollowers;
	@FXML private Label lblFollowing;
	@FXML private Label lblSkips;
	@FXML private Label lblAge;
	@FXML private Label lblGender;
	@FXML private VBox feedUserPosts;
	@FXML private MenuItem edit;
	@FXML private MenuItem follow;
	@FXML private Button btnFollow;
	@FXML private MenuItem logout;

	//class fields
	User displayUser;

	public void refresh() {
		//get user data
		Task<Void> profileRefresh = new Task<>() {
			@Override public Void call() {
				//refresh current user
				refreshUser();

				try {
					displayUser = Main.getProfile(displayUser.getUsername());
				} catch (Exception e) {
					//print stack trace to console
					e.printStackTrace();

					//update GUI
					Platform.runLater(() -> {
						//show general error
						showError("Exception in pebl client", e.getMessage());

						//exit program
						Platform.exit();
					});
				}

				if (displayUser != null) {
					//update gui
					Platform.runLater(() -> {
						//update fields
						lblUsername.setText(displayUser.getUsername());

						//check if user has status
						if (displayUser.getStatus() != null && !displayUser.getStatus().isEmpty()) {
							//show label
							lblStatus.setVisible(true);

							//set status
							lblStatus.setText("\"" + displayUser.getStatus() + "\"");
						} else {
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
						} else {
							lblGender.setText("No Gender");
						}

						//update follow button
						if (Config.getInstance().getCurrentUser().getFollowers().contains(displayUser.getUsername())) {
							//update buttons
							btnFollow.setText("Unfollow");
							follow.setText("Unfollow");
						} else {
							//update buttons
							btnFollow.setText("Follow");
							follow.setText("Follow");
						}
					});
				}
				//null return from server
				else {
					Platform.runLater(() -> {
						//show error as no profile retrieved
						showError("Profile Error", "Error fetching Profile from Server. Please try again later.");
					});
				}

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
		signup.initModality(Modality.WINDOW_MODAL);
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
			logout.setVisible(true);
			btnFollow.setVisible(false);
		}
		else {
			//other user
			edit.setVisible(false);
			follow.setVisible(true);
			logout.setVisible(false);
			btnFollow.setVisible(true);
		}

		//refresh data
		this.refresh();
	}

	public void toggleFollow() {
		//get user data
		Task<Void> userRefresh = new Task<>() {
			@Override
			public Void call() {
				boolean success = false;

				try {
					//send follow to server
					success = Main.follow(displayUser.getUsername());
				} catch (Exception e) {
					//print stack to console
					e.printStackTrace();

					Platform.runLater(() -> {
						//show general error
						showError("Exception in pebl client", e.getMessage());

						//exit program
						Platform.exit();
					});
				}

				//if follow was succesful
				if (success) {
					Platform.runLater(() -> refresh());
				}

				//return to end task
				return null;
			}
		};

		Main.getExecutor().execute(userRefresh);
	}

	public void showProfile() {
		try {
			Main.initProfile(displayUser).show();
		} catch (Exception e) {
			//print stack to console
			e.printStackTrace();

			//show general error
			showError("Exception in pebl client", e.getMessage());

			//exit program
			Platform.exit();
		}
	}

	public void showConnections() {
		try {
			Main.initConnections(displayUser).show();
		} catch (Exception e) {
			//print stack to console
			e.printStackTrace();

			//show general error
			showError("Exception in pebl client", e.getMessage());

			//exit program
			Platform.exit();
		}
	}

	public void showLeaderboard() {
		try {
			Main.initLeaderboard(displayUser).show();
		} catch (Exception e) {
			//print stack to console
			e.printStackTrace();

			//show general error
			showError("Exception in pebl client", e.getMessage());

			//exit program
			Platform.exit();
		}
	}
}

package app.pebl.connections;

import app.pebl.Config;
import app.pebl.Controller;
import app.pebl.Main;
import app.pebl.profile.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.ArrayList;

public class ConnectionsCtrl extends Controller {
	private User displayUser;

	@FXML VBox feedMutuals;
	@FXML VBox feedFollowers;
	@FXML VBox feedFollowing;
	@FXML Label lblCurrUsername;
	@FXML Label lblCurrSkips;
	@FXML Label lblCurrStatus;
	@FXML
	MenuItem logout;

	public void refresh() {
		//get user data
		Task<Void> userRefresh = new Task<>() {
			@Override public Void call() {
				try {
					//refresh display user from server
					displayUser = Main.getProfile(displayUser.getUsername());

					//update gui
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							//update labels
							lblCurrUsername.setText(displayUser.getUsername());
							lblCurrSkips.setText("(" + displayUser.getSkips() + " Skips)");

							//check if user has status
							if (displayUser.getStatus() != null && !displayUser.getStatus().isEmpty()) {
								//show label
								lblCurrStatus.setVisible(true);

								//set status
								lblCurrStatus.setText("\"" + displayUser.getStatus() + "\"");
							}
							else {
								//hide label
								lblCurrStatus.setVisible(false);
							}

							//clear feeds
							feedMutuals.getChildren().clear();
							feedFollowers.getChildren().clear();
							feedFollowing.getChildren().clear();
						}
					});
				} catch (Exception e) {
					//print stack to console
					e.printStackTrace();

					//show error message
					showError("Exception in pebl client", e.getMessage());
				}

				//get connections lists
				ArrayList<String> mutuals = displayUser.getMutuals();
				ArrayList<String> following = displayUser.getFollowing();
				ArrayList<String> followers = displayUser.getFollowers();

				//loop for all followers
				for (String follower : followers) {
					//print to log
					System.out.println("Getting: " + follower);

					try {
						//get follower data
						User addUsr = Main.getProfile(follower);

						//check if response succeeded
						if (addUsr != null) {
							//update gui
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									try {
										//attempt to add card
										addCard(feedFollowers, addUsr);
									} catch (Exception e) {
										//print stack to console
										e.printStackTrace();

										//show error message
										showError("Exception in pebl client", e.getMessage());
									}
								}
							});
						}
					} catch (Exception e) {
						//print stack to console
						e.printStackTrace();

						//show error message
						showError("Exception in pebl client", e.getMessage());
					}
				}

				//loop for all following
				for (String followed : following) {
					//print to log
					System.out.println("Getting: " + followed);

					try {
						//get follower data
						User addUsr = Main.getProfile(followed);

						//check if response succeeded
						if (addUsr != null) {
							//update gui
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									try {
										//attempt to add card
										addCard(feedFollowing, addUsr);
									} catch (Exception e) {
										//print stack to console
										e.printStackTrace();

										//show error message
										showError("Exception in pebl client", e.getMessage());
									}
								}
							});
						}
					} catch (Exception e) {
						//print stack to console
						e.printStackTrace();

						//show error message
						showError("Exception in pebl client", e.getMessage());
					}
				}

				//loop for all mutuals
				for (String moot : mutuals) {
					//print to log
					System.out.println("Getting: " + moot);

					try {
						//get follower data
						User addUsr = Main.getProfile(moot);

						//check if response succeeded
						if (addUsr != null) {
							//update gui
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									try {
										//attempt to add card
										addCard(feedMutuals, addUsr);
									} catch (Exception e) {
										//print stack to console
										e.printStackTrace();

										//show error message
										showError("Exception in pebl client", e.getMessage());
									}
								}
							});
						}
					} catch (Exception e) {
						//print stack to console
						e.printStackTrace();

						//show error message
						showError("Exception in pebl client", e.getMessage());
					}
				}

				//end task with null return
				return null;
			};
		};

		//run thread
		Main.getExecutor().execute(userRefresh);
	}

	public void setUser(User newUser) {
		//set user
		displayUser = newUser;

		//hide/show logout
		if (displayUser.getUsername().equals(Config.getInstance().getCurrentUser().getUsername())) {
			//current user
			logout.setVisible(true);
		}
		else {
			//other user
			logout.setVisible(false);
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

	public void addCard(VBox list, User addUser) throws IOException {
		//get fxml loader
		FXMLLoader loader = Main.getFXML("miniProfile");

		//create new hbox
		loader.setRoot(new HBox());

		//add to vbox
		list.getChildren().add(loader.load());

		//get controller and inject data
		CardCtrl controller = (CardCtrl) loader.getController();
		controller.setUser(addUser);
	}
}

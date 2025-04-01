package app.pebl.connections;

import app.pebl.util.Config;
import app.pebl.util.Controller;
import app.pebl.Main;
import app.pebl.data.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.ArrayList;

public class ConnectionsCtrl extends Controller {
	//fxml elements
	@FXML private VBox feedMutuals;
	@FXML private VBox feedFollowers;
	@FXML private VBox feedFollowing;
	@FXML private Label lblCurrUsername;
	@FXML private Label lblCurrSkips;
	@FXML private Label lblCurrStatus;
	@FXML private TitledPane lblMutuals;
	@FXML private MenuItem logout;
	@FXML private MenuItem leaderboard;

	//class fields
	private User displayUser;

	public void refresh() {
		//get user data
		Task<Void> userRefresh = new Task<>() {
			@Override public Void call() {
				//refresh current user
				refreshUser();

				try {
					//refresh display user from server
					displayUser = Main.getProfile(displayUser.getUsername());

					//update gui
					Platform.runLater(() -> {
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

						if (!displayUser.equals(Config.getInstance().getCurrentUser())) {
							lblMutuals.setText("Shared Follows");
						}

						//clear feeds
						feedMutuals.getChildren().clear();
						feedFollowers.getChildren().clear();
						feedFollowing.getChildren().clear();
					});

					if (!displayUser.equals(Config.getInstance().getCurrentUser())) {
						//set mutuals list
						displayUser.setMutuals(Config.getInstance().getCurrentUser());
					} else {
						//set sharedFollows list
						displayUser.setSharedFollows(Config.getInstance().getCurrentUser());
					}
				} catch (Exception e) {
					//print stack to console
					e.printStackTrace();

					//update GUI
					Platform.runLater(() -> {
						//show general error
						showError("Exception in pebl client", e.getMessage());

						//exit program
						Platform.exit();
					});
				}

				//get connections lists
				ArrayList<String> mutuals = displayUser.getMutuals();
				ArrayList<String> following = displayUser.getFollowing();
				ArrayList<String> followers = displayUser.getFollowers();

				//check if loop needs run
				addCards(mutuals, feedMutuals);

				//check if loop needs run
				addCards(following, feedFollowing);

				//check if loop needs run
				addCards(followers, feedFollowers);

				//end task with null return
				return null;
			}
		};

		//run thread
		Main.getExecutor().execute(userRefresh);
	}

	private void addCards(ArrayList<String> usernames, VBox feed) {
		if (usernames != null) {
			//get last user
			final String last = usernames.getLast();

			//loop for all mutuals
			for (String username : usernames) {
				//print to log
				System.out.println("Getting: " + username);

				try {
					//get follower data
					User addUsr = Main.getProfile(username);

					//check if response succeeded
					if (addUsr != null) {
						//update gui
						Platform.runLater(() -> {
							try {
								//attempt to add card
								addCard(feed, addUsr);

								//add separator if not last post
								if (!addUsr.getUsername().equals(last)) {
									feed.getChildren().add(new Separator());
								}
							} catch (Exception e) {
								//print stack to console
								e.printStackTrace();

								//update GUI
								Platform.runLater(() -> {
									//show general error
									showError("Exception in pebl client", e.getMessage());

									//exit program
									Platform.exit();
								});
							}
						});
					}
				} catch (Exception e) {
					//print stack to console
					e.printStackTrace();

					//update GUI
					Platform.runLater(() -> {
						//show general error
						showError("Exception in pebl client", e.getMessage());

						//exit program
						Platform.exit();
					});
				}
			}
		}
	}

	public void setUser(User newUser) {
		//set user
		displayUser = newUser;

		//hide/show items
		if (displayUser.getUsername().equals(Config.getInstance().getCurrentUser().getUsername())) {
			logout.setVisible(true);
			leaderboard.setVisible(true);
		}
		else {
			logout.setVisible(false);
			leaderboard.setVisible(false);
		}

		//refresh data
		this.refresh();
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

	public void addCard(VBox list, User addUser) throws IOException {
		//get fxml loader
		FXMLLoader loader = Main.getFXML("card");

		//create new hbox
		loader.setRoot(new HBox());

		//add to vbox
		list.getChildren().add(loader.load());

		//get controller and inject data
		CardCtrl controller = (CardCtrl) loader.getController();
		controller.setUser(addUser);
	}
}

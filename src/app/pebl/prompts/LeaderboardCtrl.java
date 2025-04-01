package app.pebl.prompts;

import app.pebl.util.Config;
import app.pebl.util.Controller;
import app.pebl.Main;
import app.pebl.connections.CardCtrl;
import app.pebl.data.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.io.IOException;
import java.util.ArrayList;

public class LeaderboardCtrl extends Controller {
	//fxml elements
	@FXML private Text lblCurrUsername;
	@FXML private Text lblCurrSkips;
	@FXML private Text lblCurrStatus;
	@FXML private VBox feedBoard;
	@FXML private MenuItem logout;

	//class fields
	private User displayUser;

	public void refresh() {
		//get user data
		Task<Void> leaderboardRefresh = new Task<>() {
			@Override public Void call() {
				//update GUI with wait cursor
				Platform.runLater(() -> {
					//show general error
					layoutParent.getScene().setCursor(Cursor.WAIT);
				});

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

				//update gui
				Platform.runLater(() -> {
					//update fields
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

					//clear current board
					feedBoard.getChildren().clear();
				});

				//init leaderboard
				ArrayList<User> topUsers = null;

				try {
					//get leaderboard
					topUsers = Main.leaderboard();
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

				//check if fetch succeeded
				if (topUsers != null) {
					//get last user in list
					final User last = topUsers.getLast();

					//loop for all users on board
					for (User addUsr : topUsers) {
						Platform.runLater(() -> {
							try {
								//attempt to add card
								addCard(feedBoard, addUsr);

								//check if not last
								if (!addUsr.equals(last)) {
									//add separator
									feedBoard.getChildren().add(new Separator());
								}
							} catch (Exception e) {
								//print stack to console
								e.printStackTrace();

								//show error message
								showError("Exception in pebl client", e.getMessage());

								//exit app
								Platform.exit();
							}
						});
					}
				}
				//null return from server
				else {
					Platform.runLater(() -> {
						//show error as no leaderboard retrieved
						showError("Leaderboard Error", "Error fetching Leaderboard from Server. Please try again later.");
					});
				}

				//update GUI with default cursor
				Platform.runLater(() -> {
					//show general error
					layoutParent.getScene().setCursor(Cursor.DEFAULT);
				});

				//end thread
				return null;
			}
		};

		//run task
		Main.getExecutor().execute(leaderboardRefresh);
	}

	public void addCard(VBox list, User addUser) throws IOException {
		//get fxml loader
		FXMLLoader loader = Main.getFXML("card");

		loader.setRoot(new HBox());

		//load into vbox
		list.getChildren().add(loader.load());

		//get controller and inject data
		CardCtrl controller = (CardCtrl) loader.getController();
		controller.setUser(addUser);
	}

	public void setUser(User newUser) {
		//set user
		displayUser = newUser;

		//hide/show logout
		logout.setVisible(displayUser.getUsername().equals(Config.getInstance().getCurrentUser().getUsername()));

		//refresh
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
}

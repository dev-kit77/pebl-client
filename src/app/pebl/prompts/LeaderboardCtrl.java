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
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;

public class LeaderboardCtrl extends Controller {
	@FXML Text lblCurrUsername;
	@FXML Text lblCurrSkips;
	@FXML Text lblCurrStatus;
	@FXML VBox feedBoard;
	@FXML MenuItem logout;

	private User currUser;

	@Override
	public void refresh() {
		//refresh current user
		super.refresh();

		//get user data
		Task<Void> leaderboardRefresh = new Task<>() {
			@Override public Void call() {
				try {
					currUser = Main.getProfile(currUser.getUsername());
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
						lblCurrUsername.setText(currUser.getUsername());
						lblCurrSkips.setText("(" + currUser.getSkips() + " Skips)");

						//check if user has status
						if (currUser.getStatus() != null && !currUser.getStatus().isEmpty()) {
							//show label
							lblCurrStatus.setVisible(true);

							//set status
							lblCurrStatus.setText("\"" + currUser.getStatus() + "\"");
						}
						else {
							//hide label
							lblCurrStatus.setVisible(false);
						}
					}
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

				//check if fetch succeeded
				if (topUsers != null) {
					//get last user in list
					final User last = topUsers.getLast();

					//loop for all users on board
					for (User addUsr : topUsers) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								try {
									//attempt to add card
									addCard(feedBoard, addUsr);
								} catch (Exception e) {
									//print stack to console
									e.printStackTrace();

									//show error message
									showError("Exception in pebl client", e.getMessage());

									//exit app
									Platform.exit();
								}
							}
						});
					}
				}

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
		currUser = newUser;

		//hide/show logout
		if (currUser.getUsername().equals(Config.getInstance().getCurrentUser().getUsername())) {
			//current user
			logout.setVisible(true);
		}
		else {
			//other user
			logout.setVisible(false);
		}

		//refresh
		this.refresh();
	}

	public void showProfile() throws IOException {
		Main.initProfile(currUser).show();
	}

	public void showConnections() throws IOException {
		Main.initConnections(currUser).show();
	}

	public void showLeaderboard() throws IOException {
		Main.initLeaderboard(currUser).show();
	}

}

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

	@Override
	public void refresh() {
		//refresh current user
		super.refresh();

		//get user data
		Task<Void> userRefresh = new Task<Void>() {
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

				//get connections lists
				ArrayList<String> mutuals = displayUser.getMutuals();
				ArrayList<String> following = displayUser.getFollowing();
				ArrayList<String> followers = displayUser.getFollowers();

				//check if loop needs run
				if (mutuals != null) {
					//get last user
					final String lastMutual = mutuals.getLast();

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

											//add separator if not last post
											if (!addUsr.getUsername().equals(lastMutual)) {
												feedMutuals.getChildren().add(new Separator());
											}
										} catch (Exception e) {
											//print stack to console
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
									}
								});
							}
						} catch (Exception e) {
							//print stack to console
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
					}
				}

				//check if loop needs run
				if (following != null) {
					//get last follow
					final String lastFollow = following.getLast();

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

											//add separator if not last post
											if (!addUsr.getUsername().equals(lastFollow)) {
												feedFollowing.getChildren().add(new Separator());
											}
										} catch (Exception e) {
											//print stack to console
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
									}
								});
							}
						} catch (Exception e) {
							//print stack to console
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
					}
				}

				//check if loop needs run
				if (followers != null) {
					//get last follower
					final String lastFollower = followers.getLast();

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

											//add separator if not last post
											if (!addUsr.getUsername().equals(lastFollower)) {
												feedFollowers.getChildren().add(new Separator());
											}
										} catch (Exception e) {
											//print stack to console
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
									}
								});
							}
						} catch (Exception e) {
							//print stack to console
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

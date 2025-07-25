package app.pebl.profile;

import app.pebl.data.Post;
import app.pebl.posts.PostCtrl;
import app.pebl.util.Config;
import app.pebl.util.Controller;
import app.pebl.Main;
import app.pebl.data.User;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class ProfileCtrl extends Controller {
	//fxml imports
	@FXML private Text lblUsername;
	@FXML private Label lblStatus;
	@FXML private Label lblFollowers;
	@FXML private Label lblFollowing;
	@FXML private Label lblSkips;
	@FXML private Label lblAge;
	@FXML private Label lblGender;
	@FXML private Label lblLocation;
	@FXML private ScrollPane scrUserPosts;
	@FXML private VBox feedUserPosts;
	@FXML private MenuItem edit;
	@FXML private MenuItem follow;
	@FXML private Button btnFollow;
	@FXML private MenuItem logout;
	@FXML private MenuItem leaderboard;

	//class fields
	User displayUser;

	 public void initialize() {
		 //initialise listener for new posts being added to feed and scroll to bottom
		 feedUserPosts.heightProperty().addListener(new ChangeListener() {
			 @Override
			 public void changed(ObservableValue observable, Object oldvalue, Object newValue) {
				 scrUserPosts.setVvalue((Double) newValue);
			 }
		 });
	 }

	public void refresh() {
		//get user data
		Task<Void> profileRefresh = new Task<>() {
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

				if (displayUser != null) {
					//update gui
					Platform.runLater(() -> {
						//update fields
						lblUsername.setText(displayUser.getUsername());

						//check if user has status
						if (displayUser.getStatus() != null && !displayUser.getStatus().isEmpty()) {
							//show label
							lblStatus.setVisible(true);
							lblStatus.setManaged(true);

							//set status
							lblStatus.setText("\"" + displayUser.getStatus() + "\"");
						} else {
							//hide label
							lblStatus.setVisible(false);
							lblStatus.setManaged(false);
						}

						lblFollowers.setText(displayUser.getFollowers().size() + " Followers");
						lblFollowing.setText(displayUser.getFollowing().size() + " Following");
						lblSkips.setText(displayUser.getSkips() + " Skips");
						lblAge.setText("Age " + displayUser.getAge());

						//check if user has location
						if (displayUser.getLocation() != null && !displayUser.getLocation().isEmpty()) {
							//show label
							lblLocation.setVisible(true);
							lblLocation.setManaged(true);

							//set location
							lblLocation.setText(displayUser.getLocation());
						} else {
							//hide label
							lblLocation.setVisible(false);
							lblLocation.setManaged(false);
						}

						//set swedish gender (dont ask)
						if (displayUser.getGender()) {
							lblGender.setText("Gendered");
						} else {
							lblGender.setText("No Gender");
						}

						//update follow button
						if (Config.getInstance().getCurrentUser().getFollowing().contains(displayUser.getUsername())) {
							//update buttons
							btnFollow.setText("Unfollow");
							follow.setText("Unfollow");
						} else {
							//update buttons
							btnFollow.setText("Follow");
							follow.setText("Follow");
						}

						//clear post feed
						feedUserPosts.getChildren().clear();
					});
				}
				//null return from server
				else {
					Platform.runLater(() -> {
						//show error as no profile retrieved
						showError("Profile Error", "Error fetching Profile from Server. Please try again later.");
					});
				}

				//init post list and get posts
				ArrayList<Integer> postIDs = displayUser.getPosts();

				//trim arraylist to size
				postIDs.trimToSize();

				//check that get succeeded
				if (postIDs != null) {
					//get last post
					final Integer mostRecentPostID = Integer.valueOf(postIDs.getLast());

					//loop for all followers
					for (Integer id : postIDs) {
						try {
							//attempt to get post
							Post post = Main.getPost(id);

							//check if post was received
							if (post != null) {
								//update gui
								Platform.runLater(() -> {
									try {
										//attempt to add post
										addPost(feedUserPosts, post);

										//add separator if not last post
										if (id != mostRecentPostID) {
											feedUserPosts.getChildren().add(new Separator());
										}
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
								});
							}
							else {
								Platform.runLater(() -> {
									//show post get error
									showError("Post Error", "Error fetching Post: " + id + " from Server. Please try again later.");
								});
							}
						} catch(Exception e){
							//print stack to console
							e.printStackTrace();

							Platform.runLater(() -> {
								//show general error
								showError("Exception in pebl client", e.getMessage());

								//exit program
								Platform.exit();
							});
						}
					}
				}

				//update GUI with default cursor
				Platform.runLater(() -> {
					//show general error
					layoutParent.getScene().setCursor(Cursor.DEFAULT);
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
		signup.initModality(Modality.WINDOW_MODAL);
		signup.showAndWait();

		//refresh profile
		this.refresh();
	}

	public void setUser(User newUser) {
		displayUser = newUser;

		//hide/show items if current user
		if (displayUser.getUsername().equals(Config.getInstance().getCurrentUser().getUsername())) {
			//current user
			edit.setVisible(true);
			follow.setVisible(false);
			logout.setVisible(true);
			leaderboard.setVisible(true);
			btnFollow.setVisible(false);
		}
		else {
			//other user
			edit.setVisible(false);
			follow.setVisible(true);
			logout.setVisible(false);
			leaderboard.setVisible(false);
			btnFollow.setVisible(true);
		}

		//refresh data
		this.refresh();
	}

	public void toggleFollow() {
		//get user data
		Task<Void> userRefresh = new Task<>() {
			@Override public Void call() {
				//update GUI with wait cursor
				Platform.runLater(() -> {
					//show general error
					layoutParent.getScene().setCursor(Cursor.WAIT);
				});

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

	public void addPost(VBox feed, Post added) throws IOException {
		//get fxml loader
		FXMLLoader loader = Main.getFXML("post");

		//create new vbox to add
		loader.setRoot(new VBox());

		//load into vbox
		feed.getChildren().add(loader.load());

		//get controller and inject data
		PostCtrl controller = (PostCtrl) loader.getController();
		controller.setPost(added);
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

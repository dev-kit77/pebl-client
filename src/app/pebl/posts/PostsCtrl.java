package app.pebl.posts;

import app.pebl.Config;
import app.pebl.Controller;
import app.pebl.Main;
import app.pebl.profile.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

public class PostsCtrl extends Controller {
	private User currUser;

	@FXML
	VBox feedPosts;
	@FXML
	VBox feedFollowing;
	@FXML
	VBox feedFollowers;

	public void initialize() {
		//get current user
		currUser = Config.getInstance().getCurrentUser();

		//get latest posts
		refresh();
	}

	public void refresh() {
		//clear posts
		feedPosts.getChildren().clear();

		//get user data
		Task<Void> userRefresh = new Task<>() {
			@Override public Void call() {
				//init post list
				ArrayList<Post> posts = null;

				try {
					//get feed from
					posts = Main.getFeed();
				} catch (Exception e) {
					//print stack to console
					e.printStackTrace();

					//show error message
					showError("Exception in pebl client", e.getMessage());
				}

				//check that get succeeded
				if (posts != null) {
					//loop for all followers
					for (Post post : posts) {
						try {
							//check if response succeeded
							if (post != null) {
								//update gui
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										try {
											//attempt to add post
											addPost(feedPosts, post);
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
				}

				//end task with null return
				return null;
			};
		};

		//run thread
		Main.getExecutor().execute(userRefresh);
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
}

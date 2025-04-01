package app.pebl.posts;

import app.pebl.util.Config;
import app.pebl.util.Controller;
import app.pebl.Main;
import app.pebl.data.Post;
import app.pebl.data.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class PostsCtrl extends Controller {
	//fxml elements
	@FXML private VBox feedPosts;
	@FXML private VBox feedFollowing;
	@FXML private VBox feedFollowers;

	//class fields
	private User currUser;

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
		Task<Void> feedRefresh = new Task<>() {
			@Override public Void call() {
				//refresh current user
				refreshUser();

				//init post list
				ArrayList<Post> posts = null;

				try {
					//get feed from
					posts = Main.getFeed();
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

				//check that get succeeded
				if (posts != null) {
					//get last post
					final Post mostRecentPost = posts.getLast();

					//loop for all followers
					for (Post post : posts) {
						try {
							//check if response succeeded
							if (post != null) {
								//update gui
								Platform.runLater(() -> {
									try {
										//attempt to add post
										addPost(feedPosts, post);

										//add separator if not last post
										if (!post.equals(mostRecentPost)) {
											feedPosts.getChildren().add(new Separator());
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
					}
				}
				//null return from get
				else {
					Platform.runLater(() -> {
						//show error as no posts retrieved
						showError("Posts Error", "Error fetching Posts from Server. Please try again later.");
					});
				}

				//end task with null return
				return null;
			}
		};

		//run thread
		Main.getExecutor().execute(feedRefresh);
	}

	public void showProfile() {
		try {
			Main.initProfile(currUser).show();
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
			Main.initConnections(currUser).show();
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
			Main.initLeaderboard(currUser).show();
		} catch (Exception e) {
			//print stack to console
			e.printStackTrace();

			//show general error
			showError("Exception in pebl client", e.getMessage());

			//exit program
			Platform.exit();
		}
	}

	public void newPost() throws IOException {
		//create new post window
		Stage newPost = new Stage();
		newPost.setTitle("New Post");
		newPost.setScene(new Scene(Main.getFXML("newPost").load()));
		newPost.initOwner(layoutParent.getScene().getWindow());
		newPost.initModality(Modality.WINDOW_MODAL);
		newPost.showAndWait();

		//refresh posts
		refresh();
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

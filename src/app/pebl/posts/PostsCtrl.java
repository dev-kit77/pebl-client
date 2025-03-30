package app.pebl.posts;

import app.pebl.Config;
import app.pebl.Controller;
import app.pebl.Main;
import app.pebl.connections.CardCtrl;
import app.pebl.profile.User;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class PostsCtrl extends Controller {
	private User currUser;

	@FXML
	VBox fedPosts;
	@FXML
	VBox fedFollowing;
	@FXML
	VBox fedFollowers;

	public void initialize() {
		currUser = Config.getInstance().getCurrentUser();
	}

	public void refresh() {
		//get user data
		Task<Void> postRefresh = new Task<>() {
			@Override public Void call() {
				//code to do this goes here
				return null;
			};
		};

		Main.getExecutor().execute(postRefresh);

		//Add Posts to vbox
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

		//load into vbox
		feed.getChildren().add(loader.load());

		//get controller and inject data
		PostCtrl controller = (PostCtrl) loader.getController();
		controller.setPost(added);
	}
}

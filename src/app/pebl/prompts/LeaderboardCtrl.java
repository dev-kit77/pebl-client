package app.pebl.prompts;

import app.pebl.Controller;
import app.pebl.Main;
import app.pebl.connections.CardCtrl;
import app.pebl.profile.User;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class LeaderboardCtrl extends Controller {
	@FXML Label lblCurrUsername;
	@FXML Label lblCurrSkips;
	@FXML Label lblCurrStatus;
	private User currUser;

	public void refresh() {
		Task<Void> getUser = new Task<>() {
			@Override public Void call() {
				//code goes here

				//empty return
				return null;
			}
		};

		Task<Void> getLeaderboard = new Task<>() {
			@Override public Void call() {
				//code goes here

				//empty return
				return null;
			};
		};

		Main.getExecutor().execute(getUser);
		Main.getExecutor().execute(getLeaderboard);

		lblCurrUsername.setText(currUser.getUsername());
		lblCurrSkips.setText("(" + currUser.getSkips() + " Skips)");
		lblCurrStatus.setText(currUser.getStatus());

		//code to add to leaderboard vbox goes here
	}

	public void addCard(VBox list, User addUser) throws IOException {
		//get fxml loader
		FXMLLoader loader = Main.getFXML("miniProfile");

		//load into vbox
		list.getChildren().add(loader.load());

		//get controller and inject data
		CardCtrl controller = (CardCtrl) loader.getController();
		controller.setUser(addUser);
	}

	public void setUser(User newUser) {
		currUser = newUser;
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

package app.pebl.connections;

import app.pebl.Controller;
import app.pebl.Main;
import app.pebl.profile.User;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class ConnectionsCtrl extends Controller {
	private User displayUser;

	@FXML VBox fedMutuals;
	@FXML VBox fedFollowers;
	@FXML VBox fedFollowing;
	@FXML Label lblCurrUsername;
	@FXML Label lblCurrSkips;
	@FXML Label lblCurrStatus;

	public void refresh() {
		//get user data
		Task<Void> userRefresh = new Task<>() {
			@Override public Void call() {
				//code to do this goes here
				return null;
			};
		};

		Main.getExecutor().execute(userRefresh);

		lblCurrUsername.setText(displayUser.getUsername());
		lblCurrSkips.setText("(" + displayUser.getSkips() + " Skips)");
		lblCurrStatus.setText("\"" + displayUser.getStatus() + "\"");

		//add to three sections
	}

	public void setUser(User newUser) {
		displayUser = newUser;
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

		//load into vbox
		list.getChildren().add(loader.load());

		//get controller and inject data
		CardCtrl controller = (CardCtrl) loader.getController();
		controller.setUser(addUser);
	}
}

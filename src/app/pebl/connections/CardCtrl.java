package app.pebl.connections;

import app.pebl.Main;
import app.pebl.profile.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.io.IOException;

public class CardCtrl extends HBox {
	@FXML Label lblUsername;
	@FXML Label lblSkips;
	@FXML Label lblStatus;

	private User displayUser;

	public void setUser(User newUser) {
		lblUsername.setText(displayUser.getUsername());
		lblSkips.setText("(" + displayUser.getSkips() + " Skips)");
		lblStatus.setText(displayUser.getStatus());

		displayUser = newUser;
	}

	public void setUser(String username, String displayName,int skips, String status) {
		lblUsername.setText(username);
		lblSkips.setText("(" + skips + " Skips)");
		lblStatus.setText(status);
	}

	public void showProfile() throws IOException {
		//open new profile window with user
		Main.initProfile(displayUser).show();
	}
}

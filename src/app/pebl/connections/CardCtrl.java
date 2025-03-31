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
		//update fields
		lblUsername.setText(newUser.getUsername());
		lblSkips.setText("(" + newUser.getSkips() + " Skips)");
		lblStatus.setText(newUser.getStatus());

		//update display user
		displayUser = newUser;
	}

	public void setUser(String username, String displayName,int skips, String status) {
		//update fields
		lblUsername.setText(username);
		lblSkips.setText("(" + skips + " Skips)");
		lblStatus.setText(status);
	}

	public void showProfile() throws IOException {
		//open new profile window with user
		Main.initProfile(displayUser).show();
	}
}

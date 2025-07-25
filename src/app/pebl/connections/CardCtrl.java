package app.pebl.connections;

import app.pebl.Main;
import app.pebl.data.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.io.IOException;

public class CardCtrl extends HBox {
	//fxml elements
	@FXML private Text lblUsername;
	@FXML private Text lblSkips;
	@FXML private Text lblStatus;

	//class fields
	private User displayUser;

	public void setUser(User newUser) {
		//update fields
		lblUsername.setText(newUser.getUsername());
		lblSkips.setText("(" + newUser.getSkips() + " Skips)");

		//check if user has status
		if (newUser.getStatus() != null && !newUser.getStatus().isEmpty()) {
			//show label
			lblStatus.setVisible(true);

			//set status
			lblStatus.setText("\"" + newUser.getStatus() + "\"");
		}
		else {
			//hide label
			lblStatus.setVisible(false);
		}

		//update display user
		displayUser = newUser;
	}

	public void setUser(String username, int skips, String status) {
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

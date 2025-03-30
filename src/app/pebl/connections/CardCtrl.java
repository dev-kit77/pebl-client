package app.pebl.connections;

import app.pebl.profile.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class CardCtrl extends HBox {
	@FXML Label lblDisplayName;
	@FXML Label lblUsername;
	@FXML Label lblSkips;
	@FXML Label lblStatus;

	public void setUser(User displayUser) {
		lblUsername.setText("@" + displayUser.getUsername());
		//lblDisplayName.setText(displayUser.getDisplayName());
		lblSkips.setText("(" + displayUser.getSkips() + " Skips)");
		lblStatus.setText(displayUser.getStatus());
	}

	public void setUser(String username, String displayName,int skips, String status) {
		lblUsername.setText("@" + username);
		lblDisplayName.setText(displayName);
		lblSkips.setText("(" + skips + " Skips)");
		lblStatus.setText(status);
	}
}

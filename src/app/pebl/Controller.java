package app.pebl;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
	@FXML
	protected Node layoutParent;

	public void closeWindow() {
		//close window
		layoutParent.getScene().getWindow().hide();
	}

	public void showAbout() throws IOException {
		//init stage
		Stage stage = new Stage();
		stage.setTitle("About");
		stage.initOwner(layoutParent.getScene().getWindow());
		stage.initModality(Modality.APPLICATION_MODAL);

		//get fxml
		FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("res/fxml/about.fxml"));

		//load scene
		stage.setScene(new Scene(fxmlLoader.load()));

		//show about
		stage.showAndWait();
	}

	public void logout() {
		//close window
		closeWindow();

		//empty config
		Config.getInstance().setAuthToken(null);
		Config.getInstance().setCurrentUser(null);
		Config.getInstance().setUserCache(false);
		Config.getInstance().setUsername(null);

		try {
			//show login screen
			Main.showLogin(Main.getPrimaryStage());
		} catch (Exception e) {
			//print stack to console
			e.printStackTrace();

			//show error message
			showError("Exception in pebl client", e.getMessage());

			//stop platform
			Platform.exit();
		}
	}

	public void showAlert(String header, String content)  {
		//create alert
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Alert");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	public void showError(String header, String content) {
		//create alert
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
}

package app.pebl.util;

import app.pebl.Main;
import app.pebl.data.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
	//fxml elements
	@FXML protected Node layoutParent;

	protected void refreshUser() {
		try {
			//refresh current user from server
			User updated = Main.getProfile(Config.getInstance().getCurrentUser().getUsername());

			//check if updated user is null
			if (updated == null) {
				//display user update error
				showError("User Update Error", "Error fetching current user from server. Please try again later.");
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

	public void logout() {
		//close all windows
		Stage main = (Stage) Main.getPrimaryStage().getScene().getWindow();
		main.close();

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

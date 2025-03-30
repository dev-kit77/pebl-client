package app.pebl;

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

	public void showError(String header, String content) {
		//show login error
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
}

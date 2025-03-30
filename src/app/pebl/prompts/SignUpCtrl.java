package app.pebl.prompts;

import app.pebl.Controller;
import javafx.fxml.FXML;

import javafx.scene.control.*;

public class SignUpCtrl extends Controller {
	@FXML TextField iptEmail;
	@FXML TextField iptUsername;
	@FXML PasswordField iptPassword;
	@FXML PasswordField iptConfirmation;
	@FXML Label lblAge;
	@FXML Slider iptAge;
	@FXML CheckBox iptGender;
	@FXML CheckBox iptData;
	@FXML CheckBox iptTerms;

	/**
	 * controller init method. Begins action Listener for age label
	 */
	public void initialize() {
		iptAge.valueProperty().addListener((observable, oldValue, newValue) -> {
			lblAge.setText(Long.toString(Math.round((Double) newValue)));
		});
	}

	public void handleSubmit() {
		if (iptEmail.getText().isEmpty() || iptUsername.getText().isEmpty() || iptPassword.getText().isEmpty() || iptConfirmation.getText().isEmpty()) {
			//display missing info err
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Email, Username and Password are Required");
			alert.setContentText("Please fill all the required fields to continue");
			alert.showAndWait();
		}
		else if (!iptPassword.getText().equals(iptConfirmation.getText())) {
			//set password fields to null
			iptPassword.setText(null);
			iptConfirmation.setText(null);

			//display password mismatch err
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Password and Confirm Password Do Not Match");
			alert.setContentText("Please confirm pasword to continue");
			alert.showAndWait();
		}
		else if (iptAge.getValue() < 13) {
			//display age err
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("You must be greater than 13 to use this Service.");
			alert.setContentText("Please select an age greater than 13 to continue");
			alert.showAndWait();
		}
		else if (!iptData.isSelected()) {
			//display data collection err
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("You must agree to our data collection to use this Service.");
			alert.setContentText("Please agree to our data collection to continue");
			alert.showAndWait();
		}
		else if (!iptTerms.isSelected()) {
			//display terms err
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("You must agree to our Terms and Conditions to use this Service.");
			alert.setContentText("Please agree to our Terms and Conditions to continue");
			alert.showAndWait();
		}
		else {
			//submit form

			//close window back to login
			closeWindow();
		}
	}
}

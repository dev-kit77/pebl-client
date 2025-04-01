package app.pebl.prompts;

import app.pebl.util.Controller;
import app.pebl.Main;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;

import javafx.scene.control.*;

public class SignUpCtrl extends Controller {
	//fxml elements
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
		//set first value
		lblAge.setText(Long.toString(Math.round((Double) iptAge.getValue())));

		//set update label with slider
		iptAge.valueProperty().addListener((observable, oldValue, newValue) -> lblAge.setText(Long.toString(Math.round((Double) newValue))));
	}

	public void handleSubmit() {
		if (iptEmail.getText().isEmpty() || iptUsername.getText().isEmpty() || iptPassword.getText().isEmpty() || iptConfirmation.getText().isEmpty()) {
			//display missing info err
			showError("Email, Username and Password are Required","Please fill all the required fields to continue");
		}
		else if (!iptPassword.getText().equals(iptConfirmation.getText())) {
			//set password fields to null
			iptPassword.setText(null);
			iptConfirmation.setText(null);

			//display password mismatch err
			showError("Please confirm Pasword to continue","Password and Confirm Password Do Not Match");
		}
		else if (iptAge.getValue() < 13) {
			//display age err
			showError("You must be greater than 13 to use this Service.","Please select an age greater than 13 to continue");
		}
		else if (!iptData.isSelected()) {
			//display data collection err
			showError("You must agree to our data collection to use this Service.", "Please agree to our data collection to continue");
		}
		else if (!iptTerms.isSelected()) {
			//display terms err
			showError("You must agree to our Terms and Conditions to use this Service.", "Please agree to our Terms and Conditions to continue");
		}
		else {
			//submit form
			Task<Void> createUser = new Task<>() {
				@Override
				public Void call() {
					//init check variable
					boolean success = false;

					//create new user on server
					try {
						success = Main.register(iptUsername.getText(), iptPassword.getText(), Integer.parseInt(lblAge.getText()), iptGender.isSelected());
					} catch (Exception e) {
						//print stack trace to console
						e.printStackTrace();

						//update GUI
						Platform.runLater(() -> {
							//show general error
							showError("Exception in pebl client", e.getMessage());

							//exit program
							Platform.exit();
						});
					}

					//update gui depending on success
					if (success) {
						Platform.runLater(() -> {
							//close window
							closeWindow();
						});
					} else {
						//TODO get rid of this horrible unspecific error
						Platform.runLater(() -> {
							//show unspecific error (I have no access to what the error actually is because all I get is a boolean even though the error is given in the response)
							showError("Sign-up Error", "There was an issue with your account creation. Please try other details.");
						});
					}

					//null return to end thread
					return null;
				}

			};

			//run thread
			Main.getExecutor().submit(createUser);
		}
	}
}

package app.pebl.prompts;

import app.pebl.util.Config;
import app.pebl.util.Controller;
import app.pebl.Main;
import app.pebl.data.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class EditCtrl extends Controller {
	//fields
	User currUser;

	@FXML
	Label lblUsername;
	@FXML
	TextField iptStatus;
	@FXML
	Slider ageSlider;
	@FXML
	Label age;
	@FXML
	CheckBox chkGender;

	public void initialize() {
		//get current user
		this.currUser = Config.getInstance().getCurrentUser();

		//setup fields
		lblUsername.setText("Username: " + currUser.getUsername());
		iptStatus.setText(currUser.getStatus());
		ageSlider.setValue(currUser.getAge());
		chkGender.setSelected(currUser.getGender());

		//set age
		age.setText(Long.toString(Math.round((Double) ageSlider.getValue())));

		//create listener for slider value
		ageSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			age.setText(Long.toString(Math.round((Double) newValue)));
		});
	}

	public void saveChanges() {
		//check age
		if (ageSlider.getValue() < 13) {
			//display age err
			showError("You must be greater than 13 to use this Service.", "Please select an age greater than 13 to continue");
		}
		else {
			//submit form
			Task<Void> createUser = new Task<Void>() {
				@Override public Void call() {
					//init check variable
					boolean success = false;

					//create new user on server
					try {
						success = Main.updateProfile(Integer.parseInt(age.getText()), chkGender.isSelected(), iptStatus.getText());
					} catch (Exception e) {
						//print stack trace to console
						e.printStackTrace();

						//update GUI
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								//show general error
								showError("Exception in pebl client", e.getMessage());

								//exit program
								Platform.exit();
							}
						});
					}

					//update gui depending on success
					if (success) {
						Platform.runLater(new Runnable() {
							@Override public void run() {
								//close window back to login
								closeWindow();
							}
						});
					}
					else {
						//TODO get rid of this horrible unspecific error
						Platform.runLater(new Runnable() {
							@Override public void run() {
								//show unspecific error (I have no access to what the error actually is because all I get is a boolean even though the error is given in the response)
								showError("Sign-up Error", "There was an issue with your edit. Please try other details.");
							}
						});
					}

					//null return to end thread
					return null;
				};
			};

			//run thread
			Main.getExecutor().submit(createUser);
		}
	}
}

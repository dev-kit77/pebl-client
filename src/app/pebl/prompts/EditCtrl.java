package app.pebl.prompts;

import app.pebl.util.Config;
import app.pebl.util.Controller;
import app.pebl.Main;
import app.pebl.data.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class EditCtrl extends Controller {
	//fxml elements
	@FXML private Label lblUsername;
	@FXML private TextField iptStatus;
	@FXML private Slider iptAge;
	@FXML private Label lblAge;
	@FXML private CheckBox iptGender;
	@FXML private TextField iptLocation;

	//class fields
	User currUser;

	public void initialize() {
		//get current user
		this.currUser = Config.getInstance().getCurrentUser();

		//setup fields
		lblUsername.setText("Username: " + currUser.getUsername());
		iptStatus.setText(currUser.getStatus());
		iptAge.setValue(currUser.getAge());
		iptGender.setSelected(currUser.getGender());
		iptLocation.setText(currUser.getLocation());

		//set age
		lblAge.setText(Long.toString(Math.round((Double) iptAge.getValue())));

		//create listener for slider value
		iptAge.valueProperty().addListener((observable, oldValue, newValue) -> lblAge.setText(Long.toString(Math.round((Double) newValue))));
	}

	public void saveChanges() {
		//check age
		if (iptAge.getValue() < 13) {
			//display age err
			showError("You must be greater than 13 to use this Service.", "Please select an age greater than 13 to continue");
		}
		else {
			//submit form
			Task<Void> createUser = new Task<>() {
				@Override public Void call() {
					//update GUI with wait cursor
					Platform.runLater(() -> {
						//show general error
						layoutParent.getScene().setCursor(Cursor.WAIT);
					});

					//init check variable
					boolean success = false;

					//create new user on server
					try {
						success = Main.updateProfile(Integer.parseInt(lblAge.getText()), iptGender.isSelected(), iptStatus.getText(), iptLocation.getText());
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
							//close window back to profile
							closeWindow();
						});
					} else {
						//TODO get rid of this horrible unspecific error
						Platform.runLater(() -> {
							//show unspecific error (I have no access to what the error actually is because all I get is a boolean even though the error is given in the response)
							showError("Edit Error", "There was an issue with your edit. Please try other details.");
						});
					}

					//update GUI with default cursor
					Platform.runLater(() -> {
						//show general error
						layoutParent.getScene().setCursor(Cursor.DEFAULT);
					});

					//null return to end thread
					return null;
				}

			};

			//run thread
			Main.getExecutor().submit(createUser);
		}
	}
}

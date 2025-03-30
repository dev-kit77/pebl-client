package app.pebl.prompts;

import app.pebl.Config;
import app.pebl.Controller;
import app.pebl.profile.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class EditCtrl extends Controller {
	User currUser;

	@FXML
	Label lblUsername;
	@FXML
	Slider ageSlider;
	@FXML
	Label age;
	@FXML
	CheckBox chkGender;

	public void initialize() {
		this.currUser = Config.getInstance().getCurrentUser();

		lblUsername.setText(currUser.getUsername());
		ageSlider.setValue(currUser.getAge());
		chkGender.setSelected(currUser.getGender());

		ageSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			age.setText(Long.toString(Math.round((Double) newValue)));
		});
	}

	public void saveChanges() {
		if (ageSlider.getValue() < 13) {
			//display age err
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("You must be greater than 13 to use this Service.");
			alert.setContentText("Please select an age greater than 13 to continue");
			alert.showAndWait();
		}
		else {
			//submit form

			//close window back to login
			closeWindow();
		}
	}
}

package app.pebl.login;

import app.pebl.Config;
import app.pebl.Controller;
import app.pebl.Main;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginCtrl extends Controller {
	@FXML
	private TextField username;
	@FXML
	private PasswordField password;
	@FXML
	private CheckBox chkLogin;

	private String srvAddress;

	public LoginCtrl() {
		this.srvAddress = Config.getInstance().getServerAddr();
	}

	public void handleLogin() throws Exception {
		//check checkbox state
		if (chkLogin.isSelected()) {
			System.out.println("Login Remembered");
		}
		else {
			System.out.println("Login Not Remembered");
		}

		//set server address in config
		Config.getInstance().setServerAddr(this.srvAddress);

		Task<Void> authUser = new Task<Void>() {
			@Override public Void call() {
				//authenticate user with server
				return null;
			};
		};

		//run thread
		Main.getExecutor().submit(authUser);

		//check for auth
		System.out.println("Login");
		System.out.println("Username: " + username.getText());
		System.out.println("Password: " + password.getText());

		//show main window set
		Main.showMainWindows(Main.getPrimaryStage());

		//hide login window
		closeWindow();
	}

	public void handleSignUp() throws IOException {
		Stage signup = new Stage();
		signup.setTitle("Sign Up");
		signup.setScene(new Scene(Main.loadFXML("signup")));
		signup.initOwner(layoutParent.getScene().getWindow());
		signup.initModality(Modality.APPLICATION_MODAL);
		signup.showAndWait();
	}

	public void serverPrompt() {
		//create dialog window
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Server Address");
		dialog.setHeaderText("Please Enter Server Address");
		dialog.showAndWait();

		//get address from dialog
		this.srvAddress = dialog.getEditor().getText();
		System.out.println("Server Address: " + this.srvAddress);
	}
}
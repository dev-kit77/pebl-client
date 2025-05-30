package app.pebl.login;

import app.pebl.util.Config;
import app.pebl.util.Controller;
import app.pebl.Main;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginCtrl extends Controller {
	//fxml elements
	@FXML private TextField username;
	@FXML private PasswordField password;
	@FXML private CheckBox chkLogin;

	//class fields
	private String srvAddress;

	public LoginCtrl() {
		this.srvAddress = Config.getInstance().getServerAddr();
	}

	public void handleLogin() {
		//check checkbox state
		if (chkLogin.isSelected()) {
			//send to log
			System.out.println("Login Remembered");
		}
		else {
			//print to log
			System.out.println("Login Not Remembered");
		}

		//set config
		Config.getInstance().setUserCache(chkLogin.isSelected());

		//set server address in config
		Config.getInstance().setServerAddr(this.srvAddress);

		Task<Void> authUser = new Task<>() {
			@Override public Void call() {
				//update GUI with wait cursor
				Platform.runLater(() -> {
					//show general error
					layoutParent.getScene().setCursor(Cursor.WAIT);
				});

				//init conditions
				boolean online = false;
				boolean success = false;

				//authenticate user with server
				try {
					//check server connection
					online = Main.checkServer();

					if (online) {
						success = Main.login(username.getText(), password.getText());
					} else {
						//update GUI
						Platform.runLater(() -> {
							//show server error
							showError("Connection to pebl server: " + srvAddress + " failed", "Please check your server address and try again.");
						});
					}

					if (!success) {
						//update GUI
						Platform.runLater(() -> {
							//show login error
							showError("Login failed", "Please check your Username and Password and try again.");

							//clear password
							password.setText("");
						});
					} else {
						//update GUI
						Platform.runLater(() -> {
							//show main window set
							try {
								Main.showMainWindows();
							} catch (IOException e) {
								//print stack
								e.printStackTrace();

								//show general error
								showError("Exception in pebl client", e.getMessage());

								//exit program
								Platform.exit();
							}

							//hide login window
							closeWindow();
						});
					}
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

				//update GUI with default cursor
				Platform.runLater(() -> {
					//show general error
					layoutParent.getScene().setCursor(Cursor.DEFAULT);
				});

				//end thread
				return null;
			}

		};

		//run thread
		Main.getExecutor().submit(authUser);

		//print test to console
		System.out.println("Login");
		System.out.println("Username: " + username.getText());
	}

	public void handleSignUp() throws IOException {
		Stage signup = new Stage();
		signup.setTitle("Sign Up");
		signup.setScene(new Scene(Main.getFXML("signup").load()));
		signup.initOwner(layoutParent.getScene().getWindow());
		signup.initModality(Modality.APPLICATION_MODAL);
		signup.showAndWait();

		if (Config.getInstance().getAuthToken() != null) {
			//hide login window
			closeWindow();

			//show alert for account creation
			showAlert("Registration Successful", "Logging in to your new Account");

			//show main window set
			try {
				Main.showMainWindows();
			} catch (IOException e) {
				//print stack
				e.printStackTrace();

				//show general error
				showError("Exception in pebl client", e.getMessage());

				//exit program
				Platform.exit();
			}
		}
	}

	public void serverPrompt() {
		//create dialog window
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Server Address");
		dialog.setHeaderText("Please Enter Server Address");
		dialog.getEditor().setText(Config.getInstance().getDefaultAddress());
		dialog.showAndWait();

		//get address from dialog
		this.srvAddress = dialog.getEditor().getText();
		System.out.println("Server Address: " + this.srvAddress);
	}
}
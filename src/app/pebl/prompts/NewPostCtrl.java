package app.pebl.prompts;

import app.pebl.Main;
import app.pebl.util.Controller;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class NewPostCtrl extends Controller {
	//fxml elements
	@FXML TextArea body;

	public void handleSubmit() {
		Task<Void> newPost = new Task<>() {
			@Override
			public Void call() {
				//init success
				boolean success = false;

				try {
					//create new post
					success = Main.createPost(body.getText());
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

				//show error if not successful
				if (!success) {
					//update gui
					Platform.runLater(() -> {
						//show error to user
						showError("Error Creating Post", "Post creation failed. Please try again later.");
					});
				}

				//update gui
				Platform.runLater(() -> {
					//close window
					closeWindow();
				});

				//return to end task
				return null;
			}
		};

		//execute thread
		Main.getExecutor().submit(newPost);
	}
}

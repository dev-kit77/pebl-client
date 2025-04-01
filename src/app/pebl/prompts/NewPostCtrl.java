package app.pebl.prompts;

import app.pebl.Main;
import app.pebl.util.Controller;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class NewPostCtrl extends Controller {
	@FXML TextArea body;

	public void handleSubmit() {
		Task<Void> newPost = new Task<Void>() {
			@Override public Void call() {
				//init success
				boolean success = false;

				try {
					//create new post
					success = Main.createPost(body.getText());
				} catch (Exception e) {
					//print stack to console
					e.printStackTrace();

					//show error message
					showError("Exception in pebl client", e.getMessage());

					//exit app
					Platform.exit();
				}

				//show error if not successful
				if (!success) {
					//update gui
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							//show error to user
							showError("Error Creating Post", "Post creation failed. Please try again later.");
						}
					});
				}

				//update gui
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						//close window
						closeWindow();
					}
				});

				//return to end task
				return null;
			}
		};

		//execute thread
		Main.getExecutor().submit(newPost);
	}
}

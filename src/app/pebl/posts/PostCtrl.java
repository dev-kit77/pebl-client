package app.pebl.posts;

import app.pebl.Main;
import app.pebl.data.Post;
import app.pebl.util.Config;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;

public class PostCtrl extends VBox {
	@FXML Text lblUsername;
	@FXML Text txtBody;
	@FXML Button btnSkip;
	private Post post;

	public void refresh() {
		//get post data
		Task<Void> postRefresh = new Task<>() {
			@Override public Void call() {
				try {
					//get post from server
					post = Main.getPost(post.getId());
				} catch (Exception e) {
					//print stack to console
					e.printStackTrace();

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

				//check that get succeeded
				if (post != null) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							try {
								//update fields
								lblUsername.setText(post.getSender());
								txtBody.setText(post.getContent());
								btnSkip.setText(post.getSkips() + " Skips");

								//disable button if post is from current user
								if (post.getSender().equals(Config.getInstance().getCurrentUser().getUsername())) {
									btnSkip.setDisable(true);
								}
								else{
									btnSkip.setDisable(false);
								}
							} catch (Exception e) {
								//print stack to console
								e.printStackTrace();

								//show error message
								showError("Exception in pebl client", e.getMessage());

								//exit app
								Platform.exit();
							}
						}
					});
				}
				//failed to get post
				else {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							//display post error
							showError("Post Error", "Error fetching Post from Server. Please try again later.");
						}
					});
				}

				//end task with null return
				return null;
			}
		};

		//run thread
		Main.getExecutor().execute(postRefresh);
	}

	public void skip() {
		//get user data
		Task<Void> skip = new Task<>() {
			@Override public Void call() {
				try {
					//send like to server
					Main.like(post.getId());
				} catch (Exception e) {
					//print stack to console
					e.printStackTrace();

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

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						refresh();
					}
				});

				//end task
				return null;
			}
		};

		//execute on thread
		Main.getExecutor().execute(skip);
	}

	public void setPost(Post newPost) {
		//update fields
		lblUsername.setText(newPost.getSender());
		txtBody.setText(newPost.getContent());
		btnSkip.setText(newPost.getSkips() + " Skips");

		//disable button if post is from current user
		if (newPost.getSender().equals(Config.getInstance().getCurrentUser().getUsername())) {
			btnSkip.setDisable(true);
		}
		else{
			btnSkip.setDisable(false);
		}

		//link post object
		this.post = newPost;
	}

	public void setPost(String sender, String content, int skips) {
		//update fields
		lblUsername.setText(sender);
		txtBody.setText(content);
		btnSkip.setText(Integer.toString(skips));
	}

	public void showProfile() throws IOException {
		//open new profile window with user
		Main.initProfile(post.getSender()).show();
	}

	protected void showError(String header, String content) {
		//create alert
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
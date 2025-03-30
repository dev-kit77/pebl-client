//package specification
package app.pebl;

//imports
import app.pebl.profile.User;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Main class for GUI init
 */
public class Main extends Application {
	//Fields
	private static Stage primaryStage;

	/**
	 * start() method for the GUI. Initializes the login GUI.
	 *
	 * @param stage The main stage of the program
	 * @throws IOException passthrough from fxml loader
	 */
	@Override
	public void start(Stage stage) throws IOException {
		showLogin(stage);
	}

	@Override
	public void stop() {
		Task<Void> shutdownTask = new Task<>() {
			@Override public Void call() {
				//set wait cursor
				getPrimaryStage().getScene().setCursor(Cursor.WAIT);

				//save config file
				boolean success = Config.getInstance().save(".pebl.cfg");

				if (success) {
					System.out.println("Config Saved Successfully");
				}
				else {
					System.out.println("Config Saving Failed");
				}

				//set regular cursor
				getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);

				//empty return
				return null;
			}
		};

		new Thread(shutdownTask).start();
	}

	public static Stage initProfile(User displayUser) throws IOException {
		Stage stage = new Stage();
		stage.setTitle("Profile");
		stage.setScene(new Scene(loadFXML("profile")));

		return stage;
	}

	public static Stage initConnections(User displayUser) throws IOException {
		Stage stage = new Stage();
		stage.setTitle("Connections");
		stage.setScene(new Scene(loadFXML("connections")));

		return stage;
	}

	public static Stage initPosts() throws IOException {
		Stage stage = new Stage();
		stage.setTitle("Latest Posts");
		stage.setScene(new Scene(loadFXML("posts")));

		return stage;
	}

	public static void showMainWindows(Stage mainStage) throws IOException {
		//init posts window as main window
		mainStage = initPosts();
		mainStage.show();

		//init connections window
		Stage connectionStage = initConnections(Config.getInstance().getCurrentUser());
		connectionStage.initOwner(mainStage);
		connectionStage.show();

		//init profile window
		Stage profileStage = initProfile(Config.getInstance().getCurrentUser());
		profileStage.initOwner(mainStage);
		profileStage.show();
	}

	public static void showLogin(Stage stage) throws IOException {
		//init main stage
		stage = new Stage();
		stage.setTitle("Login");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(new Scene(loadFXML("login"), 250, 300));
		stage.show();

		//store primary stage
		primaryStage = stage;
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * FXML Loader Method.
	 *
	 * @param fxml filename of the file e.g. xyz (.fxml is added in method)
	 * @return javafx node contained within the fxml file
	 * @throws IOException file error, usually means the file does not exist
	 */
	public static Parent loadFXML(String fxml) throws IOException {
		//get fxml file from the fxml folder with filename in the parameter
		FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("res/fxml/" + fxml + ".fxml"));

		//return javafx node from fxml file
		return fxmlLoader.load();
	}

	public static void main(String[] args) {
		//init GUI
		launch(args);

		//NO MORE CODE GOES HERE IT NEEDS TO BE INVOKED ON A NEW THREAD VIA THE RUN LATER METHOD ON A SEPARATE THREAD
	 }
}

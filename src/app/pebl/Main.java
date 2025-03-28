//package specification
package app.pebl;

//imports
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Scanner;

/**
 * Main class for GUI init
 */
public class Main extends Application {
	//Fields
	public static Stage primaryStage;

	/**
	 * start() method for the GUI. Initializes the login GUI.
	 *
	 * @param stage The main stage of the program
	 * @throws IOException passthrough from fxml loader
	 */
	@Override
	public void start(Stage stage) throws IOException {
		//init main stage
		stage = new Stage();
		stage.setTitle("Login");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(new Scene(loadFXML("login"), 250, 300));
		stage.show();

		primaryStage = stage;
	}

	public static Stage initProfile() throws IOException {
		Stage stage = new Stage();
		stage.setTitle("Profile");
		stage.setScene(new Scene(loadFXML("profile")));

		return stage;
	}

	public static Stage initConnections() throws IOException {
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

	public static Stage showSignUp() throws IOException {
		Stage stage = new Stage();
		stage.setTitle("Sign Up");
		stage.setScene(new Scene(loadFXML("signup")));

		return stage;
	}

	public static void showMainWindows(Stage mainStage) throws IOException {
		//init posts window as main window
		mainStage = initPosts();
		mainStage.show();

		//init connections window
		Stage connectionStage = initConnections();
		connectionStage.initOwner(mainStage);
		connectionStage.show();

		//init profile window
		Stage profileStage = initProfile();
		profileStage.initOwner(mainStage);
		profileStage.show();
	}

	/**
	 * FXML Loader Method.
	 *
	 * @param fxml filename of the file e.g. xyz (.fxml is added in method)
	 * @return javafx node contained within the fxml file
	 * @throws IOException file error, usually means the file does not exist
	 */
	private static Parent loadFXML(String fxml) throws IOException {
		//get fxml file from the fxml folder with filename in the parameter
		FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("res/fxml/" + fxml + ".fxml"));

		//return javafx node from fxml file
		return fxmlLoader.load();
	}


	/**
	 * The program run as a cli application
	 */
	public void cli(){

	}

	public static void main(String[] args) {
		//init GUI
		launch(args);

		//NO MORE CODE GOES HERE IT NEEDS TO BE INVOKED ON A NEW THREAD VIA THE RUN LATER METHOD ON A SEPARATE THREAD
	 }
}

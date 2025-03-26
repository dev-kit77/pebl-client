//package specification
package app.pebl;

//imports
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Main class for GUI init
 */
public class Main extends Application {
	/**
	 * start() method for the GUI. Initializes basic Stages.
	 *
	 * @param PrimaryStage The main stage of the program
	 * @throws IOException passthrough from fxml loader
	 */
	@Override
	public void start(Stage PrimaryStage) throws IOException {
		//init main stage
		PrimaryStage = new Stage();
		PrimaryStage.setTitle("Login");
		PrimaryStage.setScene(new Scene(loadFXML("posts"), 450, 600));
		PrimaryStage.show();

		//init connections
		Stage friendsStage = new Stage();
		friendsStage.setTitle("Friends");
		friendsStage.setScene(new Scene(loadFXML("connections"), 200, 500));
		friendsStage.setResizable(false);
		friendsStage.show();

		//init profile
		Stage profileStage = new Stage();
		profileStage.setTitle("Profile");
		profileStage.setScene(new Scene(loadFXML("profile"), 400, 350));
		profileStage.show();

		//init login prompt
		Stage loginStage = new Stage();
		loginStage.setTitle("Login");
		loginStage.setScene(new Scene(loadFXML("login"), 250, 300));
		loginStage.showAndWait();
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

	private static void cache(){ //TODO this.
		//this shouldn't be here???
	}

	public static void main(String[] args) {
		//init GUI
		launch(args);

		//NO MORE CODE GOES HERE IT NEEDS TO BE INVOKED ON A NEW THREAD VIA THE RUN LATER METHOD ON A SEPARATE THREAD
	 }
}

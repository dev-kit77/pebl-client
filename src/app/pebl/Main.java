//package specification
package app.pebl;

//imports
import app.pebl.connections.ConnectionsCtrl;
import app.pebl.profile.ProfileCtrl;
import app.pebl.profile.User;
import app.pebl.prompts.LeaderboardCtrl;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main class for GUI init
 */
public class Main extends Application {
	//Fields
	private static Stage primaryStage;
	private final static ExecutorService executor = Executors.newSingleThreadExecutor();

	/**
	 * start() method for the GUI. Initializes the login GUI.
	 *
	 * @param stage The main stage of the program
	 * @throws IOException passthrough from fxml loader
	 */
	@Override
	public void start(Stage stage) throws IOException {
		showLogin(stage);

		//test user
		Config.getInstance().setCurrentUser(new User("kitty", 27773, new ArrayList<String>(),new ArrayList<String>(), "I love cats", 18, false));
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

		executor.execute(shutdownTask);
	}

	public static Stage initProfile(String username) throws IOException {
		//init stage
		Stage stage = new Stage();
		stage.setTitle("Profile");

		//get loader and load scene
		FXMLLoader loader = getFXML("profile");
		stage.setScene(new Scene(loader.load()));

		//init user
		User displayUser;

		//get user from server
		Task<Void> getUser = new Task<>() {
			@Override public Void call() {
				//code goes here

				//empty return
				return null;
			}
		};

		executor.execute(getUser);

		//get controller
		ProfileCtrl ctrl = (ProfileCtrl) loader.getController();
		//init controller user
		//ctrl.setUser(displayUser);

		//return window to show
		return stage;
	}

	public static Stage initProfile(User displayUser) throws IOException {
		//init stage
		Stage stage = new Stage();
		stage.setTitle("Profile");

		//get loader and load scene
		FXMLLoader loader = getFXML("profile");
		stage.setScene(new Scene(loader.load()));

		//get controller
		ProfileCtrl ctrl = (ProfileCtrl) loader.getController();
		//init controller user
		ctrl.setUser(displayUser);

		//return window to show
		return stage;
	}

	public static Stage initConnections(User displayUser) throws IOException {
		Stage stage = new Stage();
		stage.setTitle("Connections");

		//get loader and load scene
		FXMLLoader loader = getFXML("connections");
		stage.setScene(new Scene(loader.load()));

		//get controller
		ConnectionsCtrl ctrl = (ConnectionsCtrl) loader.getController();
		ctrl.setUser(displayUser);

		//return window to show
		return stage;
	}

	public static Stage initLeaderboard(User displayUser) throws IOException {
		Stage stage = new Stage();
		stage.setTitle("Leaderboard");

		//get loader and load scene
		FXMLLoader loader = getFXML("leaderboard");
		stage.setScene(new Scene(loader.load()));

		//get controller
		LeaderboardCtrl ctrl = (LeaderboardCtrl) loader.getController();
		ctrl.setUser(displayUser);

		//return window to show
		return stage;
	}

	private static Stage initPosts() throws IOException {
		//init stage
		Stage stage = new Stage();
		stage.setTitle("Latest Posts");
		//load fxml
		stage.setScene(new Scene(getFXML("posts").load()));

		//return window to show
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

		//init leaderboard window
		Stage leaderboardStage = initLeaderboard(Config.getInstance().getCurrentUser());
		leaderboardStage.initOwner(mainStage);
		leaderboardStage.show();
	}

	public static void showLogin(Stage stage) throws IOException {
		//init main stage
		stage = new Stage();
		stage.setTitle("Login");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(new Scene(getFXML("login").load(), 250, 300));
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
	public static FXMLLoader getFXML(String fxml) throws IOException {
		//get fxml file from the fxml folder with filename in the parameter
		return new FXMLLoader(Main.class.getResource("res/fxml/" + fxml + ".fxml"));
	}

	public static ExecutorService getExecutor() {
		return executor;
	}

	public static void main(String[] args) {
		//init GUI
		launch(args);

		//NO MORE CODE GOES HERE IT NEEDS TO BE INVOKED ON A NEW THREAD VIA THE RUN LATER METHOD ON A SEPARATE THREAD
	 }
}

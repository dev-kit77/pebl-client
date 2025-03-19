package app.pebl;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
	
	private static Scene scene;

	@Override
	public void start(Stage stage) throws IOException {
		scene = new Scene(loadFXML("login"), 250, 300);
		stage.setTitle("Login");
		stage.setScene(scene);
		stage.show();
	}

	static void setRoot(String fxml) throws IOException {
		scene.setRoot(loadFXML(fxml));
	}

	private static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("res/fxml/" + fxml + ".fxml"));
		return fxmlLoader.load();
	}

	// public static void main(String[] args) {
	// 	// launch(args);
	// 	Connect connect = new Connect();
	// 	connect.gethtml();
	// }
}

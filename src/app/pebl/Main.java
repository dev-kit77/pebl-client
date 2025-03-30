//package specification
package app.pebl;

//imports
import app.pebl.profile.User;
import app.pebl.posts.Post;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

/**
 * Main class for GUI init
 */
public class Main extends Application {
	//Fields
	private static Stage primaryStage;
	private final static Connect connect = new Connect();

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
	private static Parent loadFXML(String fxml) throws IOException {
		//get fxml file from the fxml folder with filename in the parameter
		FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("res/fxml/" + fxml + ".fxml"));

		//return javafx node from fxml file
		return fxmlLoader.load();
	}

	//Methods for kit to use
	/**
	 * Method to parse a JSONObject of data of a user to a User object
	 * @param response JSONObject
	 * @return User object
	 */
	private static User parseUser(JSONObject response) {
		User user =  new User(response.get("username").toString(), Integer.parseInt(response.get("skips").toString()), new ArrayList<String>((JSONArray) (response.get("followers"))), new ArrayList<String>((JSONArray) (response.get("following"))), response.get("status").toString(), Integer.parseInt(response.get("age").toString()), Boolean.parseBoolean(response.get("gender").toString()));
		user.setLocation(response.get("location").toString());
		return user;
	}

	/**
	 * Method to parse a JSONObject of data of a post to a Post object
	 * @param response JSONObject
	 * @return Post object
	 */
	private static Post parsePost(JSONObject response) {
		return new Post(Integer.parseInt(response.get("id").toString()), response.get("author").toString(), response.get("content").toString(), Integer.parseInt(response.get("likes").toString()), Long.parseLong(response.get("time").toString()));
	}

	//Methods for kit to use

	/**
	 * Method to register a new user
	 * @param username String
	 * @param password String
	 * @param age integer
	 * @param gender boolean
	 * @return True if the register was successful and False if it failed.
	 * @throws IOException in case of IOException
	 * @throws InterruptedException if the http request was interrupted
	 */
	private static boolean register(String username, String password, int age, boolean gender) throws IOException, InterruptedException {
		JSONObject obj = new JSONObject();
		obj.put("username", username);
		obj.put("password", password);
		obj.put("age", age);
		obj.put("gender", gender);
		JSONObject response = connect.request("register", obj);
		if (response != null) {
			User temp = getProfile(username);
			if (temp != null) {
				Config.getInstance().setCurrentUser(temp);
				Config.getInstance().getCurrentUser().setLocation(temp.getLocation());
				return true;
			}


		}
		System.err.println("ERROR: Could not register user");
		return false;
	}

	/**
	 * Method to log in an existing user by entering their username and password
	 * @param username String
	 * @param password String
	 * @return True if login was successful and False if it failed
	 * @throws IOException in case of IOException
	 * @throws InterruptedException if the http request was interrupted
	 */
	private static boolean login(String username, String password) throws IOException, InterruptedException {
		JSONObject obj = new JSONObject();
		obj.put("username", username);
		obj.put("password", password);
		JSONObject response = connect.request("auth", obj);
		return  (response != null);
	}

	/**
	 * Method to check if the server is online
	 * @return True if server is online and False if it is offline
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static boolean checkServer() throws IOException, InterruptedException {
		JSONObject obj = new JSONObject();
		JSONObject response = connect.request("checkOnline", obj);
		return  (response != null);
	}

	/**
	 * Method to check if the auth token is valid
	 * @return True if the token is valid and False if it is invalid
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static boolean checkAuth() throws IOException, InterruptedException {
		JSONObject obj = new JSONObject();
		JSONObject response = connect.request("checkAuth", obj);
		return  (response != null);
	}

	/**
	 * Method to view the profile of a user specified by their username
	 * @param username String
	 * @return User object if successful or null if failed
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static User getProfile(String username) throws IOException, InterruptedException {
		JSONObject obj = new JSONObject();
		obj.put("username", username);
		JSONObject response = connect.request("profileGet", obj);
		if (response != null) {
		User profile = new User(response.get("username").toString(), Integer.parseInt(response.get("skips").toString()), new ArrayList<String>((JSONArray) (response.get("followers"))), new ArrayList<String>((JSONArray) (response.get("following"))), response.get("status").toString(), Integer.parseInt(response.get("age").toString()), Boolean.parseBoolean(response.get("gender").toString()));
		profile.setLocation(response.get("location").toString());
		return profile;
		}

		else {
			return null;
		}
	}

	/**
	 * method to update profile, at the end it calls
	 * @param age int
	 * @param gender boolean
	 * @param status String
	 * @return true if success, false if fail
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static boolean updateProfile(int age, boolean gender, String status) throws IOException, InterruptedException {
		JSONObject obj = new JSONObject();
		//if age is 0 use current age
		if (age == 0){
		obj.put("age", Config.getInstance().getCurrentUser().getAge());

		}
		else {
			obj.put("age", age);
		}
		//gender is a switch, no need to check anything
		obj.put("gender", gender);
		// if status is empty, use current status
		if (status.equals("")){
			obj.put("status", Config.getInstance().getCurrentUser().getStatus());
		}
		else {
			obj.put("status", status);
		}
		JSONObject response = connect.request("updateProfile", obj);
		if (response != null) {
			System.out.println("updateProfile response: " + response.toJSONString());
			User temp;
			if ((temp = getProfile(Config.getInstance().getCurrentUser().getUsername())) != null) {
				Config.getInstance().setCurrentUser(temp);
				Config.getInstance().getCurrentUser().setLocation(temp.getLocation());
				return true;
			}
			else {
				System.err.println("Error fetching updated profile");
				return false;
			}
		}
		else {
			System.err.println("Error updating profile");
			return false;
		}
	}

	/**
	 *Method to get a specific post by its id.
	 * @param id integer id of post
	 * @return the Post object if successful, or null if failed
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static Post getPost(int id) throws IOException, InterruptedException {
		JSONObject obj = new JSONObject();
		obj.put("id", id);
		JSONObject response = connect.request("getPost", obj);
		if (response != null) {

			return new Post(Integer.parseInt(response.get("id").toString()), response.get("author").toString(), response.get("content").toString(), Integer.parseInt(response.get("likes").toString()), Long.parseLong(response.get("time").toString()));
		}
		System.err.println("Error fetching post");
		return null;
	}

	/**
	 * Method to create a post
	 * @param content String of content
	 * @return true if successful, false if failed
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static boolean createPost(String content) throws IOException, InterruptedException {
		JSONObject obj = new JSONObject();
		obj.put("content", content);
		JSONObject response = connect.request("postCreate", obj);
		if (response != null) {
			System.out.println("Post created: " + response.toJSONString());
			//update the profile with the new post
			User temp = getProfile(Config.getInstance().getCurrentUser().getUsername());
			if (temp != null) {
			Config.getInstance().setCurrentUser(temp);
			}
			return true;
		}
		else {
			System.err.println("Error creating post");
			return false;
		}

	}


	/**
	 * Method to get feed, returns the latest 50 posts in teh feed
	 * @return Array list of at max 50 Post objects
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static ArrayList<Post> getFeed() throws IOException, InterruptedException {
		JSONObject obj = new JSONObject();
		JSONObject response = connect.request("feed", obj);
		if (response != null) {
			System.out.println("Feed fetched");
			ArrayList<Post> posts = new ArrayList<Post>();
			JSONArray postsArray = (JSONArray) response.get("feed");
			for (int i = 0; i < postsArray.size(); i++) {
				JSONObject post = (JSONObject) postsArray.get(i); //convert jsonarray element object to json object
				//create post object and add it to posts array list
				posts.add(new Post(Integer.parseInt(response.get("id").toString()), response.get("author").toString(), response.get("content").toString(), Integer.parseInt(response.get("likes").toString()), Long.parseLong(response.get("time").toString())));
			}
			return posts; //return teh array list
		}
		else {
			System.err.println("Error fetching feed");
			return null;
		}
	}

	/**
	 * Method to fetch leaderboard
	 * @return ArrayList of User objects
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static ArrayList<User> leaderboard() throws IOException, InterruptedException {
		JSONArray response = connect.leaderboard();
		if (response != null) {
			System.out.println("Leaderboard fetched");
			ArrayList<User> users = new ArrayList<User>();
			for (int i = 0; i < response.size(); i++) {
				JSONObject user = (JSONObject) response.get(i); //convert the jsonarray element object to json object
				//create user object and add it to users array list
				users.add(parseUser(user));

			}
			return users;
		}
		else {
			System.err.println("Error fetching leaderboard");
			return null;
		}
	}

	/**
	 *Method to follow another user.
	 * @param username String of the username you want to follow
	 * @return True is successfully following, false if action failed
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static boolean follow(String username) throws IOException, InterruptedException {
		JSONObject obj = new JSONObject();
		obj.put("username", username);
		JSONObject response = connect.request("follow", obj);
		if (response != null) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Method to like a post
	 * @param id Integer id of the post
	 * @return True if liked successfully, false if action failed.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static boolean like(int id) throws IOException, InterruptedException {
		JSONObject obj = new JSONObject();
		obj.put("id", id);
		JSONObject response = connect.request("like", obj);
		if (response != null) {
			return true;

		}
		else {
			return false;
		}
	}

	//TODO cli
	private static void cli() {

	}

	public static void main(String[] args) {
		//init GUI
		launch(args);

		//NO MORE CODE GOES HERE IT NEEDS TO BE INVOKED ON A NEW THREAD VIA THE RUN LATER METHOD ON A SEPARATE THREAD
	 }
}

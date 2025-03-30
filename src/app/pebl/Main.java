//package specification
package app.pebl;

//imports
import app.pebl.connections.ConnectionsCtrl;
import app.pebl.profile.ProfileCtrl;
import app.pebl.profile.User;
import app.pebl.posts.Post;
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
import java.util.InputMismatchException;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/**
 * Main class for GUI init
 */
@SuppressWarnings("JavadocDeclaration")
public class Main extends Application {
	//Fields
	private static Stage primaryStage;
	private final static Connect connect = new Connect();
	private static ArrayList<Post> feed;
	private static User viewedUser = null;
	private static ArrayList<User> leaderboard = null;
	private final static ExecutorService executor = Executors.newSingleThreadExecutor();

    static {
        try {
            feed = (checkServer()) ? getFeed() : null;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

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

	//Methods for kit to use
	/**
	 * Method to parse a JSONObject of data of a user to a User object
	 * @param response JSONObject
	 * @return User object
	 */
	public static User parseUser(JSONObject response) {
		User user =  new User(response.get("username").toString(), Integer.parseInt(response.get("skips").toString()), new ArrayList<String>((JSONArray) (response.get("followers"))), new ArrayList<String>((JSONArray) (response.get("following"))), response.get("status").toString(), Integer.parseInt(response.get("age").toString()), Boolean.parseBoolean(response.get("gender").toString()), new ArrayList<Integer>((JSONArray)response.get("posts")), response.get("location").toString());
		return user;
	}

	/**
	 * Method to parse a JSONObject of data of a post to a Post object
	 * @param response JSONObject
	 * @return Post object
	 */
	public static Post parsePost(JSONObject response) {
		return new Post(Integer.parseInt(response.get("id").toString()), response.get("author").toString(), response.get("content").toString(), Integer.parseInt(response.get("likes").toString()), Long.parseLong(response.get("time").toString()));
	}

	public static JSONObject getUserAsJSON(User user) throws IOException, InterruptedException {
		JSONObject json = new JSONObject();
		json.put("username", user.getUsername().toLowerCase());
		return connect.request("profileGet", json);
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
		obj.put("username", username.toLowerCase());
		obj.put("password", password);
		obj.put("age", age);
		obj.put("gender", gender);
		JSONObject response = connect.request("register", obj);
		if (response != null) {
			User temp = getProfile(username.toLowerCase());
			if (temp != null) {
				Config.getInstance().setCurrentUser(temp);
				return true;
			}


		}
		System.err.println("ERROR: Could not register user");
		return false;
	}

	/**
	 * Method to log in an existing user by entering their username and password, username gets put to lowercase
	 * @param username String
	 * @param password String
	 * @return True if login was successful and False if it failed
	 * @throws IOException in case of IOException
	 * @throws InterruptedException if the http request was interrupted
	 */
	private static boolean login(String username, String password) throws IOException, InterruptedException {
		JSONObject obj = new JSONObject();
		obj.put("username", username.toLowerCase());
		obj.put("password", password);
		JSONObject response = connect.request("auth", obj);
		User temp = getProfile(username.toLowerCase());
		if (temp != null) {
			Config.getInstance().setCurrentUser(temp);
		}
		else {
			System.err.println("ERROR: Could not login user");
		}
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
	public static boolean checkAuth() throws IOException, InterruptedException {
		JSONObject obj = new JSONObject();
		JSONObject response = connect.request("checkAuth", obj);
		return  (response != null);
	}

	/**
	 * Method to view the profile of a user specified by their username which is put to lowercase
	 * @param username String
	 * @return User object if successful or null if failed
	 * @throws IOException
	 * @throws InterruptedException
	 */

	public static User getProfile(String username) throws IOException, InterruptedException {
		JSONObject obj = new JSONObject();
		obj.put("username", username.toLowerCase());
		JSONObject response = connect.request("profileGet", obj);
		if (response != null) {
		User profile = parseUser(response);
			if (!response.get("username").equals(Config.getInstance().getCurrentUser().getUsername())) {
				Config.getInstance().getCurrentUser().setMutuals(profile);
				viewedUser = profile;
			}
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
		obj.put("gender", ""+gender);
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
			if ((temp = getProfile(Config.getInstance().getCurrentUser().getUsername().toLowerCase())) != null) {
				Config.getInstance().setCurrentUser(temp);
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
			User temp = getProfile(Config.getInstance().getCurrentUser().getUsername().toLowerCase());
			if (temp != null) {
			Config.getInstance().setCurrentUser(temp);
			}
			leaderboard = leaderboard();
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
			feed = posts;
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
		JSONObject obj = new JSONObject(); //TODO test follow
		obj.put("username", username.toLowerCase());
		JSONObject response = connect.request("follow", obj);
		if (response != null) {
			User temp = getProfile(Config.getInstance().getCurrentUser().getUsername().toLowerCase());
			if (temp != null) {
				Config.getInstance().setCurrentUser(temp);
			}
			else {
				System.err.println("Error updating user profile with new followed username");
				return false;
			}
			return true;
		}
		else {
			System.err.println("Error following user");
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
		JSONObject obj = new JSONObject(); //TODO test like
		obj.put("id", id);
		JSONObject response = connect.request("like", obj);
		if (response != null) {
			System.out.println("Like successfully");
			return true;

		}
		else {
			System.err.println("Error liking post");
			return false;
		}
	}

	/**
	 * Method to check if there is current user data stored in Config
	 * @return True if there is user data stored, False if it is not stored.
	 */
	private static boolean checkCurrentUser(){
		if (Config.getInstance().getCurrentUser() != null || !Config.getInstance().getCurrentUser().getUsername().equals("null")) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Method that displays the leaderboard as
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void displayLeaderboard() throws IOException, InterruptedException {
		if (leaderboard != null) {
			for (User user : leaderboard) {
				System.out.println("Name: "+user.getUsername()+", Skips: "+user.getSkips());
			}
		}
		else {
			System.err.println("You haven't created your first post of the day");
		}
	}

	/**
	 * Method to display post
	 * @param post Post object
	 */
	private static void displayPost(Post post) {
		if (post != null) {
			System.out.println("Id: "+post.getId());
			System.out.println("Author: "+post.getSender());
			System.out.println("Content: "+post.getId());
			System.out.println("Skips: "+post.getId());
			System.out.println("Date: "+post.getId());

		}
		else {
			System.err.println("null post");
		}
	}

	/**
	 * Method to display currently viewed user
	 */
	private static void displayUser(){
		if (viewedUser != null) {
			System.out.println("Username: "+viewedUser.getUsername());
			System.out.println("Skips: "+viewedUser.getSkips());
			System.out.println("Followers "+viewedUser.getFollowers().toString());
			System.out.println("Following "+viewedUser.getFollowing().toString());
			System.out.println("Status: "+viewedUser.getStatus());
			System.out.println("Age: "+viewedUser.getAge());
			System.out.println("Gender: "+viewedUser.getGender());
			System.out.println("Posts: "+viewedUser.getPosts().toString());
			System.out.println("Location: "+viewedUser.getLocation());
			System.out.println("Mutual friends "+Config.getInstance().getCurrentUser().getMutuals().toString());

		}
		else {
			System.err.println("null user");
		}
	}

	/**
	 * Method to display the feed
	 */
	private static void displayFeed() {
		if (feed != null) {
			for (Post post : feed) {
				System.out.println("-------------------------------------------------------------");
				displayPost(post);
				System.out.println("-------------------------------------------------------------");
			}
		}
		else {
			System.err.println("null feed");
		}
	}

	/**
	 * The app but in cli form
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void cli() throws IOException, InterruptedException {
		Scanner input = new Scanner(System.in);
		System.out.print("Hello! and welcome to Pebl.social!");
		String choice;
		String username;
		String password;
		int age;
		boolean gender;
		String content;
		String id;
		String status;

		//if completely new user
		if (!checkCurrentUser() && (Config.getInstance().getAuthToken() == null)) {
			System.out.println("You are not yet register, lets get that started");

			System.out.println("Please enter your new username or type /exit to exit: ");
			username = input.next();
			System.out.println("Please enter your new password or type /exit to exit: ");

			password = input.nextLine();
			System.out.println("Please enter your new age (integers only) or type /exit to exit: ");
			age = input.nextInt();
			input.nextLine();
			System.out.println("Please enter your gender (true or false) or type /exit to exit: ");
			gender = input.nextBoolean();
			input.nextLine();
			if (username.equals("/exit") || password.equals("/exit")) {
				return;
			}
			else {
				if (!register(username, password, age, gender)) {
					System.err.println("Error registering user");
					return;
				}
			}



		}
		// if was logged in before
		else if (Config.getInstance().getAuthToken() != null || !(Config.getInstance().getCurrentUser() == null)) {
			if (!checkAuth()){
				System.err.println("You have been logged out.");
				System.out.println("Please enter your new username or type /exit to exit: ");
				username = input.next();
				System.out.println("Please enter your new password or type /exit to exit: ");

				password = input.nextLine();
				if (username.equals("/exit") || password.equals("/exit")) {
					return;
				}

				if (!login(username, password)) {
					System.err.println("Error logging in user");
					return;
				}
				System.out.println("Welcome back!");

			}
		}
		System.out.println("What would you like to do? Reminder: Leaderboards can only be viewed and refreshed after you create a post\nType a number to select of the following\n1: create post, 2: view specific post, 3: view profile of a user (including yourself), 4: view leaderboard, 5: Like a specific post, 6: Follow a specific user, 7: change your profile (age, gender and status), 8: View the latest 50 posts in the feed, 9: exit");
		choice = input.next();
		switch (choice) {
			case "1":
				System.out.println("Enter content for post or type /exit to cancel: ");
				content = input.next();
				if (!content.equals("/exit")) {
					createPost(content);
				}
				break;
			case "2":
				System.out.println("Enter id of post or type /exit to cancel: ");
				id = input.nextLine();
				if (!id.equals("/exit")) {
					displayPost(getPost(Integer.parseInt(id)));
				}
				break;
			case "3":
				System.out.println("Enter username or type /exit to cancel: ");
				username = input.nextLine();
				if (!username.equals("/exit")) {
					getProfile(username);
					displayUser();
				}
				break;
			case "4":
				displayLeaderboard();
				break;
			case "5":
				System.out.println("Enter id of post you want to offer a skip (like) or type /exit to cancel: ");
				id = input.nextLine();
				if (!id.equals("/exit")) {
					like(Integer.parseInt(id));
				}
				break;
			case "6":
				System.out.println("Enter username you want ot follow or type /exit to cancel: ");
				username = input.nextLine();
				if (!username.equals("/exit")) {
					follow(username);
				}
				break;
			case "7":
				System.out.println("Enter your new age (integers only) or type /exit to cancel: ");
				try {
					age = input.nextInt();
					input.nextLine();
				System.out.println("Enter your new gender (true or false only) or type /exit to cancel: ");
				gender = input.nextBoolean();
				input.nextLine();
				System.out.println("Enter your new status or type /exit to cancel: ");
				status = input.nextLine();
				updateProfile(age, gender, status);
				} catch (InputMismatchException e) {
					System.err.println("You have entered an invalid value for age or gender, please try again later.");

				}
				break;
			case "8":
				if (getFeed() != null) {
					displayFeed();
				}
				break;
			case "9":
				System.out.println("See you next time!");
				Config.getInstance().save(".pebl.cfg");
				break;
			default:
				System.err.println("Invalid choice");
				break;

		}
	}

	public static void main(String[] args) {
		//init GUI
		launch(args);

		//NO MORE CODE GOES HERE IT NEEDS TO BE INVOKED ON A NEW THREAD VIA THE RUN LATER METHOD ON A SEPARATE THREAD
	 }
}

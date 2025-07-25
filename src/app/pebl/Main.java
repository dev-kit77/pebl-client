//package specification
package app.pebl;

//imports
import app.pebl.connections.ConnectionsCtrl;
import app.pebl.profile.ProfileCtrl;
import app.pebl.data.User;
import app.pebl.data.Post;
import app.pebl.prompts.LeaderboardCtrl;
import app.pebl.util.Config;
import app.pebl.util.Connect;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/**
 * Main class for GUI init
 */
public class Main extends Application {
	//class fields
	private static Stage primaryStage;
	public static ArrayList<Post> feed;
	public static ArrayList<Post> filteredFeed; //used to temporarily store filtered feed
	public static User viewedUser = null; //user that is not currentUser data is loaded here
	public static ArrayList<User> leaderboard = null;
	public static ArrayList<String> closeFriends  = null;

	//init static objects
	private final static Connect connect = new Connect();
	private final static ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
	 * start() method for the GUI. Initializes the login GUI.
	 *
	 * @param stage The main stage of the program
	 */
	@Override
	public void start(Stage stage) {
		Task<Void> startupTask = new Task<>() {
			@Override
			public Void call() {
			//init bypassLogin
			boolean bypassLogin = false;

			//this code was the auto login but unfortunately it doesn't work due to the server setup atm
//			try {
//				//check auth token
//				bypassLogin = checkAuth();
//			} catch (Exception e) {
//				//print stack to log
//				e.printStackTrace();
//
//				//update GUI
//				Platform.runLater(new Runnable() {
//					@Override
//					public void run() {
//						//show general error
//						Alert alert = new Alert(Alert.AlertType.ERROR);
//						alert.setTitle("Error");
//						alert.setHeaderText("Exception in pebl client");
//						alert.setContentText(e.getMessage());
//						alert.showAndWait();
//
//						//exit program
//						Platform.exit();
//					}
//				});
//			}

			//update gui depending on login
			if (bypassLogin) {
				try {
					//get username from config file and load in user to config object
					Config.getInstance().setCurrentUser(getProfile(Config.getInstance().getUsername()));
				} catch (Exception e) {
						//print stack to log
						e.printStackTrace();

						//update GUI
						Platform.runLater(() -> {
							//show general error
							Alert alert = new Alert(Alert.AlertType.ERROR);
							alert.setTitle("Error");
							alert.setHeaderText("Exception in pebl client");
							alert.setContentText(e.getMessage());
							alert.showAndWait();

							//exit program
							Platform.exit();
						});
					}

					//update gui
					Platform.runLater(() -> {
						try	{
							//show main windows
							showMainWindows();
						} catch (Exception e) {
							//print stack to log
							e.printStackTrace();

							//show general error
							Alert alert = new Alert(Alert.AlertType.ERROR);
							alert.setTitle("Error");
							alert.setHeaderText("Exception in pebl client");
							alert.setContentText(e.getMessage());
							alert.showAndWait();

							//exit program
							Platform.exit();
						}
					});
				}
			else {
				//update gui
				Platform.runLater(() -> {
					try	{
						//show main windows
						showLogin(primaryStage);
					} catch (Exception e) {
						//print stack to log
						e.printStackTrace();

						//show general error
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setTitle("Error");
						alert.setHeaderText("Exception in pebl client");
						alert.setContentText(e.getMessage());
						alert.showAndWait();

						//exit program
						Platform.exit();
					}
				});
			}


			//empty return
			return null;
			}
		};

		//execute task
		executor.execute(startupTask);

	}

	/**
	 * Stop method called on GUI exit. Saves the config file then safely wraps up application threads.
	 */
	@Override
	public void stop() {
		Task<Void> shutdownTask = new Task<>() {
			@Override public Void call() {
				//update GUI with wait cursor
				Platform.runLater(() -> {
					//show general error
					primaryStage.getScene().setCursor(Cursor.WAIT);
				});

				//save config file
				boolean success = Config.getInstance().save(".pebl.cfg");

				if (success) {
					System.out.println("Config Saved Successfully");
				}
				else {
					System.out.println("Config Saving Failed");
				}

				//update GUI with default cursor
				Platform.runLater(() -> {
					//show general error
					primaryStage.getScene().setCursor(Cursor.DEFAULT);
				});

				//empty return
				return null;
			}
		};

		//execute task
		executor.execute(shutdownTask);

		//shutdown executor
		try {
			executor.shutdown();
			executor.awaitTermination(120, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static Stage initProfile(String username) throws IOException {
		//init stage
		Stage stage = new Stage();
		stage.setTitle("Profile");

		//get loader and load scene
		final FXMLLoader loader = getFXML("profile");
		stage.setScene(new Scene(loader.load()));

		//get user from server
		Task<Void> getUser = new Task<>() {
			@Override public Void call() {
				//update GUI with wait cursor
				Platform.runLater(() -> {
					//show general error
					primaryStage.getScene().setCursor(Cursor.WAIT);
				});

				try {
					//get user from username
					User viewUser = Main.getProfile(username);

					//update GUI
					Platform.runLater(() -> {
						//get controller
						ProfileCtrl ctrl = (ProfileCtrl) loader.getController();
						//init controller user
						ctrl.setUser(viewUser);
					});
				} catch (Exception e) {
					//print stack trace to console
					e.printStackTrace();

					//update GUI
					Platform.runLater(() -> {
						//show general error
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setTitle("Error");
						alert.setHeaderText("Exception in pebl client");
						alert.setContentText(e.getMessage());
						alert.showAndWait();

						//exit program
						Platform.exit();
					});
				}

				//update GUI with wait cursor
				Platform.runLater(() -> {
					//show general error
					primaryStage.getScene().setCursor(Cursor.DEFAULT);
				});

				//empty return
				return null;
			}
		};

		//execute task
		executor.execute(getUser);

		//return window to show
		return stage;
	}

	public static Stage initProfile(User displayUser) throws IOException {
		//init stage
		Stage stage = new Stage();
		stage.setTitle("Profile");
		stage.initOwner(primaryStage);

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
		//init stage
		Stage stage = new Stage();
		stage.setTitle("Connections");
		stage.initOwner(primaryStage);

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
		//init stage
		Stage stage = new Stage();
		stage.setTitle("Leaderboard");
		stage.initOwner(primaryStage);

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

	public static void showMainWindows() throws IOException {
		//init posts window as main window
		primaryStage = initPosts();
		primaryStage.show();

		//init connections window
		Stage connectionStage = initConnections(Config.getInstance().getCurrentUser());
		connectionStage.initOwner(primaryStage);
		connectionStage.show();

		//init profile window
		Stage profileStage = initProfile(Config.getInstance().getCurrentUser());
		profileStage.initOwner(primaryStage);
		profileStage.show();

		//init leaderboard window
		Stage leaderboardStage = initLeaderboard(Config.getInstance().getCurrentUser());
		leaderboardStage.initOwner(primaryStage);
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
	 */
	public static FXMLLoader getFXML(String fxml) {
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
		//return null incase paramter is null

		if (response == null) {
			System.err.println("Err: Parameter is null");
			return null;
		}

		//init int post id array
		ArrayList<Integer> postIDs = null;

		//init json array
		JSONArray postArr = (JSONArray) response.get("posts");

		//check post ids is not null
		if (postArr != null) {
			postIDs = new ArrayList<Integer>();

			//loop for all items in json array
			for (int i = 0; i < postArr.size(); i++) {
				//convert json string value to int and add to post id arraylist
				postIDs.add(Integer.parseInt(postArr.get(i).toString()));
			}
		}

        return new User(response.get("username").toString(), Integer.parseInt(response.get("skips").toString()), new ArrayList<String>((JSONArray) (response.get("followers"))), new ArrayList<String>((JSONArray) (response.get("following"))), response.get("status").toString(), Integer.parseInt(response.get("age").toString()), Boolean.parseBoolean(response.get("gender").toString()), postIDs, response.get("location").toString());
	}

	/**
	 * Method to parse a JSONObject of data of a post to a Post object
	 * @param response JSONObject
	 * @return Post object
	 */
	public static Post parsePost(JSONObject response) {
		//return null incase paramter is null

		if (response == null) {
			System.err.println("Err: Parameter is null");
		}

		return new Post(Integer.parseInt(response.get("id").toString()), response.get("author").toString(), response.get("content").toString(), Integer.parseInt(response.get("likes").toString()), Long.parseLong(response.get("time").toString()));
	}

	/**
	 * Method to get the JSON representation of a User object
	 * @param user String
	 * @return JSONObject representation of the User object
	 */
	public static JSONObject getUserAsJSON(User user) {
		//return null incase paramter is null
		if (user == null) {
			System.err.println("Err: User is null");
			return null;
		}
		//create the temporary JSONObject
		JSONObject json = new JSONObject();

		//create a temporary JSONArray to convert ArrayLists to JSONArrays
		JSONArray jsonArray = new JSONArray();

		//Populate it
		json.put("username", user.getUsername().toLowerCase());
		json.put("gender", ""+user.getGender());
		json.put("age", user.getAge());
		json.put("location", user.getLocation());
		json.put("status", user.getStatus());
		json.put("skips", user.getSkips());

		//add the followers, put in json, clear the JSONArray afterward and repeat with "following"
		jsonArray.addAll(user.getFollowers());
		json.put("followers", jsonArray);

		//add the following, put in json, clear the JSONArray afterward and repeat with posts
		jsonArray = new JSONArray();
		jsonArray.addAll(user.getFollowing());
		json.put("following", jsonArray);

		//add the following, put in json, clear the JSONArray afterward
		jsonArray = new JSONArray();
		jsonArray.addAll(user.getPosts());
		json.put("posts", jsonArray);

		//return the json object
		return json;
	}

	//Methods for kit to use

	/**
	 * Method to register a new user
	 * @param username String
	 * @param password String
	 * @param age integer
	 * @param gender boolean
	 * @return True if the register was successful and False if it failed.
	 */
	public static boolean register(String username, String password, int age, boolean gender) {

		//create the body object to be passed into connect.request() and populate it with necessary fields
		JSONObject obj = new JSONObject();
		obj.put("username", username.toLowerCase());
		obj.put("password", password);
		obj.put("age", age);

		//convert gender to String
		obj.put("gender", ""+gender);

		//send the request
		JSONObject response = connect.register(obj);

		//if the process was successful
		if (response != null) {
			//update the current user by calling getProfile
			getProfile(username.toLowerCase());
			System.out.println("Registered user: " + username);
			return true;
		}
		//if fail
		else {
			System.err.println("Err: Failed to register user: " + username);
			return false;
		}
	}

	/**
	 * Method to log in an existing user by entering their username and password, username gets put to lowercase
	 * @param username String
	 * @param password String
	 * @return True if login was successful and False if it failed
	 */
	public static boolean login(String username, String password) {
		//Create body for request and populate it
		JSONObject obj = new JSONObject();
		obj.put("username", username.toLowerCase());
		obj.put("password", password);

		//send request nd store response
		JSONObject response = connect.auth(obj);

		//if successful
		if (response != null) {
				// fetch profile data
				//update the currentUser by calling getProfile
				getProfile(username.toLowerCase());
				System.out.println("Logged in");
				return true;
		}
		//fail
		else {
			System.err.println("Err: Could not login user");
		}
		//return false if fail
		return false;
	}

	/**
	 * Method to check if the server is online
	 * @return True if server is online and False if it is offline
	 */
	public static boolean checkServer() {
		//ping server, returns true if successful, false if failed
		return (connect.ping() != null);
	}

	/**
	 * Method to check if the auth token is valid
	 * @return True if the token is valid and False if it is invalid
	 */
	public static boolean checkAuth() {
		//create body json object to pass to request
		JSONObject obj = new JSONObject();

		//adds required fields in the JSONObject
		//body structure: {"error":"none", "success": "true","token": the auth token}
		obj.put("token", Config.getInstance().getAuthToken());
		obj.put("error", "none");
		obj.put("success", "true");

		//send request
		JSONObject response = connect.checkAuth(obj);

		//return true if successful, false if fail
		return (response != null);
	}

	/**
	 * Method to view the profile of a user specified by their username which is put to lowercase
	 * @param username String
	 * @return User object if successful or null if failed
	 */
	public static User getProfile(String username) {
		//create json body for request and populate it
		JSONObject obj = new JSONObject();
		obj.put("target", username.toLowerCase());

		//send request and store the response
		JSONObject response = connect.fetchProfile(obj);

		//if success
		if (response != null) {
			//parse response to a User object
			User profile = parseUser(response);

			try {
				//check if current user is null (that can only happen if user just logged in or registered), in which case, update current user
				if (Config.getInstance().getCurrentUser() == null) {
					Config.getInstance().setCurrentUser(profile);
				}

				//check if profile is current user (current user exists)
				else if (profile.getUsername().equals(Config.getInstance().getCurrentUser().getUsername())) {
					//update current user
					Config.getInstance().setCurrentUser(profile);
				}
			} catch (NullPointerException e) {
				System.err.println("Err: Could not get current user, ignore if first get");
			}

			return profile;
		}
		else {
			//return null if failed.
			return null;
		}
	}

	/**
	 * method to update profile, at the end it calls
	 * @param age int
	 * @param gender boolean
	 * @param status String
	 * @param location String
	 * @return true if success, false if fail
	 */
	public static boolean updateProfile(int age, boolean gender, String status, String location) {
		//create json body for request and populate it
		JSONObject obj = new JSONObject();

		//if age is 0 use current age
		if (age == 0){
			//get current age
			obj.put("age", Config.getInstance().getCurrentUser().getAge());
		}
		else {
			//else use param age
			obj.put("age", age);
		}

		//gender is a switch, no need to check anything
		obj.put("gender", ""+gender);

		// if status is empty, use current status
		if (status.isEmpty()){
			//get current status
			obj.put("status", Config.getInstance().getCurrentUser().getStatus());
		}
		else {
			//else use param status
			obj.put("status", status);
		}

		//add location
		obj.put("location", location);

		//send request and store response
		JSONObject response = connect.updateProfile(obj);

		//success
		if (response != null) {
			System.out.println("Updated profile to include: "+obj);

			//fetch updated profile
			User temp;
			//if getProfile(currentUser's username) is not null
			if ((temp = getProfile(Config.getInstance().getCurrentUser().getUsername().toLowerCase())) != null) {

				//update current user
				Config.getInstance().setCurrentUser(temp);
				return true;
			}
			// fail to fetch profile
			else {
				System.err.println("Err: Could not fetch updated profile");
				return false;
			}
		}
		//fail
		else {
			System.err.println("Err: Could not fetch updated profile");
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
	public static Post getPost(int id) throws IOException, InterruptedException {

		//create json body to pass into request and populate it
		JSONObject obj = new JSONObject();
		obj.put("id", id);

		//send request and save response
		JSONObject response = connect.fetchPost(obj);

		//if success
		if (response != null) {
			//parse json into Post and return it
			return parsePost(response);
		}

		// fail
		System.err.println("Err: Could not fetch post");
		return null;
	}

	/**
	 * Method to create a post
	 * @param content String of content
	 * @return true if successful, false if failed
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static boolean createPost(String content) throws IOException, InterruptedException {
		//create json body to pass into request and populate it
		JSONObject obj = new JSONObject();
		obj.put("content", content);

		//send request and save response
		JSONObject response = connect.createPost(obj);

		//success
		if (response != null) {
			System.out.println("Post created: " + response);

			//update the profile with the new post
			User temp = getProfile(Config.getInstance().getCurrentUser().getUsername().toLowerCase());

			// if fetch successful
			if (temp != null) {
				//update user
				Config.getInstance().setCurrentUser(temp);
			}

			//fetch leaderboard
			leaderboard = leaderboard();
			return true;
		}
		//fail
		else {
			System.err.println("Err: Could not create post");
			return false;
		}

	}

	/**
	 * Method to get feed, returns the latest 50 posts in teh feed
	 * This Method must only be called to fetch new data on feed. The variable feed can be accessed at any time, however, its initial value is null
	 * @return Array list of at max 50 Post objects
	 */
	public static ArrayList<Post> getFeed() {
		//send request and save response
		JSONObject response = connect.fetchFeed();

		//success
		if (response != null) {
			System.out.println("Feed fetched");

			//temporary list to add Post objects from JsonArray of posts in response.
			ArrayList<Post> posts = new ArrayList<Post>();

			//temporary jsonArray to
			JSONArray postsArray = (JSONArray) response.get("feed");

			//iterating over the jsonarray
			for (int i = 0; i < postsArray.size(); i++) {
				//convert jsonarray element object to json object
				JSONObject post = (JSONObject) postsArray.get(i);

				//parse from JSONObject to post object and add it to posts array list
				posts.add(parsePost(post));
			}

			//update feed variable
			feed = posts;

			//return feed
			return feed;
		}
		//fail
		else {
			System.err.println("Err: Could not fetch feed");
			return null;
		}
	}

	/**
	 * Method to fetch leaderboard and update the variable leaderboard.
	 * This Method must only be called to fetch new data on leaderboard. The variable leaderboard can be accessed at any time, however, its initial value is null
	 * @return ArrayList of User objects
	 */
	public static ArrayList<User> leaderboard() {
		//send request and save response
		JSONObject response = connect.fetchLeaderboard();

		//success
		if (response != null) {

			System.out.println("Leaderboard fetched");

			//temporary list to add User objects from JsonArray of users in response
			ArrayList<User> users = new ArrayList<User>();

			//temporary jsonArray to
			JSONArray usersArray = (JSONArray) response.get("leaderboard");

			//iterating over the jsonarray
			for (int i = 0; i < usersArray.size(); i++) {
				//convert jsonarray element object to json object
				JSONObject user = (JSONObject) usersArray.get(i); //convert the jsonarray element object to json object
				//create user object and add it to users array list
				users.add(parseUser(user));
			}
			//update leaderboard variable and return it
			leaderboard = users;

			return leaderboard;
		}
		//fail
		else {
			System.err.println("Err: Could not fetch leaderboard");
			return null;
		}
	}

	/**
	 * Method to toggle following another user.
	 *
	 * @param username String of the username you want to follow
	 * @return True if method was successful, False if method failed
	 */
	public static boolean follow(String username) {
		//create json body to pass into request and populate it
		JSONObject obj = new JSONObject();
		obj.put("target", username.toLowerCase());

		//send request and save response
		JSONObject response = connect.toggleFollow(obj);

		//success
		if (response != null) {
			//create temporary user to update the current user
			User temp = getProfile(Config.getInstance().getCurrentUser().getUsername().toLowerCase());
			//if getting the profile was successful
			if (temp != null) {
				//update currentUser
				Config.getInstance().setCurrentUser(temp);

				// let know in console if the result was following the user or unfollowing the user.
				//if following
				if (Config.getInstance().getCurrentUser().getFollowers().contains(username)) {
					System.out.println("Now Following "+ username);
				}
				//if unfollowing
				else {
					System.out.println("Now Unfollowing "+ username);
				}
				return true;
			}
			// if getting profile failed
			else {
				System.err.println("Err: Could not update user profile with new followed username");

			}
		}
		//fail
		else {
			System.err.println("Err: Could not follow user");
		}
		return false;
	}

	/**
	 * Method to like a post
	 * @param id Integer id of the post
	 * @return True if liked successfully, false if action failed.
	 */
	public static boolean like(int id) {

		//create json body to pass into request and populate it
		JSONObject obj = new JSONObject();
		obj.put("id", id);

		//send request and save response
		JSONObject response = connect.likePost(obj);

		//success
		if (response != null) {
			System.out.println("Liked post!");

			return true;
		}

		//fail
		else {
			System.err.println("Err: Could not like post");
			return false;
		}
	}

	/**
	 * Method to check if there is current user data stored in Config
	 * @return True if there is user data stored, False if it is not stored.
	 */
	public static boolean checkCurrentUser(){
        return Config.getInstance().getCurrentUser() != null;
	}

	/**
	 * Method that displays the leaderboard as
	 */
	private static void displayLeaderboard() {
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
		if (feed != null) { // check if we have the feed stored

			for (Post post : feed) { //display the post objects one by one
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
	 * Method that filters the feed to only include your followers posts or followings posts.
	 * @param query True for "FOLLOWERS ONLY". False for "FOLLOWINGS ONLY"
	 * @return
	 */
	public static ArrayList<Post> filterFeed(boolean query) {
		//init
		filteredFeed = null;

		//check if feed is empty before running
		if (feed != null) {
			//set filtered to feed
			filteredFeed = new ArrayList<Post>(feed);

			//if FOLLOWERS ONLY
			if (query) {
				//remove elements whose authors are not store in current user's follower's list
				filteredFeed.removeIf(element -> !Config.getInstance().getCurrentUser().getFollowers().contains(element.getSender()));
			}
			//if FOLLOWINGS ONLY
			else {
				//remove elements whose authors are not store in current user's following's list
				filteredFeed.removeIf(element -> !Config.getInstance().getCurrentUser().getFollowing().contains(element.getSender()));
			}
		}
		else {
			System.err.println("null feed");
		}

		return filteredFeed;
	}

	/**
	 * Method UPDATES the public static variable closeFriends with an ArrayList of user's close friends (followers who the user follows back)
	 * Access the closeFriends variable for fast return, use this method only to refresh it.
	 * @return True if successfully updated closeFriends, False if it failed
	 */
	public static boolean getCloseFriends() throws IOException, InterruptedException {
		//store the followings list and store it for usage
		ArrayList<String> following = new ArrayList<String>(Config.getInstance().getCurrentUser().getFollowing());

		//get iterator
		Iterator<String> iter = following.iterator();

		//Store close friends here
		ArrayList<String> friends = new ArrayList<String>();

		// for each follow in following list
		while (iter.hasNext()) {
			//call iter.next() and store in a string for later use
			String username = iter.next();


			//create json body for request and populate it
			JSONObject obj = new JSONObject();
			obj.put("username", username);

			//send request and store response
			JSONObject response = connect.fetchProfile(obj);

			//if there was an error in getting profile, abort program
			if (response == null) {
				System.err.println("Err: Could not fetch user profile for close friends fetch");
				return false;
			}

			//get following from response and check if it has current user's username, if yes, add username to close friends
			if (((JSONArray)response.get("following")).contains(Config.getInstance().getCurrentUser().getUsername())) {
				friends.add(username);
			}
		}

		//return close friends and update
		closeFriends = friends;
		return true;
	}

	/**
	 * Method to display the filtered feed in console
	 */
	private static void displayFilterFeed() {
		//check if it is null
		if (filteredFeed != null) { // check if we have the feed stored

			//iterate over its Posts and display them
			for (Post post : filteredFeed) { //display the post objects one by one
				System.out.println("-------------------------------------------------------------");
				displayPost(post);
				System.out.println("-------------------------------------------------------------");
			}
		}
		//if null
		else {
			System.err.println("null filtered feed");
		}
	}


	/**
	 * Method to display the FILTERED feed
	 */
	private static void displayFilteredFeed() {
		if (filteredFeed != null) { // check if we have the feed stored

			for (Post post : feed) { //display the post objects one by one
				System.out.println("-------------------------------------------------------------");
				displayPost(post);
				System.out.println("-------------------------------------------------------------");
			}
		}
		else {
			System.err.println("null filtered feed");
		}
	}


	/**
	 * The app but in cli form
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void cli() throws IOException, InterruptedException {
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

		do {
			System.out.println("Type 1 to log in, Type 2 to register, Type 3 to exit");
			choice = input.nextLine();
			switch (choice) {
				case "1":
					System.out.println("Username: ");
					username = input.nextLine();
					System.out.println("Password: ");
					password = input.nextLine();

					if (!login(username, password)) {
						System.err.println("Invalid username or password");
						continue;
					};

					System.out.println(checkAuth());
					choice = null;
					break;
				case "2":
					System.out.println("Username: ");
					username = input.nextLine();
					System.out.println("Password: ");
					password = input.nextLine();
					System.out.println("Age: ");
					try {
						age = Integer.parseInt(input.nextLine());
					}catch (InputMismatchException e) {
						System.err.println("Please enter only integers");
						continue;
					}
					System.out.println("Gender: ");

					try {
						gender = Boolean.parseBoolean(input.nextLine());
					}catch (InputMismatchException e) {
						System.err.println("Please enter only true or false");
						continue;
					}
					if(!register(username, password, age, gender)) {
						System.err.println("Registration failed");
						continue;
					};


					break;
				case "3":
					System.out.println("See you later!");
					return;
				default:
					System.err.println("Invalid choice");
					break;
			}
		} while (choice != null);

		do {
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
						updateProfile(age, gender, status, "");
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
				case "10":
					System.out.println("enter username");
					username = input.nextLine();
					System.out.println("enter password");
					password = input.nextLine();
					if (username.equals("/exit") || password.equals("/exit")) {
						login(username, password);
					}

					break;
				default:
					System.err.println("Invalid choice");
					break;

			}
		} while (!choice.equals("9"));
	}

	/**
	 * Main Method for Pebl. Calls the javafx launch method.
	 */
	public static void main(String[] args) {
		//init GUI
		launch(args);

		//NO MORE CODE GOES HERE IT NEEDS TO BE INVOKED ON A NEW THREAD VIA THE RUN LATER METHOD ON A SEPARATE THREAD
	 }
}

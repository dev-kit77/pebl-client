package app.pebl;

import app.pebl.profile.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Singleton Class for holding config information
 */
public final class Config {
	private final static Config instance;

    static {
        try {
            instance = new Config();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String authToken = null;
	private User currentUser = null;
	private String serverAddr;
	private boolean showProfile;
	private boolean showConnections;
	private boolean showLeaderboard;
	private boolean saveUserInformation;

	public Config() throws InterruptedException {
		//attempt loading
		boolean success = load(".pebl.cfg");

		//loop for reattempts at file loading
		for (int attempts = 2; !success && attempts > 0; attempts--) {
			//print error message
			System.err.println("\033[0;91m" + "Err: Config load failed Reattempting (Attempts remaining: " + attempts + ")" + "\033[0m");

			//call load method on table object
			success = load(".pebl.cfg");
		}

		//print success message
		if (success) {
			System.out.println("\033[1;92m" + "Config loaded successfully!" + "\033[0m");
		}
		//File loading failed completely: exit
		else {
			//set default config values here
			serverAddr = "https://pebl-api.fly.dev/";

			//print error
			System.err.println("\033[1;91m" + "Err: Config file \".pebl.cfg\" does not exist. Creating..." + "\033[0m");

			//create scanner object
			Scanner scn = new Scanner(System.in);

			//create file object
			File cfgFile = new File(".pebl.cfg");

			try {
				//create file to write to
				cfgFile.createNewFile();

				//save defaults to file
				save(".pebl.cfg");
			}
			catch(IOException e) {
				//print error to handle exception
				System.err.println("\033[0;91m" + "Err: IO error: " + e + "\033[0m");
				System.err.println("\033[1;91m" + "Default Settings will not be saved." + "\033[0m");
			}
		}
	}

	//save/load
	/**
	 * Method to load a config from a file.
	 * Declares the objects required, then proceeds to check that the file exists and that it is writeable, returning false if these checks fail.
	 * Moving further, The program then attempts to read the file, Assigning values to the reader and buffer objects, Declaring some variables to store loading data.
	 * if everything is successful, it closes the object instances and returns true. if an exception is caught it prints an error, then returns false.
	 *
	 * @param filePath the path of the file to save to, as String.
	 * @return boolean: true if successful, false if otherwise.
	 */
	public boolean load(String filePath) throws InterruptedException {
		//object declarations
		FileReader reader;
		BufferedReader buffer;

		//define file object of fileName
		File file = new File(filePath);

		//check file exists
		if (!file.exists()) {
			//print error message
			System.err.println("\033[0;91m" + "Err: File does not exist." + "\033[0m");

			//return false for failure
			return false;
		}

		//check file is readable
		if (!file.canRead()) {
			//print error message
			System.err.println("\033[0;91m" + "Err: File unreadable." + "\033[0m");

			//return false for failure
			return false;
		}

		//begin reading file
		try {
			//declare file reading objects
			reader = new FileReader(file);
			buffer = new BufferedReader(reader);

			//Read Data Here TODO read more values if needed, otherwise, delete this todo
			String line;
			while ((line = buffer.readLine()) != null) {
				if ((line).contains("auth = ")){
					setAuthToken(line.split("\\s",3)[2]);
				}
				else if (line.contains("saveUserInformation = ")) {
					setSaveUserInformation(Boolean.parseBoolean(line.split("\\s",3)[2]));
				}
				else if ((line).contains("user = ")){
					if (saveUserInformation) {

					JSONObject response = (JSONObject) JSONValue.parse(line.split("\\s",3)[2]);
					User temp = Main.parseUser(response);
					setCurrentUser(temp);
					}
					else {
						User temp = Main.getProfile(line.split("\\s", 3)[2]);
						setCurrentUser(temp);
					}

				}
			}
			//close reader and buffer
			buffer.close();
			reader.close();

			//return true for success
			return true;
		}
		catch(IOException e) {
			//print error to handle exception
			System.err.println("\033[0;91m" + "Err: IO error: " + e + "\033[0m");

			//return false for failure
			return false;
		}
	}

	/**
	 * Method to save config to a file.
	 * Declares required objects, then it checks if the file exists and if it is writeable, returning false if these checks fail.
	 * If everything is successful, it closes the object instances and returns true. if an exception is caught it prints an error, then returns false.
	 *
	 * @param filePath the path of the file to save to, as String.
	 * @return boolean: true if successful, false if otherwise.
	 */
	public boolean save(String filePath) {
		//object declarations
		FileOutputStream writer;
		PrintWriter buffer;

		//define file object for filename parameter
		File file = new File(filePath);

		//if file does not exist
		if (!file.exists())  {
			//print error
			System.err.println("\033[0;91m" + "Err: File does not exist." + "\033[0m");

			//return false for failure
			return false;
		}

		//if file is read-only
		if (!file.canWrite()) {
			//print error
			System.err.println("\033[0;91m" + "Err: File read-only." + "\033[0m");

			//return false for failure
			return false;
		}

		//begin writing file
		try {
			//declare file reading objects
			writer = new FileOutputStream(file);
			buffer = new PrintWriter(writer);

			//check if current user is not null
			if (currentUser != null) {
				if (saveUserInformation) { //save all user data as a json if saveUserInformation is true
					//Write file here TODO write additional things if needed otherwise delete this todo
					buffer.println("auth = " + authToken);

					JSONObject user = Main.getUserAsJSON(currentUser);

					if (user != null) {
						buffer.println("user = " + user.toJSONString());
					} else {
						System.err.println("Error, cannot write user to file");
					}
				} else { //just write the username if saveUserInformation is false

					buffer.write("user = " + currentUser.getUsername());
				}
			}

			//close reader and buffer
			buffer.close();
			writer.close();
			//return true for success
			return true;
		}
		catch(IOException e) {
			//print error to handle exception
			System.err.println("\033[0;91m" + "Err: IO error: " + e + "\033[0m");

			//return false for failure
			return false;
		} catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

	public static Config getInstance() {
		return instance;
	}

	//setters and getters
	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User newUser) {
		this.currentUser = newUser;
	}

	public String getServerAddr() {
		return serverAddr;
	}

	public void setServerAddr(String addr) {
		this.serverAddr = addr;
	}

	public boolean profileShown() {
		return showProfile;
	}

	public void setProfileShown(boolean show) {
		showProfile = show;
	}

	public boolean connectionsShown() {
		return showConnections;
	}

	public void setShowConnections(boolean show) {
		showConnections = show;
	}

	public boolean leaderboardShown() {
		return showLeaderboard;
	}

	public void setShowLeaderboard(boolean show) {
		showLeaderboard = show;
	}

	public void setAuthToken(String newAuthToken){
		this.authToken = newAuthToken;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setSaveUserInformation(boolean save){
		this.saveUserInformation = save;
	}
	public boolean getSaveUserInformation(){
		return saveUserInformation;
	}
}

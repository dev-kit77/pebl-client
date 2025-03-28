package app.pebl.profile;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;



/**
 * User class
 */
public class User {
    private String username;
    private int skips;
    private ArrayList<String> followers;
    private ArrayList<String> following;
    private String status;
    private int age;
    private boolean gender;

    /**
     * User constructor
     * Must be called after sending a request to get the user profile.
     * @param username String
     * @param skips int
     * @param followers ArrayList
     * @param following ArrayList
     * @param status String
     * @param age int
     * @param gender boolean
     */
    public User(String username, int skips, ArrayList<String> followers, ArrayList<String> following, String status, int age, boolean gender) {
        this.username = username;
        this.skips = skips;
        this.followers = followers;
        this.following = following;
        this.status = status;
        this.age = age;
        this.gender = gender;
    }

    /**
     *
     * @return String value of username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Stores the parameter value in username
     * @param username String
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return integer stored in skips
     */
    public int getSkips() {
        return skips;
    }

    /**
     * Stores parameter in skips
     * @param skips integer
     */
    public void setSkips(int skips) {
        this.skips = skips;
    }

    /**
     *
     * @return Array list of usernames who are following this user
     */
    public ArrayList<String> getFollowers() {
        return followers;
    }

    /**
     *
     * @return Array list of usernames this user is following
     */
    public ArrayList<String> getFollowing() {
        return following;
    }

    /**
     *
     * @return String status of the user
     */
    public String getStatus() {
        return status;
    }

    /**
     * Stores the parameter in variable status
     * @param status String
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return integer value of user's age
     */
    public int getAge() {
        return age;
    }

    /**
     * Stores the parameter value in variable "age"
     * @param age int
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     *
     * @return boolean gender
     */
    public boolean getGender() {
        return gender;
    }

    /**
     * Stores the parameter value in variable "gender"
     * @param gender boolean
     */
    public void setGender(boolean gender) {
        this.gender = gender;
    }


}


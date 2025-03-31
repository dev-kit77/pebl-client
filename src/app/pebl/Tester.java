package app.pebl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import app.pebl.posts.Post;
import app.pebl.profile.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;



// api link: https://pebl-api.fly.dev/api/

public class Tester {
    /**TODO TEST
     * check server
     * check auth
     * login (in)valid
     * create post (in)valid
     * get post (in)valid
     * get profile (in)valid
     * update profile (in)valid
     * get feed
     * get leaderboard
     * logout and login as someone else
     *
     */
    private static final String username1 = "bob4";
    private static final String username2 = "bob2";
    private static final String password = "bob";
    private static final Connect connect = new Connect();
    private static ArrayList<Post> feed;
    private static ArrayList<User> leaderboard;
    private static User viewedUser;
    private static User currentUser;

    /**
     * test registering (better not be used often) (at the moment it passes)
     * @throws IOException
     * @throws InterruptedException
     */
    private static void register() throws IOException, InterruptedException {
        System.out.println("testing REGISTER");
        //make json object to pass to method and populate it
        JSONObject obj = new JSONObject();
        obj.put("username", username1);
        obj.put("password", password);
        obj.put("age", 12);
        obj.put("gender", true);

        //run the method and display the outcome
        System.out.println(connect.request("register", obj)+"\n");
    }

    /**
     * test logging in (at the end calls getProfile to set the current user)
     * @throws IOException
     * @throws InterruptedException
     */
    private static void login() throws IOException, InterruptedException {
        System.out.println("testing LOGIN");
        //make json object to pass to method and populate it

        JSONObject obj = new JSONObject();
        obj.put("username", username1);
        obj.put("password", password);

        //run the method and display the outcome
         System.out.println(connect.request("auth", obj)+"\n");

         currentUser = Main.parseUser(connect.request("profileGet", obj));

    }

    /**
     * Check if server is online
     * @throws IOException
     * @throws InterruptedException
     */
    private static void checkServer() throws IOException, InterruptedException {
        System.out.println("testing CHECK SERVER");
        JSONObject obj = new JSONObject();
        System.out.print(connect.request("checkOnline", obj)+"\n");
    }

    /**
     * check if auth token is still valid
     * @throws IOException
     * @throws InterruptedException
     */
    private static void checkAuth() throws IOException, InterruptedException {
        System.out.println("testing CHECK AUTH");
        JSONObject obj = new JSONObject();

        System.out.println(connect.request("checkAuth", obj)+"\n");
    }

    /**
     * test getting profile
     * @param username
     * @throws IOException
     * @throws InterruptedException
     */
    private static void getProfile(String username) throws IOException, InterruptedException {
        System.out.println("testing GET PROFILE");
        JSONObject obj = new JSONObject();
        obj.put("username", username);
        System.out.println(connect.request("profileGet", obj)+"\n");
    }


    /**
     * test updating the profile
     * @param age int
     * @param gender boolean
     * @param status String
     * @throws IOException
     * @throws InterruptedException
     */
    private static void updateProfile(int age, boolean gender, String status) throws IOException, InterruptedException {
        System.out.println("testing UPDATE PROFILE");

        //creating json object and populating it
        JSONObject obj = new JSONObject();
        obj.put("age", age);
        obj.put("gender", gender+"");
        obj.put("status", status);
        System.out.println(connect.request("profileUpdate", obj)+"\n");
    }

    /**
     * test getting post by id
     * @param id int
     * @throws IOException
     * @throws InterruptedException
     */
    private static void getPost(int id) throws IOException, InterruptedException {
        System.out.println("testing GET POST "+id);
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        System.out.println(connect.request("postGet", obj)+"\n");
    }


    /**
     * test creating a post and displaying it right away
     * @param content String
     * @throws IOException
     * @throws InterruptedException
     */
    private static void createPost(String content) throws IOException, InterruptedException {
        System.out.println("testing CREATE POST");

        //Create body for request and populate
        JSONObject obj = new JSONObject();
        obj.put("content", content);

        //store response in a JSONObject to use it for id
        JSONObject response = connect.request("postCreate", obj);

        //parse integer id and store in variable id
        int id = Integer.parseInt(response.get("id").toString());

        //print the response
        System.out.println(response+"\n");

        //get the post by the id to verify it has been created
        getPost(id);

    }

    /**
     * testing getting the feed
     */
    private static void feed() throws IOException, InterruptedException {
        System.out.println("testing FEED");
        //Create empty json object to pass to request
        JSONObject obj = new JSONObject();
        System.out.println(connect.request("feed", obj)+"\n");
    }


    /**
     * Testing following functionality
     * Makes the current logged-in user follow the entered user, then call getProfile on the current user and then the entered user
     * to check if their respective following and followers lists have been changed
     * @param username String
     * @throws IOException
     * @throws InterruptedException
     */
    private static void toggleFollow(String username) throws IOException, InterruptedException {
        System.out.println("testing FOLLOW between "+username1+" and "+username);

        //creating json object and populating it
        JSONObject obj = new JSONObject();
        obj.put("target", username);

        //storing current username in obj to reuse it for getProfile
        obj.put("username", username1);

        //store response in a variable
        JSONObject response = connect.request("follow", obj);

        System.out.println(response+"\n");

        //getProfile of current user. If the target user is stored in the "following" list,
        if (((JSONArray)connect.request("profileGet", obj).get("following")).contains(username)) {
            System.out.println("Now following "+username+"\n");
        }
        else {
            System.out.println("Now unfollowed "+username+"\n");
        }


        //Call get profile on current user and entered user to check if their respective followers and following lists have changed
        getProfile(username1);
        getProfile(username);
    }


    /**
     * test liking a post, when a user skips a post, they spend one of their own skips. To earn skips, users must create new posts, or earn the skips of others by getting their posts liked (skipped) by others
     * @param id int
     * @throws IOException
     * @throws InterruptedException
     */
    private static void like(int id) throws IOException, InterruptedException {
        System.out.println("testing LIKE id: "+id);

        //TODO tell fyr that creating posts does not increase skips
        //creating json object and populating it
        JSONObject obj = new JSONObject();
        obj.put("id", id);

        //Create a post so the current user has a skip to spare
        createPost("dame dane");

        //call get Profile and getPost(id) to compare their results before and after skipping.
        getProfile(username1);
        getPost(id);

        //print response which is the remaining skips of user
        System.out.println(connect.request("like", obj)+"\n");

        //call get Profile and getPost(id) a second time to compare with the first time they were called in this function
        getProfile(username1);
        getPost(id);

    }

    /**
     * Test fetching leaderboard
     * @throws IOException
     * @throws InterruptedException
     */
    private static void leaderboard() throws IOException, InterruptedException {
        System.out.println("testing LEADERBOARD");

        System.out.println(connect.leaderboard()+"\n");

    }

    //main
    public static void main(String[] args) throws Exception {
        //testing if server is online success
//        checkServer();


        //registering new user test success
//        register();

        //testing login success
//        login();

        //testing if auth token is still valid success
//        checkAuth();

        //testing get profile success
//        getProfile(username1);
//        getProfile(username2);

        //testing update profile success
//        updateProfile(13, true, "sleepy");
//        getProfile(username1);

        //testing getPost success
//        getPost(2);

        //testing createPost success
//        createPost("Hello World");

        //testing feed success
//        feed();

        //testing following (bob4 will follow bob2) success
//        toggleFollow(username2);

        //testing like (bob4 will like post id: 2) TODO tell charles creating posts does not increase skips, the like test failed (user had 0 skips even after creating post)
//        like(2);

        //testing leaderboard success
//        leaderboard();
    }

}
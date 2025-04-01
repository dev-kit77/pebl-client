package app.pebl.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import app.pebl.Main;
import app.pebl.data.Post;
import app.pebl.data.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


// api link: https://pebl-api.fly.dev/api/

public class Tester {
    /**
     * check server done
     * check auth done
     * register (in)valid done
     * login (in)valid done
     * create post (in)valid done
     * get post (in)valid done
     * get profile (in)valid done
     * update profile (in)valid done
     * get feed done
     * get leaderboard done
     * logout and login as someone else done
     * follow (in)valid done
     * like (in)valid done
     * close friends done
     * filter feed done
     *
     */
    private static final String username1 = "bob1";
    private static final String username2 = "bob2";
    private static final String password = "bob";
    private static final Connect connect = new Connect();
    private static ArrayList<Post> feed = null;
    private static ArrayList<Post> filteredFeed = null; //stores filtered feed
    private static ArrayList<User> leaderboard;
    private static User viewedUser;
    private static User currentUser;

    /**
     * test registering (better not be used often) (at the moment it passes)
     * @throws IOException
     * @throws InterruptedException
     */
    private static void register(String username, String password, int age, boolean gender) throws IOException, InterruptedException {
        System.out.println("testing REGISTER");
        //make json object to pass to method and populate it
        JSONObject obj = new JSONObject();
        obj.put("username", username);
        obj.put("password", password);
        obj.put("age", age);
        obj.put("gender", ""+gender);


        //run the method and display the outcome
        System.out.println(connect.request("register", obj)+"\n");

        currentUser = Main.parseUser(connect.request("profileGet", obj));
    }

    /**
     * test logging in (at the end calls getProfile to set the current user)
     * @throws IOException
     * @throws InterruptedException
     */
    private static void login(String username, String password) throws IOException, InterruptedException {
        System.out.println("testing LOGIN");
        //make json object to pass to method and populate it

        JSONObject obj = new JSONObject();
        obj.put("username", username);
        obj.put("password", password);

        //run the method and display the outcome
         System.out.println(connect.request("auth", obj)+"\n");

        //add "target" to obj in order to fetch user data
        obj.put("target", username.toLowerCase());

        // fetching user data and updating the current user;
        currentUser = Main.parseUser(connect.request("profileGet", obj));

    }






    /**
     * Test the functionality of getting close friends (You follow each other)
     * @throws IOException
     * @throws InterruptedException
     */

    private static void closeFriends() throws IOException, InterruptedException {
        System.out.println("testing CLOSE FRIENDS");


        //store the followings list and store it for usage
        ArrayList<String> following = new ArrayList<String>(currentUser.getFollowing());
        //get iterator
        Iterator<String> iter = following.iterator();


        //Store close friends here
        ArrayList<String> friends = new ArrayList<String>();


        // for each follow in following list
        while (iter.hasNext()) {
            //call iter.next() and store in a string for later use
            String username = iter.next();


            //create json body for request and populate
            JSONObject obj = new JSONObject();
            obj.put("username", username);

            //send request and store response
            JSONObject response = connect.request("profileGet", obj);

            //get following from response and check if it has current users username, if yes, add username to close friends
            if (((JSONArray)response.get("following")).contains(currentUser.getUsername())) {
                friends.add(username);
            }

        }

        System.out.println("Close friends: "+friends.toString()+"\n");
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

        //create body json object and populate it to pass into request
        JSONObject obj = new JSONObject();
        obj.put("target", username);

        //display the body (comment when not needed)
        System.out.println("Request body: "+obj);

        //send request and store response in variable for use
        JSONObject response = connect.request("profileGet", obj);
        //display response
        System.out.println(response+"\n");

        //check if the username were viewing is the current client user
        if (response.get("username").equals(currentUser.getUsername())) {
            //update the current user
            currentUser = Main.parseUser(response);
        }

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

        //send request and save response
        JSONObject response = connect.request("profileUpdate", obj);

        //update current user
        currentUser = Main.parseUser(response);

        System.out.println(response+"\n");
    }

    /**
     * test getting post by id
     * @param id int
     * @throws IOException
     * @throws InterruptedException
     */
    private static void getPost(int id) throws IOException, InterruptedException {
        System.out.println("testing GET POST "+id);
        //create json body to pass into request and populate it
        JSONObject obj = new JSONObject();
        obj.put("id", id);

        //send request and display response
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
     * testing getting the feed, also updates the feed variable
     */
    private static void feed() throws IOException, InterruptedException {
        System.out.println("testing FEED");

        //Create empty json object to pass to request
        JSONObject obj = new JSONObject();

        //send request and store response for later use
        JSONObject response = connect.request("feed", obj);

        //check if response is null
        if (response != null) {

            //create a temporary arraylist to add JSONArray elements to
            ArrayList<Post> posts = new ArrayList<Post>();

            //parse JSONArray feed in the response object
            JSONArray postsArray = (JSONArray) response.get("feed");

            //loop over the elements in possArray
            for (int i = 0; i < postsArray.size(); i++) {
                JSONObject post = (JSONObject) postsArray.get(i); //convert jsonarray element object to json object
                //create post object and add it to posts array list
                posts.add(Main.parsePost(post));
            }

            //update feed with the posts arraylist
            feed = posts;


        }

        System.out.println(response+"\n");
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
        System.out.println("testing FOLLOW between "+currentUser.getUsername()+" and "+username);

        //creating json object and populating it
        JSONObject obj = new JSONObject();
        obj.put("target", username);


        //store response in a variable
        JSONObject response = connect.request("follow", obj);

        System.out.println(response+"\n");

        //getProfile of current user. If the target user is stored in the "following" list,
        if (!currentUser.getFollowing().contains(username)) {
            System.out.println("Now following "+username+"\n");
        }
        else {
            System.out.println("Now unfollowed "+username+"\n");
        }


        //Call get profile on current user and entered user to check if their respective followers and following lists have changed
        getProfile(currentUser.getUsername());
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


        //creating json object and populating it
        JSONObject obj = new JSONObject();
        obj.put("id", id);

//        //Create a post so the current user has a skip to spare. comment out if not needed
//        createPost("dame dane");

        //call get Profile and getPost(id) to compare their results before and after skipping.
        getProfile(currentUser.getUsername());
        getPost(id);

        //print response which is the remaining skips of user
        System.out.println(connect.request("like", obj)+"\n");

        //call get Profile and getPost(id) a second time to compare with the first time they were called in this function
        getProfile(currentUser.getUsername());
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


    /**
     * Test filtering the feed (stores filtered feed in filteredFeed)
     * @param query True for "FOLLOWERS ONLY". False for "FOLLOWINGS ONLY"
     * @throws IOException
     * @throws InterruptedException
     */
    private static void filterFeed(boolean query) throws IOException, InterruptedException {
        System.out.println("testing FILTER FEED");

        //check if feed is empty before running
        if (feed == null){
            System.err.println("feed is empty");
            return;
        }

        //copy feed to filter feed
        filteredFeed = new ArrayList<Post>(feed);

        //if FOLLOWERS ONLY
        if (query){

            //remove elements whose authors are not store in current user's follower's list
            filteredFeed.removeIf(element -> !currentUser.getFollowers().contains(element.getSender()));
        }

        //if FOLLOWINGS ONLY
        else{

            //remove elements whose authors are not store in current user's following's list
            filteredFeed.removeIf(element -> !currentUser.getFollowing().contains(element.getSender()));
        }

        //display the filtered feed
        for (Post post : filteredFeed) {
            System.out.println("id :"+post.getId()+", author: "+post.getSender()+", content: "+post.getContent()+", date: "+ post.getDate().toString()+", skips: "+post.getSkips());
        }

    }


    //main
    public static void main(String[] args) throws Exception {
        //testing if server is online success
//        checkServer();


        //registering new user test success
//        register("bob1", "bob", 30, false);

        //testing login success
//        login("bob2", "bob");

        //testing if auth token is still valid success
//        checkAuth();

        //testing get profile success
//        getProfile(username1);
//        getProfile(username2);
//        getProfile("fýr");

        //testing update profile success
//        updateProfile(15, true, "klondike");
//        getProfile(username1);

        //testing getPost success
//        getPost(2);

        //testing createPost success
//        createPost("Hello World");

        //testing feed success
//        feed();

        //testing following (bob4 will follow bob2) success
//        toggleFollow("bob1");
//        toggleFollow("bob3");

        //testing like (bob2 will like post id: 3) success
//        like(3);

        //testing leaderboard success
//        leaderboard();

        //test get close friends success
//        closeFriends();

        //testing filter feed success
//        filterFeed(true);

        //test cli
//        Main.cli();

        // testing following each other and getting close friends success
//        getProfile("bob2");

//        register("bob1", "bob", 30, false);
//      login("bob1", "bob");
//        toggleFollow("bob2");

//        login("bob2", "bob");
//        toggleFollow("bob1");

//        login("bob1", "bob");
//        closeFriends();

    }

}
package app.pebl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import app.pebl.posts.Post;
import app.pebl.profile.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


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
    private static final String username1 = "bob2";
    private static final String username2 = "bob3";
    private static final String password = "bob";

    private static final Connect connect = new Connect();
    private static ArrayList<Post> feed;
    private static ArrayList<User> leaderboard;
    private static User viewedUser;

    public static void main(String[] args) throws Exception {
        Main.cli();
    }
}
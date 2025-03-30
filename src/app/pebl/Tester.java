package app.pebl;

import java.util.Properties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.net.URI;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
    private static String auth = "";
    private static final HttpClient client = HttpClient.newHttpClient();
    private
    public Tester() {

    }
}
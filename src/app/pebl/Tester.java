package app.pebl;

import java.util.Properties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.net.URI;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

// api link: https://pebble-api.fly.dev/api/

public class Tester {


    public static String api = "https://pebble-api.fly.dev/api/";
    private String auth; //auth token
    private File config; //config file
    public Tester() throws IOException{
        config = new File(new File("./.pebl.cfg").getCanonicalPath()); //file config to config file
        if (!config.exists()) {
            config.createNewFile();
        }
        // TODO read auth token from config

    }
    
    public void testregister(){ //TODO test creating a new user
        
    }
    
    public void testlogin(){ //TODO test logging in existing user
        
    }


    public boolean testSave() throws IOException{ //TODO save auth to config file (make it work alongside saving other configs)

        return false;
    }

    public boolean testChangeAuth() throws IOException{ //TODO change the auth value in the config file to a different one
        return false;
    }
    
    public static void main(String[] args) throws IOException {
        // System.out.println(api);

    }
}
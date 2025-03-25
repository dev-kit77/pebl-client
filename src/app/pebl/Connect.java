package app.pebl;

//importing packages
import java.util.Properties;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
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

//api link: // api link: https://pebl-api.fly.dev/api/


public class Connect {
    private String auth;
    private String path;
    public Connect() throws IOException{
        File file = new File(".");
        path = file.getCanonicalPath();
        
    }

    //TODO add different functions (or reuse this one) to get data on users and another for getting posts.
    /**
     * Method to test connection to server
     */


    
}

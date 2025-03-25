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
    HttpClient client = HttpClient.newHttpClient(); //The client

    public static String api = "https://pebl-api.fly.dev/api/"; //The api path for quick use
    private String auth; //auth token
    private File config; //config file

    /**
     * Tester constructor
     * @throws IOException
     */
    public Tester() throws IOException{
        config = new File(new File("./.pebl.cfg").getCanonicalPath()); 
        //Create the config file if it doesnt exist
        if (!config.exists()) {
            config.createNewFile();
        }
        FileInputStream file = null;
        try {
            file = new FileInputStream(config.getAbsolutePath());
            Properties prop = new Properties();
            prop.load(file);
            auth = prop.getProperty("auth", "");
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("IOException");
        } 
         finally {
            if (file != null) {
                try {
                    file.close();
                } 
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public String getAuth(){
        return auth;
    }

    public void request(String type, JSONObject body) throws IOException, InterruptedException{
        HttpRequest request;
        HttpResponse<String> response;
        JSONObject authFull;
        switch (type) {

            case "register":
                request = HttpRequest.newBuilder()
                .uri(URI.create(api+type))
                .POST(HttpRequest.BodyPublishers.ofString(body.toJSONString()))
                .header("Content-Type", "application/json")
                .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.statusCode());
                System.out.println(response.body());
                authFull = (JSONObject)JSONValue.parse(response.body()); //parse the response as JSON
                auth = (String)authFull.get("token"); // update the auth token
                authFull = null; //remove the JSON object

                break;

            case "auth":
                request = HttpRequest.newBuilder()
                .uri(URI.create(api+type))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("Content-Type", "application/json")
                .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.statusCode());
                System.out.println(response.body());
                authFull = (JSONObject)JSONValue.parse(response.body()); //parse the response as JSON
                auth = (String)authFull.get("token"); // update the auth token
                authFull = null; //remove the JSON object
                break;

            default:
                System.out.println("No such option for request.");
                break;
        }
    }

    /**
     * Creates a user with name Roberto, it sends a request to api/register with the body being stringified json object 
     * New account details:
     * password: bob
     * username: Roberto
     * age: 50
     * gender: true
     * @throws InterruptedException 
     * 
     */
    @SuppressWarnings("unchecked")
    public void testRegister() throws IOException, InterruptedException{ //TODO test creating a new user
        JSONObject user = new JSONObject();
        
        user.put("password", "bob");
        user.put("username", "Roberto");
        user.put("age", 50);
        user.put("gender", true);
        request("register", user);
        
    }
    
    /**
     * Tries to login as Roberto
          * @throws InterruptedException 
          * @throws IOException 
          */
         @SuppressWarnings("unchecked")
         public void testAuth() throws IOException, InterruptedException{ //TODO test logging in existing user
        JSONObject user = new JSONObject();
        user.put("username", "roberto");
        user.put("password", "bob");
        request("auth", user);
    }


    public boolean testSave() throws IOException{ //TODO save auth to config file

        return false;
    }

    public boolean testChangeAuth() throws IOException{ //TODO change the auth value in the config file to a different one
        return false;
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        // System.out.println(aypi);
        Tester tester = new Tester();
        // tester.testRegister();
        tester.testAuth();

    }
}
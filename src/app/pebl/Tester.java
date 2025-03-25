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
    public boolean testRegister() throws IOException, InterruptedException{ //TODO test creating a new user
        JSONObject user = new JSONObject();
        
        user.put("password", "bob");
        user.put("username", "Roberto");
        user.put("age", 50);
        user.put("gender", true);

        HttpRequest register = HttpRequest.newBuilder()
        .uri(URI.create(api+"register"))
        .POST(HttpRequest.BodyPublishers.ofString(user.toJSONString()))
        .header("Content-Type", "application/json")
        .build();

        HttpResponse<String> response = client.send(register, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            auth = response.body();
            System.out.println(response.statusCode());
            System.out.println(auth);
            return true;
        }
        else {
            System.out.println("code: "+ response.statusCode());
            System.out.println(user.toJSONString());
            return false;
        }
        
    }
    
    /**
     * Tries to login as Roberto
     */
    public void testLogin(){ //TODO test logging in existing user
        
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
        tester.testRegister();

    }
}
package app.pebl;

//importing packages
import java.util.Properties;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
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
import java.net.http.HttpResponse.BodyHandlers;

//api link: // api link: https://pebl-api.fly.dev/api/


public class Connect {
    private HttpClient client;
    private File config;
    private String auth;
    public Connect() throws IOException{
        client = HttpClient.newHttpClient(); //The client
        auth("");
    }

    /**
     * Updates the value of auth in the .pebl.cfg file
     * @param newAuth new authentication token to update the config file. Set this to empty string to get the value from the config file or to set the auth token to empty string.
     * 
     */
    private void auth(String newAuth){

        config = new File(new File("./.pebl.cfg").getCanonicalPath()); //teh config file
        //Create the config file if it doesnt exist
        if (!config.exists()) {
            config.createNewFile();
        }
        FileInputStream file = null;
        try {
            file = new FileInputStream(config.getAbsolutePath());
            Properties prop = new Properties();
            prop.load(file);
            if (!newAuth.equals("")) {
                prop.setProperty("auth", newAuth);
            }
            else {

                auth = prop.getProperty("auth", "");
            }
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
        if (auth.equals("")) {
            request("register", new JSONObject());
        }
    }

    /**
     * Method to handle all http requests, just enter from the options []
     * @param type where you are sending the request to
     * @param body the body
     * @throws IOException
     * @throws InterruptedException
     */
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


    
}

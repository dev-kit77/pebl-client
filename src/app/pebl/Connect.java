package app.pebl;

//importing packages
import java.util.Properties;
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


//api link: // api link: https://pebl-api.fly.dev/api/


public class Connect {
    private final HttpClient client;
    private String auth;
    public static String api = "https://pebl-api.fly.dev/api/"; //The api path for quick use
    public Connect() throws IOException {
        client = HttpClient.newHttpClient(); //The client
        auth("");
    }

    /**
     * Updates the value of auth in the .pebl.cfg file
     * @param newAuth new authentication token to update the config file. Set this to empty string to get the value from the config file or to set the auth token to empty string.
     * 
     */
    private void auth(String newAuth) throws IOException {

        File config = new File(new File("./.pebl.cfg").getCanonicalPath()); //teh config file
        //Create the config file if it doesn't exist
        if (!config.exists()) {
            config.createNewFile();
        }
        FileInputStream file = null;
        FileOutputStream fos = null;
        try {
            file = new FileInputStream(config.getAbsolutePath());
            fos = new FileOutputStream(config.getAbsoluteFile());
            Properties prop = new Properties();
            prop.load(file);
            if (!newAuth.isEmpty()) {
                prop.setProperty("auth", newAuth);
                prop.store(fos, null);

            }
            else {

                auth = prop.getProperty("auth", "");
            }
        } catch (FileNotFoundException e) {
            System.out.println("config file not found");
        } catch (IOException e) {
            System.out.println("IOException with config file");
        }
         finally {
            if (file != null) {
                try {
                    file.close();
                    assert fos != null;
                    fos.close();
                } 
                catch (Exception e) {
                    System.err.println("Error closing config file input stream or output stream");
                }
            }
        }

    }

    /**
     * Method to handle all http requests, just enter from the options [chechOnline, checkAuth, register, auth, profileGet, profileUpdate, postGet, postCreate, feed, leaderboard]
     * @param type where you are sending the request to
     * @param body the body
     */
    public JSONObject request(String type, JSONObject body) throws IOException, InterruptedException{
        HttpRequest request;
        HttpResponse<String> response;
        JSONObject responseJSON;
        switch (type) {
            case "checkOnline": //check if server is online
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api))
                        .GET()
                        .build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("sending to: "+response.uri().toString());
                if (response.statusCode() == 200) {
                    System.out.println("Server is online");
                    System.out.println("status code: "+response.statusCode());

                }
                else {
                    System.out.println("Server is not online");
                    System.out.println("status code: "+response.statusCode());
                }
                break;

            case "checkAuth": //check if auth token is valid
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api))
                        .POST(HttpRequest.BodyPublishers.ofString(body.toJSONString())) // json -> {"error":"none", "success":"true", "token": {the auth token}}
                        .build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("sending to: "+response.uri().toString());
                if (response.statusCode() == 200) {
                    System.out.println("Token is valid");
                    System.out.println("Status code"+ response.statusCode());
                }
                else {
                    System.out.println("Token is invalid or there is a server problem");
                    System.out.println("Status code: "+response.statusCode());
                }
                break;

            case "register": //create new account and get auth token Warning: the username must be all lowercase
                request = HttpRequest.newBuilder()
                .uri(URI.create(api+"register"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toJSONString()))
                .header("Content-Type", "application/json")
                .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("sending to: "+response.uri().toString());

                if (response.statusCode() == 200) {
                    System.out.println("register ok");
                    System.out.println("Status code: "+response.statusCode());
                    responseJSON = (JSONObject)JSONValue.parse(response.body()); //parse the response as JSON
                    auth = (String)responseJSON.get("token"); // update the auth token
                    auth(auth);
                    System.out.println("Status code: "+response.statusCode());
                }
                else {
                    System.out.println("register failed");
                    auth("");
                    System.out.println("Status code: "+response.statusCode());
                }

                break;

            case "auth": //send username and password to get new token
                request = HttpRequest.newBuilder()
                .uri(URI.create(api+"auth"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("Content-Type", "application/json")
                .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Sending to: "+request.uri().toString()+"\nresponse: "+response.body());

                if (response.statusCode() == 200) {
                    System.out.println("auth ok");
                    responseJSON = (JSONObject)JSONValue.parse(response.body()); //parse the response as JSON
                    auth = (String)responseJSON.get("token"); // update the auth token
                    auth(auth);
                    System.out.println("Status code: "+response.statusCode());
                }
                else {
                    System.out.println("Invalid username or password or server problem");
                    System.out.println("Status code: "+response.statusCode());
                }

                break;

            case "profileGet": // get user profile
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"user/profile"))
                        .GET()
                        .header("Content-Type", "") //TODO content type
                        .header("target", body.get("username").toString())
                        .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Sending to: "+request.uri().toString()+"\nresponse: "+response.body());

                if (response.statusCode() == 200) {
                    System.out.println("profile obtained");
                    System.out.println("status code: "+response.statusCode());

                    responseJSON = (JSONObject)JSONValue.parse(response.body());

                    return responseJSON;
                }
                else {
                    System.out.println("error getting profile");
                    System.out.println("status code: "+response.statusCode());

                }
                break;

            case "profileUpdate": //update user profile json must include {age, gender, status}
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"user/profile"))
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Basic " + auth)
                        .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Sending to: "+request.uri().toString()+"\nresponse: "+response.body());

                if (response.statusCode() == 200) {
                    System.out.println("profile updated");
                    System.out.println("status code: "+response.statusCode());

                }
                else {
                    System.out.println("error updating profile");
                    System.out.println("status code: "+response.statusCode());

                }
                break;


            case "postGet": // get specific post by id
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"post/get"))
                        .GET()
                        .header("Content-Type", "") //TODO content type
                        .header("id", body.get("id").toString())
                        .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Sending to: "+request.uri().toString()+"\nresponse: "+response.body());

                if (response.statusCode() == 200) {
                    System.out.println("post obtained");
                    System.out.println("status code: "+response.statusCode());

                    responseJSON = (JSONObject)JSONValue.parse(response.body());

                    return responseJSON;
                }
                else {
                    System.out.println("error getting post"+"\nstatus code: "+response.statusCode());
                }
                break;

            case "postCreate": // make post
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"post/create"))
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Basic " + auth)
                        .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Sending to: "+request.uri().toString());

                if (response.statusCode() == 200) {
                    System.out.println("post created and posted");
                    System.out.println("status code: "+response.statusCode());
                    System.out.println("body: "+response.body());
                }
                else {
                    System.out.println("error creating post"+"\nstatus code: "+response.statusCode());
                }
                break;

            case "feed": // get feed
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"feed"))
                        .GET()
                        .header("Content-Type", "application/json")
                        .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("Sending to: "+request.uri().toString()+"\nresponse: "+response.body());
                if (response.statusCode() == 200) {
                    System.out.println("feed obtained");
                    System.out.println("status code: "+response.statusCode());

                    responseJSON = (JSONObject)JSONValue.parse(response.body());
                    return responseJSON;
                }
                else {
                    System.out.println("error getting feed");
                    System.out.println("status code: "+response.statusCode());

                }
                break;

            case "leaderboard": // get leaderboard  information
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"leaderboard"))
                        .GET()
                        .header("Content-Type", "application/json")
                        .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("Sending to: "+request.uri().toString()+"\nresponse: "+response.body());
                if (response.statusCode() == 200) {
                    System.out.println("leaderboard obtained");
                    System.out.println("status code: "+response.statusCode());

                    responseJSON = (JSONObject)JSONValue.parse(response.body());
                    return responseJSON;
                }
                else {
                    System.out.println("error getting leaderboard");
                    System.out.println("status code: "+response.statusCode());

                }
                break;

            default:
                System.out.println("No such option for request.");
                break;
        }
        return null;
    }


    
}

package app.pebl;

//importing packages
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


//api link: // api link: https://pebl-api.fly.dev/api/


@SuppressWarnings("JavadocDeclaration")
public class Connect {
    private final HttpClient client;
    private String auth = Config.getInstance().getAuthToken();
    public static String api = Config.getInstance().getServerAddr()+"api/"; //The api path for quick use
    public Connect() {
        client = HttpClient.newHttpClient(); //The client

    }

    /**
     * Constructor that lets you change the auth token variable. Use only for testing purposes in the Tester class
     * @param auth
     */
    public Connect(String auth) {
        this.auth = auth;
        client = HttpClient.newHttpClient();
        api = "https://pebl-api.fly.dev/api/";
    }

    /**
     * Updates the value of auth in the .pebl.cfg file
     * @param newAuth new authentication token to update the config file. Set this to empty string to get the value from the config file or to set the auth token to empty string.
     * 
     */
    private void authUpdate(String newAuth) {
        Config.getInstance().setAuthToken(newAuth);

    }

    /**
     * Method to handle all http requests, just enter from the options [checkOnline, checkAuth, register, auth, profileGet, profileUpdate, postGet, postCreate, feed, leaderboard]
     * @param type where you are sending the request to
     * @param body the body
     */
    @SuppressWarnings("unused")
    public JSONObject request(String type, JSONObject body) throws IOException, InterruptedException{
        HttpRequest request;
        HttpResponse<String> response;
        JSONObject responseJSON;
        switch (type) {
            case "checkOnline": //check if server is online
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api))
                        .GET()
                        .header("Content-Type", "application/json")
                        .build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("sending to: "+response.uri().toString());
                if (response.statusCode() == 200) {
                    System.out.println("Server is online "+response.body());
                    System.out.println("status code: "+response.statusCode());
                    responseJSON = new JSONObject();
                    return responseJSON;
                }
                else {
                    System.out.println("Server is not online");
                    System.out.println("status code: "+response.statusCode());
                }
                break;

            case "checkAuth": //check if auth token is valid
                body.put("token", auth);
                body.put("error", "none");
                body.put("success", "true");
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api))
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toJSONString())) // json -> {"error":"none", "success":"true", "token": {the auth token}}
                        .header("Content-Type", "application/json")
                        .header("Authorization", auth)
                        .build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("sending to: "+response.uri().toString());
                if (response.statusCode() == 200) {
                    System.out.println("Token is valid "+response.body());
                    System.out.println("Status code"+ response.statusCode());
                    responseJSON = new JSONObject();
                    return responseJSON;
                }
                else {
                    System.out.println("Token is invalid or there is a server problem");
                    System.out.println("Status code: "+response.statusCode());
                }
                break;

            case "register": //create new account and get auth token Warning: the username must be all lowercase (), must include{username, password, age, gender}
                request = HttpRequest.newBuilder()
                .uri(URI.create(api+"register"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toJSONString()))
                .header("Content-Type", "application/json")
                .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("sending to: "+response.uri().toString());

                if (response.statusCode() == 200) {
                    System.out.println("register ok" + response.body().toString());
                    System.out.println("Status code: "+response.statusCode());
                    responseJSON = (JSONObject)JSONValue.parse(response.body()); //parse the response as JSON
                    auth = (String)responseJSON.get("token"); // update the auth token
                    authUpdate(auth);
                    System.out.println("Status code: "+response.statusCode());
                    return responseJSON;
                }
                else {
                    System.out.println("register failed, Username unavailable");
                    authUpdate("");
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
                    System.out.println("auth token received " + response.body().toString());
                    responseJSON = (JSONObject)JSONValue.parse(response.body()); //parse the response as JSON
                    auth = (String)responseJSON.get("token"); // update the auth token
                    authUpdate(auth);
                    System.out.println("Status code: "+response.statusCode());
                    return responseJSON;
                }
                else {
                    System.out.println("Invalid username or password or server problem");
                    System.out.println("Status code: "+response.statusCode());
                }

                break;

            case "profileGet": // get user profile json body must have {username: String username of user}
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"user/profile"))
                        .GET()
                        .header("Content-Type", "application/json")
                        .header("target", body.get("username").toString())
                        .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Sending to: "+request.uri().toString()+"\nresponse: "+response.body());

                if (response.statusCode() == 200) {
                    System.out.println("profile obtained "+ response.body().toString());
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
                        .header("Authorization", auth)
                        .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Sending to: "+request.uri().toString()+"\nresponse: "+response.body());

                if (response.statusCode() == 200) {
                    System.out.println("profile updated " + response.body().toString());
                    System.out.println("status code: "+response.statusCode());
                    responseJSON = new JSONObject();

                    return responseJSON;
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
                        .header("Content-Type", "application/json")
                        .header("id", body.get("id").toString())
                        .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Sending to: "+request.uri().toString()+"\nresponse: "+response.body());

                if (response.statusCode() == 200) {
                    System.out.println("post obtained "+ response.body().toString());
                    System.out.println("status code: "+response.statusCode());

                    responseJSON = (JSONObject)JSONValue.parse(response.body());

                    return responseJSON;
                }
                else {
                    System.out.println("error getting post"+"\nstatus code: "+response.statusCode());
                }
                break;

            case "postCreate": // make post must include {}
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"post/create"))
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .header("Content-Type", "application/json")
                        .header("Authorization", auth)
                        .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Sending to: "+request.uri().toString());

                if (response.statusCode() == 200) {
                    System.out.println("post created and posted "+ response.body().toString());
                    System.out.println("status code: "+response.statusCode());
                    System.out.println("body: "+response.body());
                    responseJSON = new JSONObject();

                    return responseJSON;
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
                    System.out.println("feed obtained " + response.body().toString());
                    System.out.println("status code: "+response.statusCode());

                    responseJSON = (JSONObject)JSONValue.parse(response.body());
                    return responseJSON;
                }
                else {
                    System.out.println("error getting feed");
                    System.out.println("status code: "+response.statusCode());

                }
                break;

            case "follow": //following a user
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"user/follow"))
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .header("Content-Type", "application/json")
                        .header("Authorization", auth)
                        .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Sending to: "+request.uri().toString());
                if (response.statusCode() == 200) {
                    System.out.println("follow successful " + response.body().toString());
                    responseJSON = new JSONObject();
                    return responseJSON;
                }
                else if (response.statusCode() == 404) {
                    System.out.println("user not found");

                }
                else if (response.statusCode() == 409) {
                    System.out.println("Already following user");

                }
                break;

            case "like": //liking post
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"post/skip"))
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .header("Content-Type", "application/json")
                        .header("Authorization", auth)
                        .build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString()); //json body: {"id": id} id is integer id of post
                System.out.println("Sending to: "+request.uri().toString());
                if (response.statusCode() == 200) {
                    System.out.println("like successful " + response.body().toString());
                    responseJSON = (JSONObject)JSONValue.parse(response.body()); //TODO replace with empty object incase nothing gets returned
                    return responseJSON;
                }
                else if (response.statusCode() == 404) {
                    System.out.println("Post ID not found");
                }
                else if (response.statusCode() == 402) {
                    System.out.println("User has no more skips");
                }
                else if (response.statusCode() == 401) {
                    System.out.println("User cannot like their own post");
                }
                break;

            default:
                System.out.println("No such option for request.");
                break;
        }
        return null;
    }

    /**
     * Method to request the leaderboard of users
     * @return JSONArray of users
     * @throws IOException
     * @throws InterruptedException
     */
    public JSONArray leaderboard() throws IOException, InterruptedException {
        HttpRequest request;
        HttpResponse<String> response;
        JSONArray responseJSON = new JSONArray();
        request = HttpRequest.newBuilder()
                .uri(URI.create(api+"leaderboard"))
                .GET()
                .header("Content-Type", "application/json")
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Sending to: "+request.uri().toString()+"\nresponse: "+response.body());
        if (response.statusCode() == 200) {
            System.out.println("leaderboard obtained "+ response.body().toString());
            System.out.println("status code: "+response.statusCode());

            responseJSON = (JSONArray)JSONValue.parse(response.body());
            return responseJSON;
        }
        else {
            System.out.println("error getting leaderboard");
            System.out.println("status code: "+response.statusCode());
            return null;

        }

    }
    
}

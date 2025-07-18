package app.pebl.util;

//importing packages
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//api link: // api link: "https://pebl.fyr.li/api"

@SuppressWarnings("JavadocDeclaration")
public class Connect {
    //fields
    private final HttpClient client = HttpClient.newHttpClient(); //HTTP client;
    private String auth = Config.getInstance().getAuthToken();
    public static final String api = (Config.getInstance().getServerAddr()+"api/"); //The api path for quick use

    /**
     * Method to display information on console based on status code of http request.
     * @param request the request to display
     * @return true if code is 200, false if it isn't
     */
    private boolean checkCode(HttpRequest request, HttpResponse<String> response) {
        //success
        if (response.statusCode() == 200) {
            System.out.println("OK: " + response.statusCode());
            System.out.println("Response Header: "+response.headers()+"\nResponse body: "+response.body());

            //return true
            return true;
        }
        //server error
        else if (response.statusCode() >= 500 && response.statusCode() < 600) {
            System.err.println("Internal Server Error: " + response.statusCode());
            System.err.println("Response Header: "+response.headers()+"\nResponse body: "+response.body());
        }
        //other error
        else {
            System.err.println("Err: " + response.statusCode());
            System.err.println("Request headers: \n"+request.headers()+"\nResponse header: "+response.headers()+"\nResponse body: "+response.body());
        }
        
        //return false after logging error
        return false;
    }

    /**
     * Method to ping the current api server. Logs the request attempt and then creates a HttpRequest for the api endpoint to be sent to the sendRequest() method.
     * @return JSONObject, representing the server response.
     */
    public JSONObject ping() {
        //create uri
        URI uri = URI.create(api);

        //log request
        System.out.println("Pinging server at " + uri);

        //build and send request
        return sendRequest(HttpRequest.newBuilder()
                .uri(URI.create(api))
                .GET()
                .header("Content-Type", "application/json")
                .build());
    }

    /**
     * Method to login the current api server to receive an auth token. Logs the request attempt and then creates a HttpRequest for the api endpoint to be sent to the sendRequest() method.
     * @param body JSONObject, Containing body structure: {"username":"[username]", "password":"[password]"}.
     * @return JSONObject, representing the server response.
     */
    public JSONObject auth(JSONObject body) {
        //create uri
        URI uri = URI.create(api+"auth");

        //log request
        System.out.println("Logging in user on " + uri);

        //build and send request
        return sendRequest(HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("Content-Type", "application/json")
                .build());
    }

    /**
     * Method to check an auth token on the current api server. Logs the request attempt and then creates a HttpRequest for the api endpoint to be sent to the sendRequest() method.
     * @param body JSONObject, Containing body structure: {"error":"none", "success": "true","token": [auth token}
     * @return JSONObject, representing the server response.
     */
    public JSONObject checkAuth(JSONObject body) {
        //create uri
        URI uri = URI.create(api);

        //log request
        System.out.println("Checking auth token validation on " + uri);

        //build and send request
        return sendRequest(HttpRequest.newBuilder()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(body.toJSONString())) // json -> {"error":"none", "success":"true", "token": {the auth token}}
                .header("Content-Type", "application/json")
                .header("Authorization", auth)
                .build());
    }

    /**
     * Method to register a new user on the current api server. Logs the request attempt and then creates a HttpRequest for the api endpoint to be sent to the sendRequest() method.
     * @param body JSONObject, Containing body structure: {"username":"[username]", "password":"[password]", "age":"[age]", "gender":"[gender]"}
     * @return JSONObject, representing the server response.
     */
    public JSONObject register(JSONObject body) {
        //create uri
        URI uri = URI.create(api+"register");

        //log request
        System.out.println("Registering user on " + uri);

        //build and send request
        return sendRequest(HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("Content-Type", "application/json")
                .build());
    }

    /**
     * Method to fetch a user profile on the current api server. Logs the request attempt and then creates a HttpRequest for the api endpoint to be sent to the sendRequest() method.
     * @param body JSONObject, Containing body structure: {"target":"[username]"}
     * @return JSONObject, representing the server response.
     */
    public JSONObject fetchProfile(JSONObject body) {
        //create uri
        URI uri = URI.create(api+"user/profile");

        //log request
        System.out.println("Fetching user profile of: " + body.get("target").toString() + " on " + uri);

        //build and send request
        return sendRequest(HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("Content-Type", "application/json")
                .build());
    }

    /**
     * Method to update the current user profile on the current api server. Logs the request attempt and then creates a HttpRequest for the api endpoint to be sent to the sendRequest() method.
     * @param body JSONObject, Containing body structure: {"age":"[age]", "gender":"[gender]", "status":"[user status]"}
     * @return JSONObject, representing the server response.
     */
    public JSONObject updateProfile(JSONObject body) {
        //create uri
        URI uri = URI.create(api+"user/profile");

        //log request
        System.out.println("Updating user profile on " + uri);

        //build and send request
        return sendRequest(HttpRequest.newBuilder()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("Content-Type", "application/json")
                .header("Authorization", auth)
                .build());
    }

    /**
     * Method to toggle following of a user profile on the current api server. Logs the request attempt and then creates a HttpRequest for the api endpoint to be sent to the sendRequest() method.
     * @param body JSONObject, Containing body structure: {"target":"[username]"}
     * @return JSONObject, representing the server response.
     */
    public JSONObject toggleFollow(JSONObject body) {
        //create uri
        URI uri = URI.create(api+"user/toggleFollow");

        //log request
        System.out.println("Toggling following user: " + body.get("target") + " on " + uri);

        //build and send request
        return sendRequest(HttpRequest.newBuilder()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("Content-Type", "application/json")
                .header("Authorization", auth)
                .build());
    }

    /**
     * Method to fetch a post on the current api server. Logs the request attempt and then creates a HttpRequest for the api endpoint to be sent to the sendRequest() method.
     * @param body JSONObject, Containing body structure: {"id":"[post id]"}
     * @return JSONObject, representing the server response.
     */
    public JSONObject fetchPost(JSONObject body) {
        //create uri
        URI uri = URI.create(api+"post/get");

        //log request
        System.out.println("Fetching post ID: " + body.get("id").toString() + " on " + uri);

        //build and send request
        return sendRequest(HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .header("Content-Type", "application/json")
                .header("id", body.get("id").toString())
                .build());
    }

    /**
     * Method to create a post on the current api server. Logs the request attempt and then creates a HttpRequest for the api endpoint to be sent to the sendRequest() method.
     * @param body JSONObject, Containing body structure: {"content":"[post content]"}
     * @return JSONObject, representing the server response.
     */
    public JSONObject createPost(JSONObject body) {
        //create uri
        URI uri = URI.create(api+"post/create");

        //log request
        System.out.println("Sending new post to " + uri);

        //build and send request
        return sendRequest(HttpRequest.newBuilder()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("Content-Type", "application/json")
                .header("Authorization", auth)
                .build());
    }

    /**
     * Method to like a post on the current api server. Logs the request attempt and then creates a HttpRequest for the api endpoint to be sent to the sendRequest() method.
     * NOTE: liking (skipping) is only possible if user has more than one skip to spare. earn skips by creating posts
     * 1 new post = 1 skip added to user skip bank
     * @param body JSONObject, Containing body structure: {"id":"[post id]"}
     * @return JSONObject, representing the server response.
     */
    public JSONObject likePost(JSONObject body) {
        //create uri
        URI uri = URI.create(api+"post/skip");

        //log request
        System.out.println("Liking post: "+body.get("id"));

        //build and send request
        return sendRequest(HttpRequest.newBuilder()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("Content-Type", "application/json")
                .header("Authorization", auth)
                .build());
    }

    /**
     * Method to fetch feed on the current api server. Logs the request attempt and then creates a HttpRequest for the api endpoint to be sent to the sendRequest() method.
     * @return JSONObject, representing the server response.
     */
    public JSONObject fetchFeed() {
        //create uri
        URI uri = URI.create(api+"feed");

        //log request
        System.out.println("Fetching feed");

        //build and send request
        return sendRequest(HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .header("Content-Type", "application/json")
                .build());
    }

    /**
     * Method to request the leaderboard of users on the current api server. Logs the request attempt and then creates a HttpRequest for the api endpoint to be sent to the sendRequest() method.
     * @return JSONObject, representing the server response.
     */
    public JSONObject fetchLeaderboard() {
        //create uri
        URI uri = URI.create(api+"leaderboard");

        //log request type
        System.out.println("Fetching leaderboard on " + uri);

        //build and send request
        return sendRequest(HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .header("Content-Type", "application/json")
                .build());
    }

    /**
     * Method to handle all http requests. Sends the request given in the parameter to the server and handles errors/responses.
     * @param request HttpRequest to be sent from the client.
     * @return JSONObject, representing the server response.
     */
    private JSONObject sendRequest(HttpRequest request) {
        //setting up the requests and responses and the JSON object to be returned
        HttpResponse<String> response;
        JSONObject responseJSON = null;

        //Send request to server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            //print error to log
            System.err.println("Err: Could Not Complete Request to " + request.uri().toString());
            System.err.println("Message: " + e.getMessage());

            //update GUI
            Platform.runLater(() -> {
                //show general error
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Could Not Complete Request to " + request.uri().toString());
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            });

            //return null for error
            return null;
        }

        //Process status
		if (checkCode(request, response)) {
            //response is a JSONObject
            if (JSONValue.parse(response.body()) instanceof JSONObject) {
                //parse response into JSON Object
                responseJSON = (JSONObject) JSONValue.parse(response.body());
            }
            //response is a JSONArray
            else if (JSONValue.parse(response.body()) instanceof JSONArray) {
                //TODO: Ask Fyrine to encapsulate leaderboard in JSONObject like feed.
                //create new json object
                responseJSON = new JSONObject();
                //encapsulate array in new JSON object
                responseJSON.put("leaderboard", (JSONArray) JSONValue.parse(response.body()));
            }
            //Response is not a JSONObject
            else {
                //create new json object
                responseJSON = new JSONObject();
                //add response from server into json object
                responseJSON.put("status", response.body());
            }

            //check if response contains new auth token
            if (responseJSON.containsKey("token")) {
                //update auth
                auth = (String) responseJSON.get("token"); // update the auth token
                Config.getInstance().setAuthToken(auth);
            }
        }

        //return JSON response, Null if not updated on request completion
        return responseJSON;
    }
}

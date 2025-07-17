package app.pebl.util;

//importing packages
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.net.URI;
import java.io.IOException;
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
     * Updates the value of auth in the .pebl.cfg file
     * @param newAuth new authentication token to update the config file. Set this to empty string to get the value from the config file or to set the auth token to empty string.
     */
    private void authUpdate(String newAuth) {
        Config.getInstance().setAuthToken(newAuth);
    }

    /**
     * Method to display information on console based on status code of http request
     *
     * @param request
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
     * Method to handle all http requests, just enter from the options [checkOnline, checkAuth, register, auth, profileGet, profileUpdate, postGet, postCreate, feed, leaderboard]
     * @param type where you are sending the request to
     * @param body the body
     */
    public JSONObject request(String type, JSONObject body) throws IOException, InterruptedException{
        //setting up the requests and responses and the JSON object to be returned
        HttpRequest request;
        HttpResponse<String> response;
        JSONObject responseJSON = null;

        switch (type) {
            case "checkOnline": //check if server is online
                //build request
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api))
                        .GET()
                        .header("Content-Type", "application/json")
                        .build();

                //Logging request
                System.out.println("Pinging Server");

                //Send request to server
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                //Process status
                if (checkCode(request, response)) {
                    responseJSON = new JSONObject();
                }

                break;

            case "checkAuth": //check if auth token is valid
                //build request
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api))
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toJSONString())) // json -> {"error":"none", "success":"true", "token": {the auth token}}
                        .header("Content-Type", "application/json")
                        .header("Authorization", auth)
                        .build();

                //Logging request
                System.out.println("Validating auth token");

                //Send request to server
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                //Process status
                if (checkCode(request, response)) {
                    responseJSON = new JSONObject();
                }

                break;


            case "register": //create new account and get auth token Warning: the username must be all lowercase (), must include{username, password, age, gender}
                //build request
                request = HttpRequest.newBuilder()
                .uri(URI.create(api+"register"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toJSONString()))
                .header("Content-Type", "application/json")
                .build();

                //Logging request
                System.out.println("Registering user");

                //Send request to server
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                //Process status
                if (checkCode(request, response)) {
                        //parse response into JSONObject
                        responseJSON = (JSONObject) JSONValue.parse(response.body());

                        //update auth
                        auth = (String) responseJSON.get("token"); // update the auth token
                        authUpdate(auth);
                    }

                break;

            case "auth": //NOTE: send username and password to get new token Eg. for login
                //build
                request = HttpRequest.newBuilder()
                .uri(URI.create(api+"auth"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("Content-Type", "application/json")
                .build();

                //Logging request
                System.out.println("Logging in user");

                //Send request to server
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                //Process status
                if (checkCode(request, response)) {
                    //parse the response
                    responseJSON = (JSONObject)JSONValue.parse(response.body()); //parse the response as JSON

                    //update auth
                    auth = (String)responseJSON.get("token"); // update the auth token
                    authUpdate(auth);
                }
                
                break;

            case "profileGet": // get user profile json body must have {target: String username of user}
                //build
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"user/profile"))
                        .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .header("Content-Type", "application/json")
                        .build();

                //Logging request
                System.out.println("Fetching user profile of: "+body.get("target").toString());

                //Send request to server
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
//Process status
                if (checkCode(request, response)) {
                    //parse the response to JSONObject
                    responseJSON = (JSONObject)JSONValue.parse(response.body());
                }
                
                break;

            case "profileUpdate": //update user profile json must include {"age": integer, "gender": boolean, "status": "String"}
                //build
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"user/profile"))
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .header("Content-Type", "application/json")
                        .header("Authorization", auth)
                        .build();

                //Logging request
                System.out.println("Updating user profile");

                //Send request to server
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                //Process status
                if (checkCode(request, response)) {
                    //return empty object to signify success
                    responseJSON = new JSONObject();
                }

                break;


            case "postGet": // get specific post by id body structure: {"id": id integer}
                //build
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"post/get"))
                        .GET()
                        .header("Content-Type", "application/json")
                        .header("id", body.get("id").toString())
                        .build();

                //Logging request
                System.out.println("Fetching post");

                //Send request to server
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                //Process status
                if (checkCode(request, response)) {
                    //parsing response to JSONObject
                    responseJSON = (JSONObject)JSONValue.parse(response.body());
                }

                break;

            case "postCreate": // make post must include {"content": content String} | responds with {"id": id of post}
                //build
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"post/create"))
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .header("Content-Type", "application/json")
                        .header("Authorization", auth)
                        .build();

                //Logging request
                System.out.println("Creating post");

                //Send request to server
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                //Process status
                if (checkCode(request, response)) {
                    //parsing response to JSONObject
                    responseJSON = (JSONObject)JSONValue.parse(response.body());
                }

                break;

            case "feed": // get feed, returns JSONObject of 50 latest posts, body can be empty
                //build
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"feed"))
                        .GET()
                        .header("Content-Type", "application/json")
                        .build();

                //Logging request
                System.out.println("Fetching feed");

                //Send request to server
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                //Process status
                if (checkCode(request, response)) {
                    //parsing response to JSONObject
                    responseJSON = (JSONObject)JSONValue.parse(response.body());
                }

                break;

            case "follow": //following a user body structure: {"target": username String}
                //build
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"user/toggleFollow"))
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .header("Content-Type", "application/json")
                        .header("Authorization", auth)
                        .build();

                //Send request to server
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                //Logging request
                System.out.println("Toggling follow user "+ body.get("target"));

                //Process status
                if (checkCode(request, response)) {

                    //return empty JSONObject to signify success
                    responseJSON = new JSONObject();
                }
                
                break;

            case "like": //liking post Body must include the post ID {"id": id of post as integer}. Responds with the remaining skips a user has
                //IMPORTANT: liking (skipping) is only possible if user has more than one skip to spare. earn skips by creating posts
                // 1 new post = 1 skip added to user skip bank
                //build
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"post/skip"))
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .header("Content-Type", "application/json")
                        .header("Authorization", auth)
                        .build();

                //Logging request
                System.out.println("Liking post: "+body.get("id"));

                //Send request to server
                response = client.send(request, HttpResponse.BodyHandlers.ofString()); //json body: {"id": id} id is integer id of post

                //log response
                checkCode(request, response);
                
                break;

            default:
                //print error
                System.err.println("No such option for request.");
                
                break;
        }

        //return JSON response, Null if not updated on request completion
        return responseJSON;
    }

    /**
     * Method to request the leaderboard of users
     * @return JSONArray of users
     * @throws IOException
     * @throws InterruptedException
     */
    public JSONArray leaderboard() throws IOException, InterruptedException {
        //Declare request, response, JSONArray response
        HttpRequest request;
        HttpResponse<String> response;
        JSONArray responseJSON = null;

        //build
        request = HttpRequest.newBuilder()
                .uri(URI.create(api+"leaderboard"))
                .GET()
                .header("Content-Type", "application/json")
                .build();

        //Logging request
        System.out.println("Fetching leaderboard");

        //Send request to server
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //Process status
        if (checkCode(request, response)) {

            //parse response into JSONArray and return it upon success
            responseJSON = (JSONArray)JSONValue.parse(response.body());
        }

        return responseJSON;

    }
    
}

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
    private final HttpClient client;
    private String auth = Config.getInstance().getAuthToken();
    public static final String api = (Config.getInstance().getServerAddr()+"api/"); //The api path for quick use
    public Connect() {
        client = HttpClient.newHttpClient(); //The client

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
     * Method to display information on console based on status code of http request
     * @param request
     * @return true if code is 200, false if it isn't
     */
    private void checkCode(HttpRequest request, HttpResponse<String> response) {
            //success
           if (response.statusCode() == 200) {
               System.out.println("OK: " + response.statusCode()+"\n");
           }

           else if (response.statusCode() >= 500 && response.statusCode() < 600) {
               System.err.println("Server error: " + response.statusCode()+"\n");
               System.out.println("Response header"+response.headers()+"\nResponse body"+response.body()+"\n");

           }

           else {
               System.err.println("Error: " + response.statusCode());
               System.out.println("Request headers: \n"+request.headers()+"\nResponse header"+response.headers()+"\nResponse body"+response.body()+"\n");
           }

    }

    /**
     * Method to handle all http requests, just enter from the options [checkOnline, checkAuth, register, auth, profileGet, profileUpdate, postGet, postCreate, feed, leaderboard]
     * @param type where you are sending the request to
     * @param body the body
     */
    @SuppressWarnings("unused")

    public JSONObject request(String type, JSONObject body) throws IOException, InterruptedException{
        //setting up the requests and responses and the JSON object to be returned
        HttpRequest request;
        HttpResponse<String> response;
        JSONObject responseJSON;
        switch (type) {
            case "checkOnline": //check if server is online
                //build request

                request = HttpRequest.newBuilder()
                        .uri(URI.create(api))
                        .GET()
                        .header("Content-Type", "application/json")
                        .build();

                //say what is it doing
                System.out.println("Pinging Server");

                //send it
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                //Process status code
                checkCode(request, response);

                //return empty json object signifying success
                if (response.statusCode() == 200) {
                    responseJSON = new JSONObject();
                    return responseJSON;
                }

                break;

            case "checkAuth": //check if auth token is valid
                //adds required fields in the JSONObject

                //body structure: {"error":"none", "success": "true","token": the auth token}
                body.put("token", auth);
                body.put("error", "none");
                body.put("success", "true");

                //build request
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api))
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toJSONString())) // json -> {"error":"none", "success":"true", "token": {the auth token}}
                        .header("Content-Type", "application/json")
                        .header("Authorization", auth)
                        .build();

                //what is it doing
                System.out.println("Validating auth token");

                //send it
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                //Process status code
                checkCode(request, response);

                //return empty json object signifying success
                if (response.statusCode() == 200) {
                    responseJSON = new JSONObject();
                    return responseJSON;
                }

                break;


            case "register": //create new account and get auth token Warning: the username must be all lowercase (), must include{username, password, age, gender}
                //build request
                request = HttpRequest.newBuilder()
                .uri(URI.create(api+"register"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toJSONString()))
                .header("Content-Type", "application/json")
                .build();

                //what is it doing
                System.out.println("Registering user");

                //send it
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                //return json object of token on success
                    if (response.statusCode() == 200) {

                        //parse response into JSONObject
                        responseJSON = (JSONObject) JSONValue.parse(response.body());
                        System.out.println(responseJSON);
                        //update auth
                        auth = (String)responseJSON.get("token"); // update the auth token
                        authUpdate(auth);

                        //Process status code
                        checkCode(request, response);
                        return responseJSON;
                    }

                    //Process status code if it isn't 200
                    checkCode(request, response);


                break;

            case "auth": //send username and password to get new token aka LOGIN
                //build
                request = HttpRequest.newBuilder()
                .uri(URI.create(api+"auth"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("Content-Type", "application/json")
                .build();

                //what is it doing
                System.out.println("Logging in user");

                //send it
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                //success
                if (response.statusCode() == 200) {
                    //parse the response
                    responseJSON = (JSONObject)JSONValue.parse(response.body()); //parse the response as JSON

                    //update auth
                    auth = (String)responseJSON.get("token"); // update the auth token
                    authUpdate(auth);

                    //check status code
                    checkCode(request, response);
                    return responseJSON;
                }

                //check status code
                checkCode(request,response);
                break;

            case "profileGet": // get user profile json body must have {target: String username of user}
                //build
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"user/profile"))
                        .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .header("Content-Type", "application/json")
                        .build();

                //what is it doing
                System.out.println("Getting user profile of: "+body.get("target").toString());

                //send it
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {

                    //parse the response to JSONObject
                    responseJSON = (JSONObject)JSONValue.parse(response.body());
                    System.out.println(responseJSON);

                    //check status code
                    checkCode(request, response);
                    return responseJSON;
                }

                //check status
                checkCode(request, response);
                break;

            case "profileUpdate": //update user profile json must include {"age": integer, "gender": boolean, "status": "String"}
                //build
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"user/profile"))
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .header("Content-Type", "application/json")
                        .header("Authorization", auth)
                        .build();

                //what is it doing
                System.out.println("Updating user profile");

                //send it
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                //success
                if (response.statusCode() == 200) {
                    //return empty object to signify success
                    responseJSON = new JSONObject();

                    //check status code
                    checkCode(request, response);
                    return responseJSON;
                }

                //fail, check status
                checkCode(request, response);

                break;


            case "postGet": // get specific post by id body structure: {"id": id integer}
                //build
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"post/get"))
                        .GET()
                        .header("Content-Type", "application/json")
                        .header("id", body.get("id").toString())
                        .build();

                //what is it doing
                System.out.println("Getting post");

                //send it
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    //parsing response to JSONObject
                    responseJSON = (JSONObject)JSONValue.parse(response.body());

                    //check status code
                    checkCode(request, response);
                    return responseJSON;
                }
                //check status code
                checkCode(request, response);
                break;

            case "postCreate": // make post must include {"content": content String} | responds with {"id": id of post}

                //build
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"post/create"))
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .header("Content-Type", "application/json")
                        .header("Authorization", auth)
                        .build();

                //what is it doing
                System.out.println("Creating post");

                //send it
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                //success
                if (response.statusCode() == 200) {
                    //parsing response to JSONObject
                    responseJSON = (JSONObject)JSONValue.parse(response.body());

                    //check status code
                    checkCode(request, response);
                    return responseJSON;
                }
                //fail, check status code
                checkCode(request, response);
                break;

            case "feed": // get feed, returns JSONObject of 50 latest posts, body can be empty
                //build
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"feed"))
                        .GET()
                        .header("Content-Type", "application/json")
                        .build();

                //what is it doing
                System.out.println("Getting feed");

                //send it
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                //success
                if (response.statusCode() == 200) {
                    //parsing response to JSONObject
                    responseJSON = (JSONObject)JSONValue.parse(response.body());

                    //check status code
                    checkCode(request, response);
                    return responseJSON;
                }
                //fail, check status code
                checkCode(request, response);

                break;

            case "follow": //following a user body structure: {"target": username String}
                //build
                request = HttpRequest.newBuilder()
                        .uri(URI.create(api+"user/toggleFollow"))
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .header("Content-Type", "application/json")
                        .header("Authorization", auth)
                        .build();

                //send it
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                //what is it doing
                System.out.println("Toggling follow user "+ body.get("target"));

                //success
                if (response.statusCode() == 200) {

                    //return empty JSONObject to signify success
                    responseJSON = new JSONObject();

                    //check status code
                    checkCode(request, response);
                    return responseJSON;
                }

                //fail, check status code
                checkCode(request, response);
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

                //what is it doing
                System.out.println("Liking post: "+body.get("id"));

                //send it
                response = client.send(request, HttpResponse.BodyHandlers.ofString()); //json body: {"id": id} id is integer id of post

                //check status code
                checkCode(request, response);
                break;

            default:
                System.out.println("No such option for request.");
                break;
        }

        //return null upon fail
        return null;
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
        JSONArray responseJSON;

        //build
        request = HttpRequest.newBuilder()
                .uri(URI.create(api+"leaderboard"))
                .GET()
                .header("Content-Type", "application/json")
                .build();

        //what is it doing
        System.out.println("Getting leaderboard");

        //send it
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //success
        if (response.statusCode() == 200) {

            //parse response into JSONArray and return it upon success
            responseJSON = (JSONArray)JSONValue.parse(response.body());

            //check status code (new object in body parameter because this method does not use body)
            checkCode(request, response);
            return responseJSON;
        }

        //fail, check status code (new object in body parameter because this method does not use body)
        checkCode(request, response);
        return null;

    }
    
}

package app.pebl.posts;

import java.util.Date;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

/**
 * Posts class
 */
public class Posts {
    private String id;
    private String sender;
    private String content;
    private int skips;
    private Date date;

    /**
     * Empty constructor
     */
    public Posts(){

    }
    //TODO add a Post constructor that contacts the api to get a post ID for syncronisation
}

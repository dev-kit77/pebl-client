package app.pebl.posts;

import java.util.Date;

/**
 * Posts class
 */
public class Post {
    private String id;
    private String sender;
    private String content;
    private int skips;
    private Date date;

    /**
     * Post Constructor
     * Must be called after sending a request to get the Post that was just made.
     */
    public Post(String id, String sender, String content, int skips, long timestamp) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.skips = skips;
        this.date = new Date((long) timestamp);
    }

    /**
     *
     * @return the String id of the post TODO change the id to integer if its discovered that its an integer!
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return the username of the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     *
     * @return the String value of the post content
     */
    public String getContent() {
        return content;

    }

    /**
     *
     * @return the skips of the user as an integer
     */
    public int getSkips() {
        return skips;
    }

    /**
     *
     * @return the Date object of when the post was created
     */
    public Date getDate() {
        return date;
    }
}

package app.pebl.data;

import java.util.Date;

/**
 * Posts class
 */
public class Post {
    private int id;
    private String author;
    private String content;
    private int skips;
    private Date date;

    /**
     * Post Constructor
     * Must be called after sending a request to get the Post that was just made.
     */
    public Post(int id, String author, String content, int skips, long timestamp) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.skips = skips;
        this.date = new Date((long) timestamp);
    }

    /**
     *
     * @return the String id of the post
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return the username of the author
     */
    public String getSender() {
        return author;
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

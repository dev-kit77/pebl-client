package app.pebl;


import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;

public class Connect {


    public Connect(){
        
    }

    //TODO add different functions (or reuse this one) to get data on users and another for getting posts.
    public void getResponse(){
        // Try block to check exceptions
        try {
            String val;
 
            // Constructing the URL connection
            // by defining the URL constructors
            URL URL = new URL(
                "https://pebble-api.fly.dev/");
 
            // Reading the response of the server
            BufferedReader br = new BufferedReader(
                new InputStreamReader(URL.openStream()));
 
            /* Catching the string and  if found any null
             character break the String */
            while ((val = br.readLine()) != null) {
                System.out.println(val);
            }
 
            // Closing the file
            br.close();
        }
 
        // Catch block to handle exceptions
        catch (Exception ex) {
 
            // No file found
            System.out.println(ex.getMessage());
        }
    }

}

package nl.gerardverbeek.services;

import nl.gerardverbeek.model.GoogleLocation;
import com.google.gson.Gson;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;


/**
 * Created by gerardverbeek on 05/11/15.
 */
@Service
public class GoogleLocationService {

    @Value("${googleLocation.databaseUrl}")
    private String databaseUrl;

    public String changeCoordinate(String coordinate) {
        int split = 0;
        int length = coordinate.length();
        if(length == 8){
            split = 1;
        } else if(length == 9) {
            split = 2;
        } else if(length == 10) {
            split = 3;
        }
        return coordinate.substring(0, split) + "." + coordinate.substring(split, coordinate.length());
    }

    public boolean parseFile() {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("/Users/gerardverbeek/Downloads/Takeout 2/Locatiegeschiedenis/Locatiegeschiedenis_original.json"));
            JSONObject jsonObject = (JSONObject) obj;

            JSONArray locations = (JSONArray) jsonObject.get("locations");
            int size = locations.size();
            List list = new ArrayList<GoogleLocation>();

            for(int i = 0; i < size; i++ ) {
                JSONObject location = (JSONObject) locations.get(i);

                String timestampMs = (String) location.get("timestampMs");
                String longitude = changeCoordinate(location.get("longitudeE7").toString());
                String latitude = changeCoordinate(location.get("latitudeE7").toString());

                System.out.println("long: " + longitude);
                System.out.println("lat: " + latitude);
                System.out.println("timestampMs: " + timestampMs);

                GoogleLocation googleLocation = new GoogleLocation();
                googleLocation.setLatitude(latitude);
                googleLocation.setLongitude(longitude);
                googleLocation.setTime(timestampMs);

                JSONObject googleJson = new JSONObject();
                googleJson.put("timestampMs", timestampMs);
                googleJson.put("location", latitude+","+longitude);

                System.out.println("Start printing..");
                sendObject(databaseUrl, googleJson);
                System.out.println("..end printing");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean sendObject(String endpoint, JSONObject googleLocationJson) {
        HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead

        try{
            String postUrl = endpoint;
            HttpPost post = new HttpPost(postUrl);
            StringEntity requestEntity = new StringEntity(
                    googleLocationJson.toString(),
                    "application/json",
                    "UTF-8");
            post.setEntity(requestEntity);
            post.setHeader("Content-type", "application/json");
            HttpResponse  response = httpClient.execute(post);
            System.out.println("response: " + response);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to send docuemnt");
            System.out.println("Exception: " + e);
            return false;
        }
    }

    public JSONObject createJsonObject(String time, String longitude, String latitude) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("time", time);
        jsonObject.put("longitude", longitude);
        jsonObject.put("latitude", latitude);
        return jsonObject;
    }
}

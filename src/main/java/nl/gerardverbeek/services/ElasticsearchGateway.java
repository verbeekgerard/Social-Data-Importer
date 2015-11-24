package nl.gerardverbeek.services;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

/**
 * Created by gerardverbeek on 08/11/15.
 */
@Service
public class ElasticsearchGateway implements DocumentGateway {

    @Override
    public boolean sendDocument(String endPoint, Object documentToSend) {
        HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead

        try{
            String postUrl = endPoint;
            HttpPost post = new HttpPost(postUrl);
            StringEntity requestEntity = new StringEntity(
                    documentToSend.toString(),
                    "application/json",
                    "UTF-8");
            post.setEntity(requestEntity);
            post.setHeader("Content-type", "application/json");
            HttpResponse response = httpClient.execute(post);
            System.out.println("response: " + response);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to send docuemnt");
            System.out.println("Exception: " + e);
            return false;
        }

    }
}

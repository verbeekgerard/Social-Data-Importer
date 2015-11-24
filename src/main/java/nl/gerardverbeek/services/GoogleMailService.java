package nl.gerardverbeek.services;

import net.fortuna.ical4j.model.DateTime;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by gerardverbeek on 09/11/15.
 */
@Service
public class GoogleMailService implements ParseFileInterface {

    @Autowired
    ElasticsearchGateway documentGateway;

    @Value("${googleMail.filesLocation}")
    private String filesFolder;

    @Value("${googleMail.fileLocationExport}")
    private String fileLocationExport;

    @Override
    public String parseFile() {

        ArrayList<String> listWithPaths = getAllFilePaths(filesFolder);

        for (String path : listWithPaths) {
            JSONObject jsonObject = parseFileToJson(path);
            if (jsonObject != null) {
                System.out.println(jsonObject.toJSONString());

                //Send to
            }
        }
        return null;
    }

    private JSONObject parseFileToJson(String path) {
        JSONObject jsonObject;
        try {
            jsonObject = parseFileToJson(path, Charset.defaultCharset());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("ArrayIndexOutOfBoundsException: " + e);
            return null;
        } catch (UncheckedIOException ue) {
            System.out.println("Failed with defaultCharset: ");
            try {
                System.out.println("Try with iso-8859-1: ");
                jsonObject = parseFileToJson(path, Charset.forName("iso-8859-1"));
            } catch (Exception e) {
                System.out.println("Failed with iso-8859-1: ");
                return null;
            }
            return jsonObject;
        } catch (MalformedInputException me) {
            System.out.println("MalformedInputException: " + me);
            return null;
        } catch (IOException e) {
            return null;
        }
        return jsonObject;
    }

    private JSONObject parseFileToJson(String pathToHtmlFile, Charset charset) throws IOException {
        Stream<String> lines = Files.lines(Paths.get(pathToHtmlFile), charset);
        Object[] linesAsArray = lines.toArray();
        JSONObject jsonObject = new JSONObject();

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i<linesAsArray.length; i++){
            String line = (String) linesAsArray[i];
            if (line.contains("header-part1")) {
                jsonObject.put("metaData", getMetaData(line));
            }if(i>6) {
                builder.append(getMessage((String)linesAsArray[i]));
            }
        }

        jsonObject.put("message", builder.toString());

        lines.close();
        return jsonObject;
    }

    private String getMessage(String rawMessage) {
        String striped = Jsoup.parse(rawMessage).text().trim();
        if(!striped.equals("") && !striped.contains("@") && !striped.contains(";") && !striped.contains("{") && !striped.contains("}")){
            return striped+" ";
        }
        return "";
    }


    private JSONObject getMetaData(String line) {
        String[] strings = line.split("<b>");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("subject", getSubject(strings[1]));
        jsonObject.put("from", getSender(strings[2]));
        jsonObject.put("to", getReceiver(strings[4]));
        jsonObject.put("epochSeconds", getDateTime(strings[3]));
        return jsonObject;
    }

    private String getDateTime(String line) {
        String dateTimeRaw = line.split("Datum: </b>")[1].split("</td>")[0];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeRaw, formatter);
        ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
        return Long.toString(dateTime.atZone(zoneId).toEpochSecond());
    }

    private JSONObject getReceiver(String line) {
        JSONObject jsonObject = new JSONObject();
        String receiverRaw = line.split("Aan: </b>")[1].split("</td></tr></table><br>")[0];
        String[] receiverRawArray = receiverRaw.split("<");
        if (receiverRawArray.length < 2) {
            receiverRawArray = receiverRaw.split("&lt;");
        }
        String receiverName = receiverRawArray[0].trim().replaceAll("&quot;", "").replaceAll("&#39;", "");
        String receiverEmail = null;
        if (receiverRawArray.length > 1) {
            receiverEmail = receiverRawArray[1].replaceAll("&lt;", "").replaceAll("&gt;", "").replaceAll(",", "").replaceAll(">", "").split(" ")[0].trim();
        }


        if (receiverEmail != null && receiverEmail.contains("@")) {
            jsonObject.put("receiverEmail", receiverEmail);
            jsonObject.put("receiverName", receiverName);
        } else if (receiverName.contains("@")) {
            jsonObject.put("receiverEmail", receiverName);
        }

        return jsonObject;

    }


    private JSONObject getSender(String line) {
        JSONObject jsonObject = new JSONObject();
        String senderRaw = line.split("Van: </b>")[1].split("</td></tr><tr><td>")[0];
        String[] senderRawArray = senderRaw.split("<");
        String senderName = senderRawArray[0].trim();
        String senderEmail = null;
        if (senderRawArray.length > 1) {
            senderEmail = senderRawArray[1].replace("<", "").replace(">", "").trim();
        }
        jsonObject.put("senderName", senderName);
        jsonObject.put("senderMail", senderEmail);
        return jsonObject;
    }

    private String getSubject(String line) {
        return line.split("Onderwerp: </b>")[1].split("</td>")[0];
    }


    /**
     * Get all the files from a folder
     *
     * @param folder
     * @return
     */
    private ArrayList<String> getAllFilePaths(String folder) {
        ArrayList<String> list = new ArrayList<>();
        try {
            Files.walk(Paths.get(folder))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList())
                    .forEach(path -> list.add(path.toString()));
        } catch (IOException e) {
            System.out.println("Exception : " + e);
        }

        return list;
    }

}

package nl.gerardverbeek.services;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gerardverbeek on 08/11/15.
 */
@Service
public class GoogleCalendarService {
    @Autowired
    ElasticsearchGateway documentGateway;

    @Value("${googleCalendar.fileLocation}")
    private String fileLocation;

    @Value("${googleCalendar.databaseUrl}")
    private String databaseUrl;



    public boolean parseFile() {

//        preProcessFile();
        int count = 0;

        try {
            List<String> lines = Files.lines(Paths.get(fileLocation)).collect(Collectors.toList());
            int size = lines.size();
            boolean event = false;
            JSONObject eventJson = new JSONObject();
            for (int i = 0; i < size; i++) {
                if (lines.get(i).equals("BEGIN:VEVENT")) {
                    System.out.println("Found Begin");
                    eventJson = new JSONObject();
                    event = true;
                }

                if(event == true){
                    if(lines.get(i).startsWith("DTSTART")){
                        eventJson.put("startDate", lines.get(i).split(":")[1]);
                    }
                    else if(lines.get(i).startsWith("DTEND")){
                        eventJson.put("endDate", lines.get(i).split(":")[1]);
                    }
                    else if(lines.get(i).startsWith("SUMMARY:")){
                        String [] summaryList = lines.get(i).split(":");
                        if(summaryList != null) {
                            if(summaryList.length > 1){
                                eventJson.put("title", lines.get(i).split(":")[1]);
                            }else {
                                System.out.println("SummaryList.length <= 0");
                            }
                        }else {
                            System.out.println("SummaryList == null");
                        }
                    }
                }

                if (lines.get(i).equals("END:VEVENT")) {
                    System.out.println("Found END!!");
                    documentGateway.sendDocument(databaseUrl, eventJson);
                    System.out.println("count = "+ count);
                    count++;
                    event = false;
                }
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }



    private Calendar getCalendarObjectFromFile(){
        try{
            FileInputStream fin = new FileInputStream(fileLocation);
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(fin);
            return calendar;

        } catch (Exception e) {
            return null;
        }
    }

}

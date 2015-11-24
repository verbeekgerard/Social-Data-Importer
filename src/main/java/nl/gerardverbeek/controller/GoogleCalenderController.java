package nl.gerardverbeek.controller;

import nl.gerardverbeek.services.GoogleCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by gerardverbeek on 08/11/15.
 */
@Controller
public class GoogleCalenderController {

    @Autowired
    GoogleCalendarService googleCalendarService;

    @RequestMapping(value="/parseGoogleCalendarFile", method= RequestMethod.GET)
    public String parseFile(){
        if(googleCalendarService.parseFile()){
            return "true";
        } else {
            return "false";
        }
    }
}

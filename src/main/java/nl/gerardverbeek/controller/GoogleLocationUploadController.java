package nl.gerardverbeek.controller;


import nl.gerardverbeek.services.GoogleLocationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by gerardverbeek on 05/11/15.
 */
@Controller
public class GoogleLocationUploadController {

    @Autowired
    GoogleLocationService googleLocationService;

    @RequestMapping(value="/upload", method= RequestMethod.GET)
    public @ResponseBody
    String provideUploadInfo() {
        return "Upload to the same url with POST.";
    }


    @RequestMapping(value="/parseLocationFile")
    public void parseFile(){
        googleLocationService.parseFile();
    }
}

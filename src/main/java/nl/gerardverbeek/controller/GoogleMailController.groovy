package nl.gerardverbeek.controller

import nl.gerardverbeek.services.GoogleMailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * Created by gerardverbeek on 09/11/15.
 */
@Controller
class GoogleMailController {

    @Autowired
    GoogleMailService googleMailService;

    @RequestMapping(value = "/parseMail", method =RequestMethod.GET)
    public String parseFile(){
        googleMailService.parseFile();
        return "true";
    }
}

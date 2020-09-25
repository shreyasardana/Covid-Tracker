package com.labproject.covidtracker.controllers;

import com.labproject.covidtracker.models.LocationInfo;
import com.labproject.covidtracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
//TO SET ATTRIBUTES THAT HAVE TO BE DISPLAYED IN BROWSER
//to render data in UI format
//the way to get any page to show up any accessed url is by creating a controller

@Controller//because we want to render an html UI
public class HomeController {

    //Autowiring service into controller so that controller can access allocation list/data of service class
    @Autowired//so that it controller gets access to the data in service class
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")// "/" represents root url that has to be mapped with the html file
    //method to return styles of UI by mapping it with the html file
    public String home(Model model){//model-object which has data that has to rendered in the html
        //model.addAttribute("testName","TEST");//setting attribute "testName" with value "TEST"

        //to add info of the allLocation list in the model
        model.addAttribute("locationInfo",coronaVirusDataService.getAllLocation());

        //to find sum of all reported cases
        List<LocationInfo> allLoc=coronaVirusDataService.getAllLocation();
        int totalReportedCases = allLoc.stream().mapToInt(stat -> stat.getLatestCases()).sum();
        int newReportedCases = allLoc.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
        model.addAttribute("totalReportedCases",totalReportedCases);
        model.addAttribute("newReportedCases",newReportedCases);




        return "home";//returns the name of html file that points to the template folder

    }
    //It works because of thymeleaf dependency
}

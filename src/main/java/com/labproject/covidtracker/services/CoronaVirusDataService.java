package com.labproject.covidtracker.services;

import com.labproject.covidtracker.models.LocationInfo;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/*
when the application runs we want some code to execute and call the url(having the covid confirmed cases) and get the
data here. This class is going to give us the data and when the application loads which makes the call to code and then
fetches the data.
 */

@Service//used with classes that provide some business functionalities.
// Makes this class a spring service

public class CoronaVirusDataService {

    //this is the url from where the data is being fetched. this url is a .csv file which holds all the confirmed cases
    // upto the current data of all different locations
    private static String URL="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private List<LocationInfo> allLocation=new ArrayList<>();//creating an arraylist of the locationInfo class so that after creating instance of this
    // we can add info to it everytime we make a get request and have a bunch of records


    //for controller
    public List<LocationInfo> getAllLocation() {
        return allLocation;
    }

    @PostConstruct//runs this method after instance of this service class is created
    // used on a method that needs to be executed after dependency injection is done to perform any initialization.

    @Scheduled(cron="* * 1 * * *")//used to schedule the run of a method on a regular basis
    //runs once every day
    //sec min hr day month year


    // this function is going to make the http call to the url to get/fetch the data
    public void fetchVirusData() throws IOException, InterruptedException {

        //new list: to remove concurrency issues
        List<LocationInfo> newLocation=new ArrayList<>();

        HttpClient client=HttpClient.newHttpClient();//to make http calls we make its client
        HttpRequest request=HttpRequest.newBuilder().uri(URI.create(URL)).build();//saying where do we need to do the httpRequest
        //Builder class provides a bunch of methods which we can use to configure our request.
        //HttpRequest – represents the request to be sent via the HttpClient
        //HttpClient – behaves as a container for configuration information common to multiple requests
        //HttpResponse – represents the result of an HttpRequest call

        //client sends a synchronous GET request and response is stored in httpRespose
        HttpResponse<String> httpResponse=client.send(request, HttpResponse.BodyHandlers.ofString());
        //ofString method converts the response body bytes to string
        //BodyHandlers handel the response body
        //System.out.println(httpResponse.body());



        //to show the csv file data. to manage the printed string of data from the url we use csv library which is added as a dependency.
        // To convert the string to objects so that we can access each column as we want
        /*Some CSV files define header names in their first record. If configured, Apache Commons CSV can parse the
        header names from the first record. This will use the values from the first record as header names and skip the
        first record when iterating.
        */

        //to convert response string to reader
        StringReader csvBodyReader=new StringReader(httpResponse.body());
        //StringReader: instance of reader that parses a string

        //to parse the string reader using the open source library i.e. apache csv
        //access colum while skipping header
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        //looping through the records after the parse which are the objects/columns returned when http request fetches the data.
        // we want to save the data in a model class so that we can use it easier
        for (CSVRecord record : records) {
            LocationInfo locationInfo=new LocationInfo();
            locationInfo.setState(record.get("Province/State"));
            locationInfo.setCountry(record.get("Country/Region"));
            int latestCases = Integer.parseInt(record.get(record.size()-1));
            int prevDayCases = Integer.parseInt(record.get(record.size()-2));
            locationInfo.setLatestCases(latestCases);//gives the last updated column
            locationInfo.setDiffFromPrevDay(latestCases-prevDayCases);
            //System.out.println(locationInfo);
            newLocation.add(locationInfo);
            //String state = record.get("Province/State");
            //System.out.println(state);//prints the column we want : here prints all the province/state from the url
        }
        this.allLocation=newLocation;

    }
}

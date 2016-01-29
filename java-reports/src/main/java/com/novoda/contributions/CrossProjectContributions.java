package com.novoda.contributions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CrossProjectContributions {

    // https://github.com/floatschedule/api

    // ttps://api.floatschedule.com/api/v1/people?active=1

    public static void main(String[] args) throws IOException {
        String floatAccessToken = args[0];
        //TODO
        // Find out what developer is on what project
        // Find out if X developer has commented on / merged / closed another projects PR

        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("https://api.floatschedule.com/api/v1/")
                .build();



        // Input needed - Date Range

        String inputStartDate = "2016-01-11";
        String inputEndDate = "2016-01-18";
        LocalDateTime startDateTime = LocalDateTime.parse(inputStartDate + "T00:00:00");
        LocalDateTime endDateTime = LocalDateTime.parse(inputEndDate + "T00:00:00");

        FloatWebService floatWebService = retrofit.create(FloatWebService.class);
        // Pull down the list of all projects
        Call<ApiProjects> projects = floatWebService.getProjects(floatAccessToken);
        System.out.println(projects.execute().body());
        // Pull down all people from float

        Call<ApiPeople> people = floatWebService.getPeople(floatAccessToken);
        ApiPeople apiPeople = people.execute().body();

        // Filter 'all people' to get just the developers
        Stream<ApiPeople.ApiPerson> craftsmen = apiPeople.people
                .parallelStream()
                .filter(p -> p.jobTitle.toLowerCase().contains("craftsman"));

        System.out.println(craftsmen.collect(Collectors.toList()));


        // Pull down all tasks for daterange

        Call<ApiTasks> tasks = floatWebService.getTasks(floatAccessToken, inputStartDate, 2);
        System.out.println(tasks.execute().body());


        // From the tasks get all tasks for person X
        // From person X's tasks find all project names

        // Query each github repo


    }



}

//October
//Ataul  The Times (1/10/16 - 12/10/16) , All4 (13/10/16 - 15/10/16), The Times (15/10/16 , 31/10/16)
//Paul    All4 (1/10/16 - 31/10/16)
//
//October
//The Times, All4, Oddschecker
//
//Paul has commented on?  The times(1/10/16 - 31/10/16) / Oddschecker(1/10/16 - 31/10/16)
//Ataul has commented on? , Oddschecker(1/10/16 - 31/10/16), All4 (1/10/16 - 12/10/16), The Times (13/10/16 - 15/10/16), All4 (15/10/16 , 31/10/16)
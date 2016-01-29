package com.novoda.contributions;

import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * https://github.com/floatschedule/api
 */
public class CrossProjectContributions {

    public static void main(String[] args) throws IOException {
        String floatAccessToken = args[0];
        // Find out what developer is on what project
        // Find out if X developer has commented on / merged / closed another projects PR

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.floatschedule.com/api/v1/")
                .build();


        // Input needed - Date Range

        String inputStartDate = "2016-01-11";
        String inputEndDate = "2016-01-18";
        // TODO calculate this
        int inputNumberOfWeeks = 2;

        FloatWebService floatWebService = retrofit.create(FloatWebService.class);
        // Pull down the list of all projects
        Call<ApiProjects> projects = floatWebService.getProjects(floatAccessToken);
//        System.out.println(projects.execute().body());

        // Pull down all people from float
        Call<ApiPeople> people = floatWebService.getPeople(floatAccessToken);
        ApiPeople apiPeople = people.execute().body();
//        System.out.println(apiPeople);
        // Filter 'all people' to get just the developers
        // Filter contractors
        List<ApiPeople.ApiPerson> craftsmen = apiPeople.people
                .parallelStream()
                .filter(p -> p.jobTitle.toLowerCase().contains("craftsman"))
                .filter(p -> p.contractor != 1)
                .collect(Collectors.toList());
        System.out.println(craftsmen);

        // Pull down all tasks for daterange
        Call<ApiTasks> allTasks = floatWebService.getTasks(floatAccessToken, inputStartDate, inputNumberOfWeeks);
        ApiTasks apiTasks = allTasks.execute().body();

        // Find all the tasks for just the developers
        Set<String> craftsmenIds = craftsmen
                .parallelStream()
                .map(ApiPeople.ApiPerson::getPersonId)
                .collect(Collectors.toSet());

        List<ApiTasks.ApiPeopleWithTasks> craftsmenTasks = apiTasks.people
                .parallelStream()
                .filter(p -> craftsmenIds.contains(p.personId))
                .collect(Collectors.toList());
//        System.out.println(craftsmenTasks);

        // Change person id's into peoples names
        List<CraftsmanWithTasks> list = craftsmenTasks
                .parallelStream()
                .map(apiPeopleWithTasks -> {
                    CraftsmanWithTasks craftsmanWithTasks = new CraftsmanWithTasks();
                    craftsmen.forEach(apiPerson -> {
                        if (apiPerson.personId.equals(apiPeopleWithTasks.personId)) {
                            craftsmanWithTasks.name = apiPerson.name;
                        }
                    });
                    craftsmanWithTasks.tasks = apiPeopleWithTasks.tasks;
                    return craftsmanWithTasks;
                })
                .collect(Collectors.toList());
        System.out.println(list);


        // From person X's tasks find all project names


        // Query each github repo


    }

    public static class CraftsmanWithTasks {
        public String name;
        public List<ApiTasks.ApiTask> tasks;

        @Override
        public String toString() {
            return "CraftsmanWithTasks{" +
                    "name='" + name + '\'' +
                    ", tasks=" + tasks +
                    '}';
        }
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
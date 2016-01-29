package com.novoda.contributions;

import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ContributionTracker {

    private final String floatAccessToken;

    public ContributionTracker(String floatAccessToken) {
        this.floatAccessToken = floatAccessToken;
    }

    public String track(String startDate, String endDate) throws IOException {
        validateDateFormat(startDate, endDate);
        // TODO calculate this
        int inputNumberOfWeeks = 2;
        // Find out what developer is on what project
        // Find out if X developer has commented on / merged / closed another projects PR

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.floatschedule.com/api/v1/")
                .build();
        FloatWebService floatWebService = retrofit.create(FloatWebService.class);

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
        Call<ApiTasks> allTasks = floatWebService.getTasks(floatAccessToken, startDate, inputNumberOfWeeks);
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


        return "todo work out output format";
    }

    private void validateDateFormat(String startDate, String endDate) {
        String dateFormat = "\\d\\d\\d\\d-\\d\\d-\\d\\d";
        if(!startDate.matches(dateFormat)) {
            throw new DateTimeParseException("StartDate Format should be YYYY-MM-DD", startDate, 0);
        }
        if(!endDate.matches(dateFormat)) {
            throw new DateTimeParseException("EndDate Format should be YYYY-MM-DD", endDate, 0);
        }
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

package com.novoda.contributions.floatcom;

import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FloatContributionTracker {

    private final String floatAccessToken;

    public FloatContributionTracker(String floatAccessToken) {
        this.floatAccessToken = floatAccessToken;
    }

    /**
     * Find out what developer is on what project
     *
     * @param startDate the first date you want to track from YYYY-MM-DD
     * @param endDate   the date you want to stop tracking at YYYY-MM-DD
     * @return TODO
     * @throws IOException
     */
    public List<FloatDevs> track(String startDate, String endDate) throws IOException {
        validateDateFormat(startDate, endDate);
        // TODO calculate this
        int inputNumberOfWeeks = 2;

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.floatschedule.com/api/v1/")
                .build();
        FloatWebService floatWebService = retrofit.create(FloatWebService.class);

        // Pull down all people from float
        Call<ApiPeople> people = floatWebService.getPeople(floatAccessToken);
        ApiPeople apiPeople = people.execute().body();

        // Filter 'all people' to get just the developers
        // Filter contractors
        List<ApiPeople.ApiPerson> craftsmen = apiPeople.people
                .parallelStream()
                .filter(p -> p.jobTitle.toLowerCase().contains("craftsman"))
                .filter(p -> p.contractor != 1)
                .collect(Collectors.toList());

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

        // Change person id's into peoples names

        return craftsmenTasks
                .parallelStream()
                .map(apiPeopleWithTasks -> {
                    final String[] craftsmanName = new String[1];
                    craftsmen.forEach(apiPerson -> {
                        if (apiPerson.personId.equals(apiPeopleWithTasks.personId)) {
                            craftsmanName[0] = apiPerson.name;
                        }
                    });
                    List<FloatDevs.Task> tasks = new ArrayList<>();
                    apiPeopleWithTasks.tasks.forEach(apiTask -> {
                        tasks.add(new FloatDevs.Task(apiTask.projectName, apiTask.startDate, apiTask.endDate));
                    });
                    return new FloatDevs(craftsmanName[0], tasks);
                })
                .collect(Collectors.toList());
    }

    private void validateDateFormat(String startDate, String endDate) {
        String dateFormat = "\\d\\d\\d\\d-\\d\\d-\\d\\d";
        if (!startDate.matches(dateFormat)) {
            throw new DateTimeParseException("StartDate Format should be YYYY-MM-DD", startDate, 0);
        }
        if (!endDate.matches(dateFormat)) {
            throw new DateTimeParseException("EndDate Format should be YYYY-MM-DD", endDate, 0);
        }
    }

}

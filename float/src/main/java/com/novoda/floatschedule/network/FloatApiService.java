package com.novoda.floatschedule.network;

import com.novoda.floatschedule.people.People;
import com.novoda.floatschedule.project.Projects;
import com.novoda.floatschedule.task.Assignments;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface FloatApiService {

    @GET("people")
    Observable<Response<People>> getPeople();

    @GET("projects")
    Observable<Response<Projects>> getProjects();

    /**
     * @param startDate assignments starting after this date. its format is YYYY-MM-dd
     * @param weeks number of weeks to search up to, starting on startDate
     * @param peopleId the person id to target, if any
     */
    @GET("tasks")
    Observable<Response<Assignments>> getTasks(@Query("start_day") String startDate,
                                               @Query("weeks") Integer weeks,
                                               @Query("people_id") Integer peopleId);
}

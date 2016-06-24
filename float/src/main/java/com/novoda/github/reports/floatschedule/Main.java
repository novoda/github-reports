package com.novoda.github.reports.floatschedule;

import com.novoda.github.reports.floatschedule.people.PeopleServiceClient;
import com.novoda.github.reports.floatschedule.people.Person;
import com.novoda.github.reports.floatschedule.project.Project;
import com.novoda.github.reports.floatschedule.project.ProjectServiceClient;
import com.novoda.github.reports.floatschedule.task.Task;
import com.novoda.github.reports.floatschedule.task.TaskServiceClient;

import rx.Subscriber;

public class Main {

    public static void main(String[] args) {

        TaskServiceClient taskServiceClient = TaskServiceClient.newInstance();

        //getPersons();
        //getProjects();
        //getTasks();

        /*
        TO DO
            . get tasks for a given user
            . get projects
            . determine to which repositories the user is assigned to
        */

        // . paul requests data for "adam brown"
        // TODO get "adam brown"'s github handle        -> json file
        // TODO get "adam brown"'s float projects       -> float api
        // TODO get projects' repositories              -> json file
        // we're now ready to hit github-stats

        // 1.



        System.out.println("... DONE!");

    }

    private static void getTasks() {
        TaskServiceClient taskServiceClient = TaskServiceClient.newInstance();
        taskServiceClient.getTasks()
                .toBlocking()
                .subscribe(new Subscriber<Task>() {
                    @Override
                    public void onCompleted() {
                        System.out.println(">>> COMPLETED");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(">>> ERROR: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Task task) {
                        System.out.println(">>> TASK: " + task);
                    }
                });
    }

    private static void getProjects() {
        ProjectServiceClient projectServiceClient = ProjectServiceClient.newInstance();
        projectServiceClient.getProjects()
                .toBlocking()
                .subscribe(new Subscriber<Project>() {
                    @Override
                    public void onCompleted() {
                        System.out.println(">>> COMPLETED");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(">>> ERROR: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Project project) {
                        System.out.println(">>> PROJECT: " + project);
                    }
                });
    }

    private static void getPersons() {
        PeopleServiceClient peopleServiceClient = PeopleServiceClient.newInstance();
        peopleServiceClient.getPersons()
                .toBlocking()
                .subscribe(new Subscriber<Person>() {
                    @Override
                    public void onCompleted() {
                        System.out.println(">>> COMPLETED");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(">>> ERROR: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Person person) {
                        System.out.println(">>> PERSON: " + person);
                    }
                });
    }

}

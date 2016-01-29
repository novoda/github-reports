package com.novoda.contributions;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiPeople {

    @SerializedName("people")
    List<ApiPerson> people;

    public static class ApiPerson {

        @SerializedName("people_id")
        String personId;
        @SerializedName("name")
        String name;
        @SerializedName("job_title")
        String jobTitle;

        @Override
        public String toString() {
            return "ApiPerson{" +
                    "personId='" + personId + '\'' +
                    ", name='" + name + '\'' +
                    ", jobTitle='" + jobTitle + '\'' +
                    "}\n";
        }
    }

    @Override
    public String toString() {
        return "ApiPeople{" +
                "people=" + people +
                '}';
    }
}

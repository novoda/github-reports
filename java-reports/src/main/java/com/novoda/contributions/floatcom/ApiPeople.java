package com.novoda.contributions.floatcom;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class ApiPeople {

    @SerializedName("people")
    List<ApiPerson> people;

    public static class ApiPerson {

        @SerializedName("people_id")
        String personId;
        @SerializedName("name")
        String name;
        @SerializedName("job_title")
        String jobTitle;
        @SerializedName("contractor")
        int contractor;

        public String getPersonId() {
            return personId;
        }

        @Override
        public String toString() {
            return "ApiPerson{" +
                    "personId='" + personId + '\'' +
                    ", name='" + name + '\'' +
                    ", jobTitle='" + jobTitle + '\'' +
                    ", contractor=" + contractor +
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
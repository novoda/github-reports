package com.novoda.reports.organisation;

public class OrganisationRepo {

    private final String login;
    private final String name;

    OrganisationRepo(String login, String name) {
        this.login = login;
        this.name = name;
    }

    public String getId() {
        return login + "/" + name;
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public String toString() {
        return "\nOrganisationRepo{" +
                "login='" + login + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OrganisationRepo that = (OrganisationRepo) o;

        return login.equals(that.login) && name.equals(that.name);

    }

    @Override
    public int hashCode() {
        int result = login.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}

package com.novoda.reports.organisation;

import com.almworks.sqlite4java.SQLite;
import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class RepoSqlite3Database {

    static {
        SQLite.setLibraryPath("java-reports/build/libs");
    }

    private static final File DB_FILE = new File("/tmp/database.sqlite3");
    private static final String TBL_REPOS = "github_repos";
    private static final String COL_ORGANISATION = "organisation";
    private static final String COL_LOGIN = "login";
    private static final String COL_NAME = "name";

    public void create() throws SQLiteException {
        SQLiteConnection connection = new SQLiteConnection(DB_FILE);
        try {
            connection.open(true);
            SQLiteStatement createStatement = connection.open()
                    .prepare("CREATE TABLE IF NOT EXISTS '" + TBL_REPOS + "' (" +
                            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            COL_ORGANISATION + " STRING NOT NULL," +
                            COL_LOGIN + " STRING NOT NULL," +
                            COL_NAME + " STRING NOT NULL," +
                            "UNIQUE (" + COL_ORGANISATION + ", " + COL_NAME + ") ON CONFLICT REPLACE" +
                            ");");
            createStatement.step();
            createStatement.dispose();
        } finally {
            connection.dispose();
        }
    }

    public List<OrganisationRepo> read(String organisation) throws SQLiteException {
        SQLiteConnection connection = new SQLiteConnection(DB_FILE);
        connection.open(false);
        SQLiteStatement existsStatement = connection.prepare(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TBL_REPOS + "';");
        if (!existsStatement.step()) {
            return new ArrayList<>();
        }

        SQLiteStatement readStatement = connection.prepare(
                "SELECT " + COL_LOGIN + ", " + COL_NAME + " FROM " + TBL_REPOS + " WHERE " + COL_ORGANISATION + " = ?");
        List<OrganisationRepo> repos = new ArrayList<>();
        try {
            readStatement.bind(1, organisation);
            while (readStatement.step()) {
                String login = readStatement.columnString(0);
                String name = readStatement.columnString(1);
                repos.add(new OrganisationRepo(login, name));
            }
        } finally {
            readStatement.dispose();
            connection.dispose();
        }
        return repos;
    }

    public void update(String organisation, List<OrganisationRepo> organisationRepos) throws SQLiteException {
        SQLiteConnection connection = new SQLiteConnection(DB_FILE);
        try {
            connection.open(false);
            SQLiteStatement updateStatement = connection.prepare(
                    "INSERT INTO '" + TBL_REPOS + "' (" +
                            COL_ORGANISATION + ", " + COL_LOGIN + ", " + COL_NAME +
                            ") " +
                            "VALUES (?, ?, ?)" +
                            ";");
            updateStatement.bind(1, organisation);
            for (OrganisationRepo repo : organisationRepos) {
                updateStatement.bind(2, repo.getLogin());
                updateStatement.bind(3, repo.getName());
                updateStatement.step();
                updateStatement.reset(false);
            }
            updateStatement.dispose();
        } finally {
            connection.dispose();
        }
    }

    public void delete(String organisation) throws SQLiteException {
        SQLiteConnection connection = new SQLiteConnection(DB_FILE);
        try {
            connection.open(false);
            SQLiteStatement deleteStatement = connection.prepare(
                    "DELETE FROM '" + TBL_REPOS + "' " +
                            "WHERE " + COL_ORGANISATION + " = '" + organisation + "'" +
                            ";"
            );
            deleteStatement.step();
            deleteStatement.dispose();
        } finally {
            connection.dispose();
        }
    }
}

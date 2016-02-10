package com.novoda.reports.pullrequest;

import com.almworks.sqlite4java.SQLite;
import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import com.novoda.reports.organisation.OrganisationRepo;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PullRequestSqlite3Database {

    private static final String TBL_PULL_REQUESTS = "github_pull_requests";
    private static final String COL_REPO_NAME = "repo_name";
    private static final String COL_REPO_OWNER_LOGIN = "repo_owner_login";
    private static final String COL_NUMBER = "number";
    private static final String COL_TITLE = "title";
    private static final String COL_USER_LOGIN = "user_login";
    private static final String COL_CREATED_AT = "created_at";
    private static final String TBL_PULL_REQUESTS_EXT = "github_pull_requests_extras";
    private static final String COL_IS_MERGED = "is_merged";
    private static final String COL_MERGED_BY_USER_LOGIN = "merged_by_user_login";

    static {
        SQLite.setLibraryPath("java-reports/build/libs");
    }

    private static final File DB_FILE = new File("/tmp/database.sqlite3");

    public void create() throws SQLiteException {
        SQLiteConnection connection = new SQLiteConnection(DB_FILE);
        try {
            connection.open(true);
            createLite(connection);
            createFull(connection);
        } finally {
            connection.dispose();
        }
    }

    private void createLite(SQLiteConnection connection) throws SQLiteException {
        SQLiteStatement createLiteStatement = connection.open()
                .prepare("CREATE TABLE IF NOT EXISTS '" + TBL_PULL_REQUESTS + "' (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COL_REPO_NAME + " STRING NOT NULL," +
                        COL_REPO_OWNER_LOGIN + " STRING NOT NULL," +
                        COL_NUMBER + " INTEGER NOT NULL," +
                        COL_TITLE + " STRING NOT NULL," +
                        COL_USER_LOGIN + " STRING NOT NULL," +
                        COL_CREATED_AT + " LONG NOT NULL," +
                        "UNIQUE (" + COL_REPO_NAME + ", " + COL_NUMBER + ") ON CONFLICT REPLACE" +
                        ");");
        createLiteStatement.step();
        createLiteStatement.dispose();
    }

    private void createFull(SQLiteConnection connection) throws SQLiteException {
        SQLiteStatement createFullStatement = connection.open()
                .prepare("CREATE TABLE IF NOT EXISTS '" + TBL_PULL_REQUESTS_EXT + "' (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COL_REPO_NAME + " INTEGER NOT NULL," +
                        COL_NUMBER + " INTEGER NOT NULL," +
                        COL_IS_MERGED + " BOOLEAN NOT NULL," +
                        COL_MERGED_BY_USER_LOGIN + " STRING NOT NULL," +
                        "UNIQUE (" + COL_REPO_NAME + ", " + COL_NUMBER + ") ON CONFLICT REPLACE" +
                        ");");
        createFullStatement.step();
        createFullStatement.dispose();
    }

    public List<LitePullRequest> read(OrganisationRepo repo) throws SQLiteException {
        SQLiteConnection connection = new SQLiteConnection(DB_FILE);
        try {
            connection.open(true);

            SQLiteStatement existsStatement = connection.prepare(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TBL_PULL_REQUESTS + "';");
            if (!existsStatement.step()) {
                existsStatement.dispose();
                return new ArrayList<>();
            }
            existsStatement.dispose();

            SQLiteStatement readStatement = connection.prepare(
                    "SELECT " + COL_REPO_NAME +
                            ", " + COL_REPO_OWNER_LOGIN +
                            ", " + COL_NUMBER +
                            ", " + COL_TITLE +
                            ", " + COL_USER_LOGIN +
                            ", " + COL_CREATED_AT +
                            " FROM " + TBL_PULL_REQUESTS +
                            " WHERE " + COL_REPO_NAME + " = ?");
            readStatement.bind(1, repo.getName());
            List<LitePullRequest> litePullRequests = new ArrayList<>();
            while (readStatement.step()) {
                String repoName = readStatement.columnString(0);
                String repoOwnerLogin = readStatement.columnString(1);
                int number = readStatement.columnInt(2);
                String title = readStatement.columnString(3);
                String userLogin = readStatement.columnString(4);
                long rawCreatedAt = readStatement.columnLong(5);
                LocalDate createdAt = LocalDate.ofEpochDay(rawCreatedAt);
                litePullRequests.add(new LitePullRequest(repoName, repoOwnerLogin, number, title, userLogin, createdAt));
            }
            readStatement.dispose();
            return litePullRequests;
        } finally {
            connection.dispose();
        }
    }

    // nullable TODO add annotations
    public FullPullRequest read(LitePullRequest litePullRequest) throws SQLiteException {
        SQLiteConnection connection = new SQLiteConnection(DB_FILE);
        try {
            connection.open(true);

            SQLiteStatement existsStatement = connection.prepare(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TBL_PULL_REQUESTS_EXT + "';");
            if (!existsStatement.step()) {
                existsStatement.dispose();
                return null;
            }
            existsStatement.dispose();

            SQLiteStatement readStatement = connection.prepare(
                    "SELECT " + COL_IS_MERGED + ", "
                            + COL_MERGED_BY_USER_LOGIN +
                            " FROM " + TBL_PULL_REQUESTS_EXT +
                            " WHERE " + COL_REPO_NAME + " = ? " +
                            " AND " + COL_NUMBER + " = ?");
            readStatement.bind(1, litePullRequest.getRepoName());
            readStatement.bind(2, litePullRequest.getNumber());

            if (readStatement.step()) {
                readStatement.dispose();
                boolean isMerged = readStatement.columnInt(0) == 1;
                String mergedByUserName = readStatement.columnString(1);
                return new FullPullRequest(litePullRequest, isMerged, mergedByUserName);
            } else {
                readStatement.dispose();
                return null;
            }
        } finally {
            connection.dispose();
        }
    }

    public void update(OrganisationRepo repo, List<LitePullRequest> litePullRequests) throws SQLiteException {
        SQLiteConnection connection = new SQLiteConnection(DB_FILE);
        try {
            connection.open(false);
            SQLiteStatement updateStatement = connection.prepare(
                    "INSERT INTO '" + TBL_PULL_REQUESTS + "' (" +
                            COL_REPO_NAME + ", " + COL_REPO_OWNER_LOGIN + ", " +
                            COL_NUMBER + ", " + COL_TITLE + ", " + COL_USER_LOGIN + ", " + COL_CREATED_AT +
                            ") " +
                            "VALUES (?, ?, ? ,? ,?, ?)");
            updateStatement.bind(1, repo.getName());
            updateStatement.bind(2, repo.getLogin());
            for (LitePullRequest litePullRequest : litePullRequests) {
                updateStatement.bind(3, litePullRequest.getNumber());
                updateStatement.bind(4, litePullRequest.getTitle());
                updateStatement.bind(5, litePullRequest.getUserLogin());
                updateStatement.bind(6, litePullRequest.getCreatedAt().toEpochDay());
                updateStatement.step();
                updateStatement.reset(false);
            }
            updateStatement.dispose();
        } finally {
            connection.dispose();
        }
    }

    public void update(LitePullRequest litePullRequest, FullPullRequest fullPullRequest) throws SQLiteException {
        SQLiteConnection connection = new SQLiteConnection(DB_FILE);
        try {
            connection.open(false);
            SQLiteStatement updateStatement = connection.prepare(
                    "INSERT INTO '" + TBL_PULL_REQUESTS_EXT + "' (" +
                            COL_REPO_NAME + ", " + COL_NUMBER + ", " +
                            COL_IS_MERGED + ", " + COL_MERGED_BY_USER_LOGIN +
                            ") " +
                            "VALUES (?, ?, ? ,? )");
            updateStatement.bind(1, litePullRequest.getRepoName());
            updateStatement.bind(2, litePullRequest.getNumber());
            updateStatement.bind(3, fullPullRequest.isMerged() ? 1 : 0);
            updateStatement.bind(4, fullPullRequest.getMergedByUserLogin());
            updateStatement.step();
            updateStatement.reset();
            updateStatement.dispose();
        } finally {
            connection.dispose();
        }
    }
}

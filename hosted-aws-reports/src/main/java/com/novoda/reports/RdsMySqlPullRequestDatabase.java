package com.novoda.reports;

import com.novoda.reports.organisation.OrganisationRepo;
import com.novoda.reports.pullrequest.FullPullRequest;
import com.novoda.reports.pullrequest.LitePullRequest;
import com.novoda.reports.pullrequest.PullRequestDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RdsMySqlPullRequestDatabase implements PullRequestDatabase {

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

    private Connection connection;

    @Override
    public void open() throws DatabaseException {
        if (connection != null) {
            return;
        }
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            String protocol = "jdbc:mysql://";
            String endpoint = "ire-mysql-github-reports.cmekjjceogeb.eu-west-1.rds.amazonaws.com";
            String databaseName = "githubreportsdb";
            int port = 3306;
            String url = protocol + endpoint + ":" + port + "/" + databaseName;
            String user = "halreports";
            String password = "novoda951";
            connection = DriverManager.getConnection(url, user, password);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            // Reflection
            throw new IllegalStateException(e);
        } catch (SQLException e) {
            // DB
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void create() throws DatabaseException {
        try {
            createLite();
            createFull();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private void createLite() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(
                "CREATE TABLE IF NOT EXISTS " + TBL_PULL_REQUESTS + " (" +
                        "_id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                        COL_REPO_NAME + " VARCHAR(255) NOT NULL, " +
                        COL_REPO_OWNER_LOGIN + " VARCHAR(255) NOT NULL, " +
                        COL_NUMBER + " INTEGER NOT NULL, " +
                        COL_TITLE + " VARCHAR(255) NOT NULL, " +
                        COL_USER_LOGIN + " VARCHAR(255) NOT NULL, " +
                        COL_CREATED_AT + " LONG NOT NULL, " +
                        "UNIQUE (" + COL_REPO_NAME + ", " + COL_NUMBER + ")" +
                        ");");
        statement.close();
    }

    private void createFull() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(
                "CREATE TABLE IF NOT EXISTS " + TBL_PULL_REQUESTS_EXT + " (" +
                        "_id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                        COL_REPO_NAME + " INTEGER NOT NULL," +
                        COL_NUMBER + " INTEGER NOT NULL," +
                        COL_IS_MERGED + " BOOLEAN NOT NULL," +
                        COL_MERGED_BY_USER_LOGIN + " VARCHAR(255) NOT NULL," +
                        "UNIQUE (" + COL_REPO_NAME + ", " + COL_NUMBER + ")" +
                        ");");
        statement.close();
    }

    @Override
    public List<LitePullRequest> read(OrganisationRepo repo) throws DatabaseException {
        try {
            Statement existsStatement = connection.createStatement();
            boolean hasResult = existsStatement.execute(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TBL_PULL_REQUESTS + "';");
            if (!hasResult) {
                existsStatement.close();
                return new ArrayList<>();
            }
            existsStatement.close();

            Statement readStatement = connection.createStatement();
            ResultSet resultSet = readStatement.executeQuery(
                    "SELECT " + COL_REPO_NAME +
                            ", " + COL_REPO_OWNER_LOGIN +
                            ", " + COL_NUMBER +
                            ", " + COL_TITLE +
                            ", " + COL_USER_LOGIN +
                            ", " + COL_CREATED_AT +
                            " FROM " + TBL_PULL_REQUESTS +
                            " WHERE " + COL_REPO_NAME + " = " + repo.getName());
            readStatement.close();
            List<LitePullRequest> litePullRequests = new ArrayList<>();
            while (resultSet.next()) {
                String repoName = resultSet.getString(0);
                String repoOwnerLogin = resultSet.getString(1);
                int number = resultSet.getInt(2);
                String title = resultSet.getString(3);
                String userLogin = resultSet.getString(4);
                long rawCreatedAt = resultSet.getLong(5);
                LocalDate createdAt = LocalDate.ofEpochDay(rawCreatedAt);
                litePullRequests.add(new LitePullRequest(repoName, repoOwnerLogin, number, title, userLogin, createdAt));
            }
            resultSet.close();
            return litePullRequests;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public FullPullRequest read(LitePullRequest litePullRequest) throws DatabaseException {
        try {
            Statement existsStatement = connection.createStatement();
            boolean hasResult = existsStatement.execute(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TBL_PULL_REQUESTS_EXT + "';");
            if (!hasResult) {
                existsStatement.close();
                return null;
            }
            existsStatement.close();

            Statement readStatement = connection.createStatement();
            ResultSet resultSet = readStatement.executeQuery(
                    "SELECT " + COL_IS_MERGED + ", "
                            + COL_MERGED_BY_USER_LOGIN +
                            " FROM " + TBL_PULL_REQUESTS_EXT +
                            " WHERE " + COL_REPO_NAME + " = " + litePullRequest.getRepoName() +
                            " AND " + COL_NUMBER + " = " + litePullRequest.getNumber());

            readStatement.close();
            if (resultSet.next()) {
                boolean isMerged = resultSet.getInt(0) == 1;
                String mergedByUserName = resultSet.getString(1);
                resultSet.close();
                return new FullPullRequest(litePullRequest, isMerged, mergedByUserName);
            } else {
                resultSet.close();
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void update(OrganisationRepo repo, List<LitePullRequest> litePullRequests) throws DatabaseException {
        try {
            Statement updateStatement = connection.createStatement();

            for (LitePullRequest litePullRequest : litePullRequests) {
                updateStatement.addBatch(
                        "INSERT INTO '" + TBL_PULL_REQUESTS + "' (" +
                                COL_REPO_NAME + ", " + COL_REPO_OWNER_LOGIN + ", " +
                                COL_NUMBER + ", " + COL_TITLE + ", " + COL_USER_LOGIN + ", " + COL_CREATED_AT +
                                ") " +
                                "VALUES ( "
                                + repo.getName() + ", "
                                + repo.getLogin() + ", "
                                + litePullRequest.getNumber() + ", "
                                + litePullRequest.getTitle() + ", "
                                + litePullRequest.getUserLogin() + ", "
                                + litePullRequest.getCreatedAt().toEpochDay()
                                + ") " +
                                "ON DUPLICATE KEY UPDATE "
                                + COL_REPO_NAME + "=VALUES(" + COL_REPO_NAME + "), "
                                + COL_REPO_OWNER_LOGIN + "=VALUES(" + COL_REPO_OWNER_LOGIN + "), "
                                + COL_NUMBER + "=VALUES(" + COL_NUMBER + "), "
                                + COL_TITLE + "=VALUES(" + COL_TITLE + "), "
                                + COL_USER_LOGIN + "=VALUES(" + COL_USER_LOGIN + "), "
                                + COL_CREATED_AT + "=VALUES(" + COL_CREATED_AT + ")");
            }
            updateStatement.executeBatch();
            updateStatement.close();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void update(LitePullRequest litePullRequest, FullPullRequest fullPullRequest) throws DatabaseException {
        try {
            Statement updateStatement = connection.createStatement();
            updateStatement.execute(
                    "INSERT INTO '" + TBL_PULL_REQUESTS_EXT + "' (" +
                            COL_REPO_NAME + ", " + COL_NUMBER + ", " +
                            COL_IS_MERGED + ", " + COL_MERGED_BY_USER_LOGIN +
                            ") " +
                            "VALUES ("
                            + litePullRequest.getRepoName() + ", "
                            + litePullRequest.getNumber() + ", "
                            + (fullPullRequest.isMerged() ? 1 : 0) + ", "
                            + fullPullRequest.getMergedByUserLogin() + ")"
                            + "ON DUPLICATE KEY UPDATE "
                            + COL_REPO_NAME + "=VALUES(" + COL_REPO_NAME + "),"
                            + COL_NUMBER + "=VALUES(" + COL_NUMBER + "),"
                            + COL_IS_MERGED + "=VALUES(" + COL_IS_MERGED + "),"
                            + COL_MERGED_BY_USER_LOGIN + "=VALUES(" + COL_MERGED_BY_USER_LOGIN + "),"
            );
            updateStatement.close();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void close() throws DatabaseException {
        try {
            connection.close();
            connection = null;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}

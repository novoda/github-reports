package com.novoda.reports.pullrequest.comment;

import com.almworks.sqlite4java.SQLite;
import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import com.novoda.reports.pullrequest.LitePullRequest;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommentSqlite3Database {

    static {
        SQLite.setLibraryPath("java-reports/build/libs");
    }

    private static final File DB_FILE = new File("/tmp/database.sqlite3");

    private static final String TBL_COMMENTS = "pull_request_comments";
    private static final String COL_REPO_NAME = "repo_name";
    private static final String COL_PR_NUMBER = "pull_request_number";
    private static final String COL_USER_LOGIN = "user_login";
    private static final String COL_CREATED_AT = "created_at";

    public void create() throws SQLiteException {
        SQLiteConnection connection = new SQLiteConnection(DB_FILE);
        try {
            connection.open(true);
            SQLiteStatement createStatement = connection.open()
                    .prepare("CREATE TABLE IF NOT EXISTS '" + TBL_COMMENTS + "' (" +
                            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            COL_REPO_NAME + " STRING NOT NULL," +
                            COL_PR_NUMBER + " INTEGER NOT NULL," +
                            COL_USER_LOGIN + " STRING NOT NULL," +
                            COL_CREATED_AT + " STRING NOT NULL," +
                            "UNIQUE (" + COL_REPO_NAME + ", " + COL_PR_NUMBER + ") ON CONFLICT REPLACE" +
                            ");");
            createStatement.step();
            createStatement.dispose();
        } finally {
            connection.dispose();
        }
    }

    public List<Comment> read(LitePullRequest pullRequest) throws SQLiteException {
        SQLiteConnection connection = new SQLiteConnection(DB_FILE);
        try {
            connection.open(true);

            SQLiteStatement existsStatement = connection.prepare(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TBL_COMMENTS + "';");
            if (!existsStatement.step()) {
                existsStatement.dispose();
                return Collections.emptyList();
            }
            existsStatement.dispose();

            SQLiteStatement readStatement = connection.prepare(
                    "SELECT " + COL_REPO_NAME +
                            ", " + COL_USER_LOGIN +
                            ", " + COL_CREATED_AT +
                            " FROM " + TBL_COMMENTS +
                            " WHERE " + COL_REPO_NAME + " = ?" +
                            " AND " + COL_PR_NUMBER + " = ?");
            readStatement.bind(1, pullRequest.getRepoName());
            readStatement.bind(2, pullRequest.getNumber());
            List<Comment> comments = new ArrayList<>();
            while (readStatement.step()) {
                String userLogin = readStatement.columnString(0);
                long rawCreatedAt = readStatement.columnLong(1);
                LocalDate createdAt = LocalDate.ofEpochDay(rawCreatedAt);
                comments.add(new Comment(userLogin, createdAt));
            }
            readStatement.dispose();
            return comments;
        } finally {
            connection.dispose();
        }
    }

    public void update(LitePullRequest pullRequest, List<Comment> comments) throws SQLiteException {
        SQLiteConnection connection = new SQLiteConnection(DB_FILE);
        try {
            connection.open(false);
            SQLiteStatement updateStatement = connection.prepare(
                    "INSERT INTO '" + TBL_COMMENTS + "' (" +
                            COL_REPO_NAME + ", " + COL_PR_NUMBER + ", " +
                            COL_USER_LOGIN + ", " + COL_CREATED_AT +
                            ") " +
                            "VALUES (?, ?, ? ,?)");
            updateStatement.bind(1, pullRequest.getRepoName());
            updateStatement.bind(2, pullRequest.getNumber());
            for (Comment comment : comments) {
                updateStatement.bind(3, comment.getUserLogin());
                updateStatement.bind(4, comment.getCreatedAt().toEpochDay());
                updateStatement.step();
                updateStatement.reset(false);
            }
            updateStatement.dispose();
        } finally {
            connection.dispose();
        }
    }
}

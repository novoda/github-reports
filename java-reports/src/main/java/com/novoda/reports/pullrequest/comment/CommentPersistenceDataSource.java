package com.novoda.reports.pullrequest.comment;

import com.almworks.sqlite4java.SQLiteException;
import com.novoda.reports.pullrequest.LitePullRequest;

import java.util.List;

class CommentPersistenceDataSource {

    private final CommentSqlite3Database commentDatabase;

    CommentPersistenceDataSource(CommentSqlite3Database commentDatabase) {
        this.commentDatabase = commentDatabase;
    }

    public void createComments(LitePullRequest pullRequest, List<Comment> comments) {
        try {
            commentDatabase.open();
            commentDatabase.create();
            commentDatabase.update(pullRequest, comments);
        } catch (SQLiteException e) {
            throw new IllegalStateException("Could not save comments for " + pullRequest.getTitle() + " to repository.", e);
        } finally {
            commentDatabase.close();
        }
    }

    public List<Comment> readComments(LitePullRequest pullRequest) {
        try {
            commentDatabase.open();
            return commentDatabase.read(pullRequest);
        } catch (SQLiteException e) {
            throw new IllegalStateException("Could not read comments for " + pullRequest.getTitle() + " from repository.", e);
        } finally {
            commentDatabase.close();
        }
    }
}

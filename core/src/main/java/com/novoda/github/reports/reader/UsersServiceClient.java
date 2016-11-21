package com.novoda.github.reports.reader;

import java.io.IOException;

import rx.Observable;

public class UsersServiceClient {

    // FIXME we're leaking float into core

    private final UsersReader usersReader;

    public static UsersServiceClient newInstance() {
        return new UsersServiceClient(UsersReader.newInstance());
    }

    private UsersServiceClient(UsersReader usersReader) {
        this.usersReader = usersReader;
    }

    public Observable<String> getAllGithubUsers() {
        try {
            readIfNeeded();
            return Observable.from(usersReader.getContent().values());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Observable.empty();
    }

    private void readIfNeeded() throws IOException {
        if (usersReader.hasContent()) {
            return;
        }
        usersReader.read();
    }
}

package com.novoda.handler;

import com.novoda.command.UserOptions;
import com.novoda.core.data.UserDataLayer;
import com.novoda.core.stats.UserStats;

public class UserCommandHandler implements CommandHandler<UserStats, UserOptions> {
    private final UserDataLayer dataLayer;

    public UserCommandHandler(UserDataLayer dataLayer) {
        this.dataLayer = dataLayer;
    }

    @Override
    public UserStats handle(UserOptions options) {
        return dataLayer.getStats(options.getUser(), options.getRepository(), options.getFrom(), options.getTo());
    }
}

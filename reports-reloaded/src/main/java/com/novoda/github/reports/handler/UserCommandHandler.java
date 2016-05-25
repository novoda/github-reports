package com.novoda.github.reports.handler;

import com.novoda.github.reports.command.UserOptions;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.model.UserStats;

public class UserCommandHandler implements CommandHandler<UserStats, UserOptions> {
    private final UserDataLayer dataLayer;

    public UserCommandHandler(UserDataLayer dataLayer) {
        this.dataLayer = dataLayer;
    }

    @Override
    public UserStats handle(UserOptions options) {
        try {
            return dataLayer.getStats(options.getUser(), options.getRepository(), options.getFrom(), options.getTo());
        } catch (DataLayerException e) {
            e.printStackTrace();
        }
        return null;
    }
}

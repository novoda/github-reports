package com.novoda.github.reports.batch.logger;

import com.novoda.github.reports.util.StringHelper;

import static com.novoda.github.reports.batch.logger.Logger.Level.*;

public interface Logger {

    default void log(Level level, String template, Object... args) {
        String message = String.format(template, args);
        log(level, message);
    }

    default void error(Throwable t) {
        log(ERROR, t.getMessage() + "\n" + t.toString());
    }

    default void error(String template, Object... args) {
        log(ERROR, template, args);
    }

    default void warn(String template, Object... args) {
        log(WARNING, template, args);
    }

    default void info(String template, Object... args) {
        log(INFO, template, args);
    }

    default void debug(String template, Object... args) {
        log(DEBUG, template, args);
    }

    void log(Level level, String message);

    void setMinimumLevel(Level level);

    void logAll();

    void logNone();

    enum Level {
        DEBUG("DEBUG"),
        INFO("INFO"),
        WARNING("WARNING", 0x26A0),
        ERROR("ERROR", 0x274C);

        private final String levelName;
        private final String emojiCode;

        Level(String levelName) {
            this.levelName = levelName;
            this.emojiCode = null;
        }

        Level(String levelName, int emojiCode) {
            this.levelName = levelName;
            this.emojiCode = StringHelper.emojiToString(emojiCode);
        }

        @Override
        public String toString() {
            return wrapInBrackets(levelName) + getEmojiString();
        }

        private String wrapInBrackets(String wrapThis) {
            return "[ " + wrapThis + " ]";
        }

        private String getEmojiString() {
            return emojiCode != null ? " " + emojiCode + " " : "";
        }
    }

}

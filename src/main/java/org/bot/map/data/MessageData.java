package org.bot.map.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class MessageData {
    public static final String DEFAULT_TITLE = "Default title";

    private String command;
    private String timeInterval;
    private String date;
    private String title = DEFAULT_TITLE;
    private String description;

    public boolean hasCommand() {
        return command != null;
    }

    public boolean hasTimeInterval() {
        return timeInterval != null;
    }

    public boolean hasDate() {
        return date != null;
    }

    public boolean hasDefaultTitle() {
        return title.equals(DEFAULT_TITLE);
    }

    public boolean hasDescription() {
        return description != null;
    }

    public void setTimeInterval(String timeInterval) {
        String[] times = timeInterval.split("-");
        if (times[0].compareTo(times[1]) <= 0) {
            this.timeInterval = timeInterval;
        } else {
            throw new Error("invalid time interval");
        }
    }
}

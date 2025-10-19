package org.bot.map.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@Getter
@Setter
public class MessageData implements Comparable<MessageData> {
    public static final String DEFAULT_TITLE = "Default title";

    private String command;
    private String timeInterval;
    private String date;
    @NotNull
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

    @Override
    public int compareTo(@NotNull MessageData o) {
        int dateRes = date.compareTo(o.getDate());
        if (dateRes == 0) {
            if (hasTimeInterval() ^ o.hasTimeInterval()) {
                if (hasTimeInterval()) {
                    return 1;
                }
                return -1;
            } else if (hasTimeInterval()) {
                int intervalRes = timeInterval.compareTo(o.getTimeInterval());
                if (intervalRes == 0) {
                    return compareTitleAndDescription(o);
                }
                return intervalRes < 0 ? -1 : 1;
            }
            return compareTitleAndDescription(o);
        }
        return dateRes < 0 ? -1 : 1;
    }

    private int compareTitleAndDescription(MessageData o) {
        int titleRes = title.compareTo(o.getTitle());
        if (titleRes == 0) {
            if (hasDescription() ^ o.hasDescription()) {
                if (hasDescription()) {
                    return 1;
                }
                return  -1;
            }
            if (hasDescription()) {
                int descriptionRes = description.compareTo(o.getDescription());
                if (descriptionRes != 0) {
                    return descriptionRes < 0 ? -1 : 1;
                }
            }
            return 0;
        }
        return titleRes < 0 ? -1 : 1;
    }

    @Override
    public String toString() {
        if (hasCommand()) {
            return command;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (hasDate()) {
            stringBuilder.append(date).append("\n");
        }
        if (hasTimeInterval()) {
            stringBuilder.append(timeInterval).append("\n");
        }
        stringBuilder.append(title).append("\n");
        if (hasDescription()) {
            stringBuilder.append(description);
        }

        return stringBuilder.toString();
    }
}

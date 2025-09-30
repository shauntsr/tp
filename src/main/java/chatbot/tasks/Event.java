package chatbot.tasks;

import java.time.LocalDateTime;

public class Event extends Deadline {
    protected String from;
    protected LocalDateTime fromDateTime;

    public LocalDateTime getFromDateTime() {
        return fromDateTime;
    }

    public void setFromDateTime(LocalDateTime fromDateTime) {
        this.fromDateTime = fromDateTime;
    }


    public Event(String name, String from, String to) {
        super(name, to);
        this.from = from;
        this.fromDateTime = parseDate(from);
    }

    public Event(String name, boolean isDone, String from, String to) {
        super(name, isDone, to);
        this.from = from;
        this.fromDateTime = parseDate(from);
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
        this.fromDateTime = parseDate(from);
    }

    public String getTo() {
        return by;
    }

    public void setTo(String to) {
        this.by = to;
        this.byDateTime = parseDate(to);
    }

    @Override
    public String toString() {
        String fromDisplay = (fromDateTime != null)
                ? fromDateTime.format(OUTPUT_FORMAT)
                : from;
        String toDisplay = (byDateTime != null)
                ? byDateTime.format(OUTPUT_FORMAT)
                : by;
        return "[E]" + getBaseString() + " (from: " + fromDisplay + " to: " + toDisplay + ")";
    }

    @Override
    public String toSaveFormat() {
        return "E | " + getSafeString() + " | " + from + " /to " + by;
    }
}
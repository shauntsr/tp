public class Event extends Deadline {
    protected String from;

    public Event(String name, String from, String to) {
        super(name, to);
        this.from = from;
        this.by = to;
    }

    public Event(String name, boolean isDone, String from, String to) {
        super(name,to);
        this.from = from;
        this.by = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return by;
    }

    public void setTo(String to) {
        this.by = to;
    }

    @Override
    public String toString() {
        return "[E]" + getBaseString() + " (from: " + from + " to: " + by + ")";
    }
}
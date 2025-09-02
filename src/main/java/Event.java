public class Event extends Task {
    protected String from;
    protected String to;

    public Event(String name, String from, String to) {
        super(name, false);
        this.from = from;
        this.to = to;
    }

    public Event(String name, boolean isDone, String from, String to) {
        super(name, isDone);
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
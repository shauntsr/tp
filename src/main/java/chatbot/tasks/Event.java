package chatbot.tasks;

import java.time.LocalDateTime;

/**
 * Represents an event task with a start and end time.
 * An Event is a task that occurs during a specific time period,
 * defined by a start time (from) and an end time (to).
 * Extends Deadline to reuse date parsing functionality.
 */
public class Event extends Deadline {
    protected String from;
    protected LocalDateTime fromDateTime;

    /**
     * Returns the start time as a LocalDateTime object.
     *
     * @return The start date and time, or null if parsing failed.
     */
    public LocalDateTime getFromDateTime() {
        return fromDateTime;
    }

    /**
     * Sets the start time as a LocalDateTime object.
     *
     * @param fromDateTime The start date and time.
     */
    public void setFromDateTime(LocalDateTime fromDateTime) {
        this.fromDateTime = fromDateTime;
    }

    /**
     * Constructs an Event task with the specified name, start time, and end time.
     * The task is initially marked as not done.
     *
     * @param name The name of the event task.
     * @param from The start time string in one of the supported formats.
     * @param to   The end time string in one of the supported formats.
     */
    public Event(String name, String from, String to) {
        super(name, to);
        this.from = from;
        this.fromDateTime = parseDate(from);
    }

    /**
     * Constructs an Event task with the specified name, mark status, start time, and end time.
     *
     * @param name   The name of the event task.
     * @param isDone True if the task is done, false otherwise.
     * @param from   The start time string in one of the supported formats.
     * @param to     The end time string in one of the supported formats.
     */
    public Event(String name, boolean isDone, String from, String to) {
        super(name, isDone, to);
        this.from = from;
        this.fromDateTime = parseDate(from);
    }

    /**
     * Returns the original start time string as entered by the user.
     *
     * @return The start time string.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets the start time string and parses it into a LocalDateTime.
     *
     * @param from The new start time string in one of the supported formats.
     */
    public void setFrom(String from) {
        this.from = from;
        this.fromDateTime = parseDate(from);
    }

    /**
     * Returns the original end time string as entered by the user.
     *
     * @return The end time string.
     */
    public String getTo() {
        return by;
    }

    /**
     * Sets the end time string and parses it into a LocalDateTime.
     *
     * @param to The new end time string in one of the supported formats.
     */
    public void setTo(String to) {
        this.by = to;
        this.byDateTime = parseDate(to);
    }

    /**
     * Returns a string representation of the event task for display.
     * If dates cannot be parsed, displays the original time strings.
     *
     * @return The string representation of the event task.
     */
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

    /**
     * Returns a string representation of the event task for saving to file.
     *
     * @return The save format string representation of the event task.
     */
    @Override
    public String toSaveFormat() {
        return "E | " + getSaveString() + " | " + from + " /to " + by;
    }
}
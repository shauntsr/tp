package seedu.duke.tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a task with a deadline.
 * Deadline is a task that needs to be completed by a specific date and time.
 * The deadline can be specified in multiple date/time formats and is stored as a LocalDateTime if specified time fits format.
 */
public class Deadline extends ToDo {
    protected String by;
    protected LocalDateTime byDateTime;

    /**
     * Returns the deadline as a LocalDateTime object.
     *
     * @return The deadline date and time, or null if parsing failed.
     */
    public LocalDateTime getByDateTime() {
        return byDateTime;
    }

    /**
     * Sets the deadline as a LocalDateTime object.
     *
     * @param byDateTime The deadline date and time.
     */
    public void setByDateTime(LocalDateTime byDateTime) {
        this.byDateTime = byDateTime;
    }

    protected static final DateTimeFormatter[] INPUT_FORMATS = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm"),
            DateTimeFormatter.ofPattern("d/M/yyyy H:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("d/M/yyyy")
    };
    protected static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy, h:mma");

    /**
     * Constructs a Deadline task with the specified name and deadline.
     * The task is initially marked as not done.
     *
     * @param name The name of the deadline task.
     * @param by   The deadline string in one of the supported formats.
     */
    public Deadline(String name, String by) {
        super(name, false);
        this.by = by;
        this.byDateTime = parseDate(by);
    }

    /**
     * Constructs a Deadline task with the specified name, mark status, and deadline by.
     *
     * @param name   The name of the deadline task.
     * @param isDone True if the task is done, false otherwise.
     * @param by     The deadline string in one of the supported formats.
     */
    public Deadline(String name, boolean isDone, String by) {
        super(name, isDone);
        this.by = by;
        this.byDateTime = parseDate(by);
    }

    /**
     * Returns the original deadline string as entered by the user.
     *
     * @return The deadline string.
     */
    public String getBy() {
        return by;
    }

    /**
     * Sets the deadline string and parses it into a LocalDateTime.
     *
     * @param by The new deadline string in one of the supported formats.
     */
    public void setBy(String by) {
        this.by = by;
        this.byDateTime = parseDate(by);
    }

    /**
     * Parses a date string in multiple supported formats.
     * Supported formats include: yyyy-MM-dd HHmm, d/M/yyyy HHmm, d/M/yyyy H:mm,
     * yyyy-MM-dd, and d/M/yyyy. Date-only formats are converted to start of day.
     *
     * @param input The date string to parse.
     * @return The parsed LocalDateTime object, or null if no format matches.
     */
    protected LocalDateTime parseDate(String input) {
        for (DateTimeFormatter fmt : INPUT_FORMATS) {
            try {
                if (fmt.toString().contains("H")) {
                    return LocalDateTime.parse(input, fmt);
                } else {
                    return java.time.LocalDate.parse(input, fmt).atStartOfDay();
                }
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }

    /**
     * Returns a string representation of the deadline task for display.
     * If the date cannot be parsed, displays the original deadline string.
     *
     * @return The string representation of the deadline task.
     */
    @Override
    public String toString() {
        String display = (byDateTime != null)
                ? byDateTime.format(OUTPUT_FORMAT)
                : by;
        return "[D]" + getBaseString() + " (by: " + display + ")";
    }

    /**
     * Returns a string representation of the deadline task for saving to file.
     *
     * @return The save format string representation of the deadline task.
     */
    public String toSaveFormat() {
        return "D | " + getSaveString() + " | " + by;
    }
}

package chatbot.tasks;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Deadline extends ToDo {
    protected String by;
    protected LocalDateTime byDateTime;

    public LocalDateTime getByDateTime() {
        return byDateTime;
    }

    public void setByDateTime(LocalDateTime byDateTime) {
        this.byDateTime = byDateTime;
    }

    protected static final DateTimeFormatter[] INPUT_FORMATS = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm"),
            DateTimeFormatter.ofPattern("d/M/yyyy H:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),   // date only
            DateTimeFormatter.ofPattern("d/M/yyyy")      // date only
    };
    protected static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy, h:mma");

    public Deadline(String name, String by) {
        super(name,  false);
        this.by = by;
        this.byDateTime = parseDate(by);
    }

    public Deadline(String name, boolean isDone, String by) {
        super(name, false);
        this.by = by;
        this.byDateTime = parseDate(by);
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
        this.byDateTime = parseDate(by);
    }

    protected LocalDateTime parseDate(String input) {
        for (DateTimeFormatter fmt : INPUT_FORMATS) {
            try {
                if (fmt.toString().contains("H")) {
                    return LocalDateTime.parse(input, fmt);
                } else {
                    return java.time.LocalDate.parse(input, fmt).atStartOfDay();
                }
            } catch (DateTimeParseException ignored) {}
        }
        return null;
    }

    @Override
    public String toString() {
        String display = (byDateTime != null)
                ? byDateTime.format(OUTPUT_FORMAT)
                : by;
        return "[D]" + getBaseString() + " (by: " + display + ")";
    }
    public String toSaveFormat() {
        return "D | " + getSafeString() + " | " + by;
    }
}

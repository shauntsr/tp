package chatbot.commands;

import chatbot.Storage;
import chatbot.UI;
import chatbot.exceptions.CoachException;
import chatbot.tasks.Deadline;
import chatbot.tasks.Event;
import chatbot.tasks.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Command to list all tasks occurring on a specific date.
 * ListDateCommand filters and displays tasks that match the given date,
 * including deadlines on that date and events that fall within or on that date.
 */
public class ListDateCommand extends Command {
    private final LocalDate date;

    /**
     * Constructs a ListDateCommand with the specified date.
     *
     * @param date The date to filter tasks by.
     */
    public ListDateCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Executes the list date command by filtering tasks that occur on the specified date.
     * For Event tasks, checks if the date falls within the event's time range
     * If one of the event times cannot be parsed, checks if the other time matches.
     * For Deadline tasks, checks if the deadline date matches the specified date.
     * ToDo tasks omitted as they have no time
     *
     * @param tasks The list of tasks to filter.
     * @param ui    The UI object for displaying the filtered tasks.
     * @throws CoachException If an error occurs during execution.
     */
    @Override
    public void execute(ArrayList<Task> tasks, UI ui, Storage storage) throws CoachException {
        List<Task> filtered = tasks.stream()
                .filter(task -> {
                    if (task instanceof Event e) {
                        LocalDate start = e.getFromDateTime() != null ? e.getFromDateTime().toLocalDate() : null;
                        LocalDate end = e.getByDateTime() != null ? e.getByDateTime().toLocalDate() : null;

                        // Case 1: Both times is valid LocalDate
                        if (start != null && end != null) {
                            // Check if date falls within range
                            if (!start.isAfter(end)) {
                                return !date.isBefore(start) && !date.isAfter(end);
                            }
                            // If range is reverse, from is after to, check if either times match given date
                            return date.equals(start) || date.equals(end);
                        }
                        // Only start date is valid, check if matches
                        if (start != null) {
                            return start.equals(date);
                        }
                        // Only end date is valid, check if matches
                        if (end != null) {
                            return end.equals(date);
                        }
                        // Neither date is valid, do not include
                        return false;
                    } else if (task instanceof Deadline d && d.getByDateTime() != null) {
                        return d.getByDateTime().toLocalDate().equals(date);
                    }
                    return false;
                })
                .toList();

        ui.showTasksOnDate(date, filtered);
    }
}

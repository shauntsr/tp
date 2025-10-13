package seedu.duke.commands;

import seedu.duke.Storage;
import seedu.duke.UI;
import seedu.duke.exceptions.CoachException;
import seedu.duke.tasks.Deadline;
import seedu.duke.tasks.Event;
import seedu.duke.tasks.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ListDateCommand extends Command {
    private final LocalDate date;

    public ListDateCommand(LocalDate date) {
        this.date = date;
    }

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

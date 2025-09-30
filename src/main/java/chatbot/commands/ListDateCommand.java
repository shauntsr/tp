package chatbot.commands;

import chatbot.Storage;
import chatbot.UI;
import chatbot.exceptions.CoachException;
import chatbot.tasks.Deadline;
import chatbot.tasks.Event;
import chatbot.tasks.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ListDateCommand extends Command {
    private final LocalDate date;

    // Constructor now takes parsed LocalDate
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

                        if (start != null && end != null) {
                            return !date.isBefore(start) && !date.isAfter(end);
                        }
                        if (start != null) {
                            return start.equals(date);
                        }
                        if (end != null) {
                            return end.equals(date);
                        }
                    } else if (task instanceof Deadline d && d.getByDateTime() != null) {
                        return d.getByDateTime().toLocalDate().equals(date);
                    }
                    return false;
                })
                .toList();

        ui.showTasksOnDate(date, filtered);
    }
}

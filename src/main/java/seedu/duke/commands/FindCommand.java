package seedu.duke.commands;

import seedu.duke.Storage;
import seedu.duke.UI;
import seedu.duke.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FindCommand extends Command {
    private final String keyword;

    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(ArrayList<Task> tasks, UI ui, Storage storage) {
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());

        ui.showFinds(keyword, matchingTasks);
    }
}

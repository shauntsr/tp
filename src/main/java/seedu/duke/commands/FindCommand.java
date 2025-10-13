package seedu.duke.commands;

import seedu.duke.Storage;
import seedu.duke.UI;
import seedu.duke.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command to search for tasks containing a specific keyword.
 * FindCommand filters the task list by matching task names against the provided keyword and displays the matching results.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Constructs a FindCommand with the specified search keyword.
     *
     * @param keyword The keyword to search for in task names.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Executes the find command by filtering tasks that contain the keyword in their names and displaying the matching tasks.
     * The search matches partial strings and is not case-sensitive.
     *
     * @param tasks The list of tasks to search through.
     */
    @Override
    public void execute(ArrayList<Task> tasks, UI ui, Storage storage) {
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());

        ui.showFinds(keyword, matchingTasks);
    }
}

package chatbot.commands;

import chatbot.Storage;
import chatbot.UI;
import chatbot.tasks.Task;

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
                .filter(task -> task.getName().contains(keyword))
                .collect(Collectors.toList());

        ui.showFinds(keyword, matchingTasks);
    }

}

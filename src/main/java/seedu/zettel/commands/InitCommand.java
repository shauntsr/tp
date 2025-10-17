package seedu.zettel.commands;

import java.util.ArrayList;

import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;

public class InitCommand extends Command {

    private final String repoName;

    public InitCommand(String repoName) {
        assert repoName != null: "Repo name should not be null." ;
        this.repoName = repoName;
    }

    @Override
    public void execute(ArrayList<Note> notes, UI ui, Storage storage) throws ZettelException {
        storage.createRepo(repoName);
        ui.showRepoInit(repoName);
    }
}

# ZettelCLI User Guide

## Introduction

ZettelCLI is a desktop Command Line Interface (CLI) application used for managing a personal Zettlekasten note system. It is optimised for users who prefer keyboard commands over Graphical User Interfaces (GUI). Through just a keyboard, users can build their own repository of notes or quickly navigate through their knowledge base with ease.


## Quick Start
1. Ensure that you have Java 17 or above installed. 
2. Download the latest version of `Duke` from [here](http://link.to/duke).
3. Run the application
```bash
java -jar zettel.jar
```

## Features 

### Starting a Zettelkasten Repository: `init`

Initialises a new Zettelkasten repository.

**Format:**
```
init <repository-name>
```

**Example:**
```
init My_New_Repo
```

**Expected Output:**
```
 Repository /My_New_Repo has been created.
```

---

### Changing Repository: `change-repo`

Switches to another existing repository. The repository must have been previously created using the `init` command.
If we are already in the repository that we are changing to, no change occurs.

**Format:**
```
change-repo <repository-name>
```
or
```
change-repository <repository-name>
```

**Example:**
```
change-repo My_New_Repo
```


**Expected Output:**
```
 Successfully changed to repository: /My_New_Repo
```

**Example (already in repository):**
```
change-repo main
```

**Expected Output:**
```
 Already on main!
```

---

### Adding a New Note: `new`

Creates a new note in the Zettelkasten repository. Notes can be created with only a title, or with both a title and a 
body. The ID generated is randomised.

**Format:**
```
new -t <TITLE> [-b <BODY>]
```

**Example:**
```
new -t New_Note -b "This is a new note"
```

**Expected Output:**
```
Note created: New_Note.txt #e0e7b989
```

---

### Listing Notes (with filters): `list`

Lists notes with precise filters for pinned and archived states.

• `-p` shows only pinned notes.  
• `-a` shows only archived notes.  
You can combine flags in any order; behavior is defined by this matrix:

- `list` → pinned = X, archived = 0 (all unarchived notes)
- `list -a` → pinned = X, archived = 1 (all archived notes)
- `list -p` → pinned = 1, archived = 0 (pinned and unarchived)
- `list -p -a` → pinned = 1, archived = 1 (pinned and archived)

**Format:**
```
list [-p] [-a]
```

#### To view unarchived notes (pinned and unpinned)
Use the command without flags.

**Example:**
```
list
```

**Expected Output:**
```
You have 3 notes:
    1. New_Note.txt 2025-10-17 e0e7b989
    2. brrr_againi.txt 2025-10-16 ccfd2e51
    3. file.txt 2025-10-16 55bb2cac
```

#### To view archived notes (pinned and unpinned)
Use the `-a` flag.

**Example:**
```
list -a
```

**Expected Output:**
```
You have 2 notes (archived):
    1. Archived_Note.txt 2025-10-10 a1b2c3d4
    2. Old_Research.txt 2025-10-08 a9f0e1d2
```

#### To view pinned notes that are archived only
Use both `-p` and `-a` (in any order).

**Example:**
```
list -a -p
```

**Expected Output:**
```
You have 1 pinned archived notes:
    1. Archived_Note.txt 2025-10-10 a1b2c3d4
```

#### To view pinned notes that are unarchived only
Use the `-p` flag without `-a`.

**Example:**
```
list -p
```

**Expected Output:**
```
You have 1 pinned notes:
    1. file.txt 2025-10-16 55bb2cac
```
---

### Removing a Note: `delete`

Removes the note at the specified index. Confirmation prevents accidental loss.

**Format:**
```
delete [-f] <NOTE_ID>
```

#### Delete with confirmation
To delete with confirmation, use the command with no flags.

**Example:**

```
delete e0e7b989 
```

**Expected Output:**

```
Confirm deletion on 'New_Note', noteID e0e7b989? (y/n)
> y
Note at e0e7b989 deleted
```

#### Delete without confirmation
Use the command with the `-f` flag.

**Example:**
```
delete -f 55bb2cac
```

**Expected Output:**
```
Note at 55bb2cac deleted
```
---

### Pinning a Note: `pin`

Pins a note in the repository to keep it at the top of your list.

**Format:**

```
pin <NOTE_ID>
```

**Example:**

```
pin ccfd2e51
```

**Expected Output:**

```
Got it. I've pinned this note: ccfd2e51
```

### Unpinning a Note: `unpin`

Unpins a note in the repository.

**Format:**

```
unpin <NOTE_ID>
```

**Example:**

```
unpin ccfd2e51
```

**Expected Output:**

```
Got it. I've unpinned this note: ccfd2e51
```

### Viewing All Commands: `help`

Displays a complete list of available commands and their descriptions.

**Format:**

```
help
```

**Expected Output:**s

```
Available Commands:
    init <repo-name>                - Initialize a new repository
    new -t <title> [-b <body>]      - Create a new note
    edit <note-id>                  - Edit an existing note
    list [-p] [-a]                  - List notes (pinned and/or archived filters)
    delete [-f] <note-id>           - Delete a note by ID
    pin <note-id>                   - Pin a note
    unpin <note-id>                 - Unpin a note
    new-tag <tag-name>              - Adds a tag
    add-tag <note-id> <tag-name>    - Tag a note
    link <source-id> <target-id>    - Link two notes
    unlink <source-id> <target-id>  - Unlink two notes
    link-both <id1> <id2>           - Link two notes in both directions
    unlink-both <id1> <id2>         - Unlink two notes in both directions
    list-incoming-links <note-id>   - Show incoming linked notes
    list-outgoing-links <note-id>   - Show outgoing linked notes
    list-tags-all                   - Lists all tags that exist globally
    list-tags <note-id>             - List tags for an single note
    delete-tag [-f] <note-id> <tag> - Delete a tag from a note
    delete-tag-globally [-f] <tag>  - Delete a tag from all notes
    rename-tag <old-tag> <new-tag>  - Rename a tag globally
    archive <note-id>               - Moves note to archive folder
    unarchive <note-id>             - Moves note out of archive folder
    print-body <note-id>            - Print the body of a note
    find <text>                     - Search for notes
    help                             - Show this list of commands
    bye                             - Exit the application
```

## Command Summary
* Start a new Zettelkasten repository: `init <repository-name>`
* Add a new note: `new -t <TITLE> [-b <BODY>]`
* List unarchived notes: `list`
* List archived notes: `list -a`
* List only pinned (unarchived): `list -p`
* List only pinned (archived): `list -a -p`
* Delete a note (with confirmation): `delete <NOTE_ID>`
* Delete a note (without confirmation): `delete -f <NOTE_ID>`
* Pin a note: `pin <NOTE_ID>`
* Unpin a note: `unpin <NOTE_ID>`
* View all commands: `help`
* Print a note's body: `print-body <NOTE_ID>`

---

### Printing a Note Body: `print-body`

Prints the full body of a note by its ID. Note ID must be 8 lowercase hexadecimal characters.

**Format:**
```
print-body <NOTE_ID>
```

**Example:**
```
print-body abcd1234
```

**Expected Output:**
```
 Body of note #abcd1234:
 Hello World
```

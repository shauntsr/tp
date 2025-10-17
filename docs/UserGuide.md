# ZettelCLI User Guide

## Introduction

ZettelCLI is a desktop Command Line Interface (CLI) application used for managing a personal Zettlekasten note system. It is optimised for users who prefer keyboard commands over Graphical User Interfaces (GUI). Through just a keyboard, users can build their own repository of notes or quickly navigate through their knowledge base with ease.


## Quick Start
1. Ensure that you have Java 17 or above installed. 
2. Down the latest version of `Duke` from [here](http://link.to/duke).
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

### Listing All Notes: `list`

Lists all existing notes, alongside their creation time.

**Format:**
```
list [-p]
```

#### To view all notes
Use the command without the flag.

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

#### To view only pinned notes:
Use the `-p` flag.

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

## Command Summary
* Start a new Zettelkasten repository: `init <repository-name>`
* Add a new note: `new -t <TITLE> [-b <BODY>]`
* List all notes: `list`
* List only pinned notes: `list -p`
* Delete a note (with confirmation): `delete <NOTE_ID>`
* Delete a note (without confirmation): `delete -f <NOTE_ID>`
* Pin a note: `pin <NOTE_ID>`
* Unpin a note: `unpin <NOTE_ID>`

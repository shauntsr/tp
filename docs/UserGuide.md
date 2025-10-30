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

---

### Editing a Note: `edit`

Opens the note in your editor to update its content.

**Format:**
```
edit <NOTE_ID>
```

**Example:**
```
edit e0e7b989
```

**Expected Output:**
```
Opening editor for note body...
Note body saved from editor.
Successfully edited note: New_Note.txt, id: e0e7b989
```

---

### Creating a Global Tag: `new-tag`

Adds a tag to the global list so it can be applied to notes.

**Format:**
```
new-tag <TAG_NAME>
```

**Example:**
```
new-tag urgent
```

**Expected Output:**
```
 Tag 'urgent' has been added.
```

---

### Tagging a Note: `add-tag`

Adds a tag to a specific note. If the tag is new, it will be added to the global list too.

**Format:**
```
add-tag <NOTE_ID> <TAG_NAME>
```

**Example (tag is new globally):**
```
add-tag abcd1234 urgent
```

**Expected Output:**
```
 Tag 'urgent' has been added.
 Note #abcd1234 has been tagged with 'urgent'
```

**Example (tag already exists globally):**
```
add-tag abcd1234 urgent
```

**Expected Output:**
```
 Note #abcd1234 has been tagged with 'urgent'
```

---

### Linking Two Notes (one direction): `link`

Creates a link from a source note to a target note.

**Format:**
```
link <SOURCE_NOTE_ID> <TARGET_NOTE_ID>
```

**Example:**
```
link abcd1234 ef567890
```

**Expected Output:**
```
 Note 'First Note' now links to note 'Second Note'.
```

---

### Unlinking Two Notes (one direction): `unlink`

Removes a link from a source note to a target note.

**Format:**
```
unlink <SOURCE_NOTE_ID> <TARGET_NOTE_ID>
```

**Example:**
```
unlink abcd1234 ef567890
```

**Expected Output:**
```
 The link from note #abcd1234 to note #ef567890 has been removed.
```

---

### Linking Notes in Both Directions: `link-both`

Creates links in both directions between two notes.

**Format:**
```
link-both <NOTE_ID_1> <NOTE_ID_2>
```

**Example:**
```
link-both abcd1234 ef567890
```

**Expected Output:**
```
 Notes 'First Note' and 'Second Note' are now linked in both directions.
```

---

### Unlinking Notes in Both Directions: `unlink-both`

Removes all links between two notes, in both directions.
If there is an existing single link between the two notes, it will
also remove that only link.

**Format:**
```
unlink-both <NOTE_ID_1> <NOTE_ID_2>
```

**Example:**
```
unlink-both abcd1234 ef567890
```

**Expected Output:**
```
 All links between note #abcd1234 and note #ef567890 have been removed.
```

---

### Listing Incoming Links: `list-incoming-links`

Shows the notes that link to the specified note (incoming links).

**Format:**
```
list-incoming-links <NOTE_ID>
```

**Example:**
```
list-incoming-links ef567890
```

**Expected Output:**
```
 Here are the notes that link to note #ef567890 (incoming):
    1. First_Note.txt 2025-10-29 abcd1234
```

---

### Listing Outgoing Links: `list-outgoing-links`

Shows the notes that the specified note links to (outgoing links).

**Format:**
```
list-outgoing-links <NOTE_ID>
```

**Example:**
```
list-outgoing-links abcd1234
```

**Expected Output:**
```
 Here are the notes that note #abcd1234 links to (outgoing):
    1. Second_Note.txt 2025-10-29 ef567890
```

---

### Listing All Tags Globally: `list-tags-all`

Displays all tags that exist globally across the repository.

**Format:**
```
list-tags-all
```

**Example:**
```
list-tags-all
```

**Expected Output:**
```
You have 3 tags:
        1. urgent
        2. java
        3. common
```

---

### Listing Tags on a Single Note: `list-tags`

Shows all tags applied to a single note.

**Format:**
```
list-tags <NOTE_ID>
```

**Example:**
```
list-tags abcd1234
```

**Expected Output:**
```
 Tags for note #abcd1234:
    1. urgent
    2. common
```

---

### Deleting a Tag from a Note: `delete-tag`

Removes a tag from a specific note. Use `-f` to skip the confirmation.

**Format:**
```
delete-tag [-f] <NOTE_ID> <TAG>
```

**Example (with confirmation):**
```
delete-tag abcd1234 urgent
```

**Expected Output:**
```
Confirm deletion of tag 'urgent' on note # 'abcd1234'? (y/n)
> y
 Tag 'urgent' has been deleted from note #abcd1234.
```

**Example (force, no confirmation):**
```
delete-tag -f abcd1234 urgent
```

**Expected Output:**
```
 Tag 'urgent' has been deleted from note #abcd1234.
```

---

### Deleting a Tag Globally: `delete-tag-globally`

Removes a tag from the global list and from all notes that have it. Use `-f` to skip the confirmation.

**Format:**
```
delete-tag-globally [-f] <TAG>
```

**Example (with confirmation):**
```
delete-tag-globally urgent
```

**Expected Output:**
```
Confirm deletion of tag 'urgent'? (y/n)
> y
 Tag 'urgent' has been deleted across all notes, globally.
```

**Example (force, no confirmation):**
```
delete-tag-globally -f urgent
```

**Expected Output:**
```
 Tag 'urgent' has been deleted across all notes, globally.
```

---

### Renaming a Tag Globally: `rename-tag`

Renames a tag everywhere. All notes using the old tag will be updated to the new tag.

**Format:**
```
rename-tag <OLD_TAG> <NEW_TAG>
```

**Example:**
```
rename-tag common general
```

**Expected Output:**
```
 Tag 'common' has been renamed to 'general' across all notes. All affected notes have been updated.
```

---

### Searching Notes: `find`

Searches for notes containing the specified text in their title or body.

**Format:**
```
find <TEXT>
```

**Example (results found):**
```
find hello
```

**Expected Output:**
```
 Here are the matching notes in your list:
    1. test3.txt 2025-10-29 c7a257e1
    2. test2.txt 2025-10-29 3a782a6f
```

**Example (no results):**
```
find python
```

**Expected Output:**
```
 No notes found matching the search criteria.
```

---

### Exiting the Application: `bye`

Closes ZettelCLI.

**Format:**
```
bye
```

**Example:**
```
bye
```

**Expected Output:**
```
 Bye. Hope to see you again soon!
```

---

### Archiving a Note: `archive`

Moves a note to the archive so it no longer appears in regular lists. You can still unarchive it later.

**Format:**
```
archive <NOTE_ID>
```

**Example:**
```
archive abcd1234
```

**Expected Output:**
```
Note at abcd1234 archived
```

---

### Unarchiving a Note: `unarchive`

Restores an archived note back to your regular notes so it shows up in normal listings.

**Format:**
```
unarchive <NOTE_ID>
```

**Example:**
```
unarchive abcd1234
```

**Expected Output:**
```
Note at abcd1234 unarchived
```

---

## Command Summary (extended)

- `init <repo-name>` — Initialize a new repository
- `new -t <title> [-b <body>]` — Create a new note
- `edit <note-id>` — Edit an existing note
- `list [-p] [-a]` — List notes (filters: `-p` pinned only, `-a` archived only; combine both for pinned archived only)
- `delete [-f] <note-id>` — Delete a note by ID
- `pin <note-id>` — Pin a note
- `unpin <note-id>` — Unpin a note
- `new-tag <tag-name>` — Add a tag globally
- `add-tag <note-id> <tag-name>` — Tag a note
- `link <source-id> <target-id>` — Link two notes (one direction)
- `unlink <source-id> <target-id>` — Unlink two notes (one direction)
- `link-both <id1> <id2>` — Link two notes in both directions
- `unlink-both <id1> <id2>` — Unlink two notes in both directions
- `list-incoming-links <note-id>` — Show incoming linked notes
- `list-outgoing-links <note-id>` — Show outgoing linked notes
- `list-tags-all` — List all tags globally
- `list-tags <note-id>` — List tags for a single note
- `delete-tag [-f] <note-id> <tag>` — Delete a tag from a note
- `delete-tag-globally [-f] <tag>` — Delete a tag from all notes
- `rename-tag <old-tag> <new-tag>` — Rename a tag globally
- `find <text>` — Search for notes
- `bye` — Exit the application
- `archive <note-id>` — Move a note to the archive
- `unarchive <note-id>` — Restore an archived note

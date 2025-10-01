# Coach Bot User Guide

Coach Bot is the only personal life coach you need! Coach can help create, mark and manage your tasks. Set ToDos, track deadlines, and plan events to stay up to date in life! With Coach Bot, you can organize your personal and professional life with just a few types in the command line

---

## Quick Start

1. Ensure you have Java 17 or above installed in your Computer.
2. Download the latest .jar file
3. Copy the file to the folder you want to use as the home folder for your coach.jar
4. Open a command terminal, cd into the folder you put the jar file in, and use the java -jar coach.jar command to run the application
4. Type the command in the command box and press Enter to execute it. e.g. typing list will list current tasks inputted

---

## Features

### Adding Todos

Add a simple task to your list without any date or time.

**Command format:** `todo DESCRIPTION`

**Example:**
todo Finish CS2113 exercise

**Expected output:**
Great! I've added this task to help you grow:
[T][ ] Finish CS2113 exercise
Now you have 1 task in the list.

---

### Adding Deadlines

Add a task that needs to be completed by a specific date and time.

**Command format:** `deadline DESCRIPTION /by DATE_TIME`

**Example:**
deadline Complete project proposal /by 2024-10-15 2359

**Expected output:**
Got it! I've added this deadline:
[D][ ] Complete project proposal (by: Oct 15 2024, 11:59 PM)
Now you have 2 tasks in the list.

> **Note:** Date format should ideally be `YYYY-MM-DD HHMM` (24-hour format), other formats will also be accepted

---

### Adding Events

Add a task that occurs during a specific time period.

**Command format:** `event DESCRIPTION /from START_TIME /to END_TIME`

**Example:**
event Coaching session with mentor /from 2024-10-10 1400 /to 2024-10-10 1600

**Expected output:**
Excellent! I've scheduled this event:
[E][ ] Coaching session with mentor (from: Oct 10 2024, 2:00 PM to: Oct 10 2024, 4:00 PM)
Now you have 3 tasks in the list.

---

### Listing All Tasks

View all tasks in your current list to see your progress.

**Command format:** `list`

**Example:**
list

**Expected output:**
 Here are the tasks in your list:

[T][ ] Finish CS2113 exercise
[D][ ] Complete project proposal (by: Oct 15 2024, 11:59 PM)
[E][ ] Coaching session with mentor (from: Oct 10 2024, 2:00 PM to: Oct 10 2024, 4:00 PM)


**Legend:**
- `[T]` = Todo
- `[D]` = Deadline
- `[E]` = Event
- `[X]` = Marked as completed
- `[ ]` = Not completed

---

### Marking Tasks as Done

Mark a task as completed.

**Command format:** `mark TASK_NUMBER`

**Example:**
mark 1

**Expected output:**
Nice! I've marked this task as done:
[T][X] Finish CS2113 exercise

---

### Unmarking Tasks

Mark a completed task as not done.

**Command format:** `unmark TASK_NUMBER`

**Example:**
unmark 1

**Expected output:**
Nice! I've marked this task as not done yet:
[T][ ] Finish CS2113 exercise

---

### Deleting Tasks

Remove a task from your list permanently.

**Command format:** `delete TASK_NUMBER`

**Example:**
delete 2

**Expected output:**
Noted. I've removed this task:
[D][ ] Complete project proposal (by: Oct 15 2024, 11:59 PM)
Now you have 2 tasks in the list.

---

### Finding Tasks

Search for tasks containing a specific keyword.

**Command format:** `find KEYWORD`

**Example:**
find exercise

**Expected output:**
Here are the matching tasks in your list:

[T][ ] Finish CS2113 exercise

---

### Viewing Tasks on a Specific Date

View all tasks scheduled on a particular date.

**Command format:** `on DATE`

**Example:**
on 2024-10-15

**Expected output:**
Here are the tasks on Oct 15 2024:

[D][ ] Submit assignment (by: Oct 15 2024, 11:59 PM)
[E][ ] Team meeting (from: Oct 15 2024, 2:00 PM to: Oct 15 2024, 4:00 PM)


> **Note:** Date format should be `YYYY-MM-DD`

---

**Expected output:**
Available commands:

todo DESCRIPTION - Add a simple task
deadline DESCRIPTION /by DATE_TIME - Add a task with deadline
event DESCRIPTION /from START /to END - Schedule an event
list - View all your tasks
mark TASK_NUMBER - Mark a task as complete
unmark TASK_NUMBER - Mark a task as incomplete
delete TASK_NUMBER - Remove a task
find KEYWORD - Search for tasks
on - View task on specified date
bye - Exit Coach Bot

---

### Exiting Coach Bot

Close the application and save your tasks.

**Command format:** `bye`

**Example:**
bye

**Expected output:**
Bye. Hope to see you again soon!

---

## Command Summary

| Command | Format | Example |
| --- | --- | --- |
| Add todo | `todo DESCRIPTION` | `todo Morning workout` |
| Add deadline | `deadline DESCRIPTION /by DATE` | `deadline Report /by 2024-10-15 2359` |
| Add event | `event DESCRIPTION /from START /to END` | `event Gym /from 2024-10-10 1400 /to 2024-10-10 1600` |
| List tasks | `list` | `list` |
| Mark done | `mark NUMBER` | `mark 1` |
| Unmark | `unmark NUMBER` | `unmark 1` |
| Delete | `delete NUMBER` | `delete 2` |
| Find | `find KEYWORD` | `find workout` |
| On | `on` | `on 2024.10/15` |
| Exit | `bye` | `bye` |

---

## Saving the Data

Coach Bot data are saved in the hard disk automatically after any command that changes the data. There is no need to save manually.

## Editing the Data File

Coach Bot data are saved automatically as a text file `[JAR file location]/data/coachbot.txt`. Advanced users are welcome to update data directly by editing that data file.

### Data File Format

The data file uses the following format:
T | 0 | Read book
D | 1 | Submit assignment | 2024-10-15 2359
E | 0 | Team meeting | 2024-10-10 1400 /to 2024-10-10 1600

**Format explanation:**
- First character: Task type (`T` = Todo, `D` = Deadline, `E` = Event)
- Second field: Completion status (`0` = Not done, `1` = Done)
- Third field: Task description
- Fourth field (Deadline): Due date and time
- Fourth & Fifth fields (Event): Start date/time and end date/time
- Fields are separated by ` | ` (space, pipe, space) or /to for event times

> ⚠️ **Caution:** If your changes to the data file makes its format invalid, Coach Bot will ignore lines as corrupted and print them out on start.

---

## FAQ

**Q: How do I transfer my data to another computer?**  
A: Install the app in the other computer and overwrite the empty data file it creates with the file that contains the data of your previous Coach Bot home folder.

---

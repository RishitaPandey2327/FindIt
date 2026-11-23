# FindIt - Campus Lost & Found System

A simple desktop application where students can report lost or found items, get automatic match suggestions, track item status, and reward people who return items.

Built with **Core Java + Swing + file storage** (no database required).

## Features

- **Report Lost / Found Item**: item name, category, color, campus location, date and description
- **Smart Matching**: rule-based suggestions using category, color and location (no AI)
- **Item Status Tracking**: unique ID and status: `LOST`, `FOUND`, `MATCHED`, `RETURNED`
- **Campus Location Filter**: Library, Computer Lab, Cafeteria, Auditorium and more
- **Good Samaritan Rewards**: finder gets 10 points and a badge for a verified return
- **In-app Updates**: email-style notifications such as "Possible match found" and "Item returned successfully"

## Tech Stack

| Technology | Purpose |
|---|---|
| Java | Main application logic |
| Swing | GUI screens and forms |
| ArrayList / Map | Managing items, notifications and users in memory |
| File Handling | Saving data permanently in text files |
| OOP | Classes and code organization |

## Project Structure

| File | Purpose |
|---|---|
| `Item.java` | Model class for one lost/found report |
| `Store.java` | Data storage (ArrayLists), file save/load, status workflow, rewards |
| `Matcher.java` | Rule-based matching logic and scoring |
| `UI.java` | Colors, fonts and custom styled components |
| `FindItApp.java` | Main window and all screens |
| `run.bat` / `run.sh` | Compile and run scripts |

## Screens

1. **Dashboard**: counts of Lost, Found, Matched and Returned items, plus recent updates
2. **Report Item**: Lost/Found form
3. **Find Matches**: possible matches with a score, then confirm
4. **Manage Reports**: search, filter by location/status, mark returned, delete
5. **Rewards**: points leaderboard and badges
6. **Updates**: all notifications

## Matching Rules

| Rule | Points |
|---|---|
| Same category | 40 |
| Same color | 30 |
| Same location | 20 |
| Common word in item name | 10 |

Category must match, and a total score of 60 or more is suggested as a possible match.

## How to Run

Requirement: **JDK 11 or higher**.

**Windows:** double-click `run.bat`

**Mac / Linux:**
```bash
chmod +x run.sh
./run.sh
```

**Manual:**
```bash
mkdir out
javac -d out *.java
java -cp out FindItApp
```

Data is saved automatically in the `data/` folder (`items.txt`, `notifs.txt`, `users.txt`). Delete this folder to start fresh.

## Rewards and Badges

- 1 return: Good Samaritan
- 3 returns: Campus Hero
- 5 returns: Campus Legend

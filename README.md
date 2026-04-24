# Quiz Leaderboard System

Java solution for Bajaj Finserv Health SRM Internship Assignment.

## Logic
- Polled validator API 10 times
- Maintained 5-second delay between polls
- Removed duplicates using:
  roundId + participant
- Aggregated scores participant-wise
- Generated sorted leaderboard
- Computed total score = 2290
- Submitted once successfully

## Final Leaderboard
1. George - 795
2. Hannah - 750
3. Ivan - 745

Total Score: 2290

## Run
```bash
javac QuizLeaderboardApp.java
java QuizLeaderboardApp
```
# Settings Persistence — Sanity Check
## Daily Student Expense Tracker with Spending Score
### Tester: Jose G. Leon Zambrano
### Date: 15 July 2026
### Purpose: Verify the settings.txt persistence fix (password + daily reminder)
### and confirm it did not break existing behavior, before the final commit.

---

## Background

Team testing (Julien Morales, Test 9) found that the daily-reminder toggle did
not survive a relaunch, and the password had the same session-only limitation.
Both are now persisted to a small `settings.txt` file via new loadSettings() and
saveSettings() methods. These 6 checks confirm the fix works and broke nothing.

`settings.txt` is gitignored (it holds the user's password) and is recreated
automatically whenever a setting changes.

---

## Setup

1. Know the current password (check settings.txt, or reset by deleting that file
   to fall back to the hardcoded default)
2. Have the app closed before starting
3. Note today's date above

---

## Test SP-1: Password persists across relaunch (the core fix)

Purpose: Confirm loadSettings() reads the saved password on startup.

Input:
- Ensure settings.txt has a non-default password (change it in Settings if needed)
- Exit the app fully, then relaunch
- At login, enter the CURRENT (changed) password

Expected:
- The changed password works
- The old default password (password123) does NOT work

Actual: Password change persists in different sessions now

Status: PASS

---

## Test SP-2: Daily reminder toggle persists across relaunch (Julien Test 9 fix)

Purpose: Confirm the exact bug Julien found is resolved.

Input:
- Log in, go to Settings, turn the daily reminder checkbox OFF
- Exit fully, relaunch, log in
- Then repeat: turn it back ON, exit, relaunch, log in

Expected:
- After turning OFF: the reminder dialog does NOT appear on the next launch
- After turning ON: the reminder dialog DOES appear again

Actual: Toggle now persists, ie:
- the dialog box does not appear if in the previous session it is disabled OR
- the dialog box appears after login if enabled in Settings in the previous sesion

Status: PASS

---

## Test SP-3: Change Password end to end, message updated

Purpose: Confirm the full change flow and that the dialog no longer claims the
password resets on relaunch.

Input:
- Settings, Change Password
- Enter current password, a new password, matching confirmation, click OK
- Read the success dialog carefully
- Exit, relaunch, log in with the newest password

Expected:
- Success dialog says "Password changed successfully." with NO note about
  resetting on relaunch
- The new password works after relaunch

Actual: Password changes persist successfully in relaunch with no resetting

Status: PASS

---

## Test SP-4: Change Password still rejects bad input (regression check)

Purpose: Confirm the saveSettings() addition did not disturb existing validation.

Input (three sub-cases):
- Wrong current password
- New password and confirmation do not match
- Empty new password

Expected:
- Wrong current: "Current password is incorrect."
- Mismatch: mismatch error, no change saved
- Empty: "New password cannot be empty."
- In all three: settings.txt password is unchanged

Actual: When testing all three cases, the expected results appear

Status: PASS

---

## Test SP-5: A transaction still saves and reloads (regression check)

Purpose: Confirm the new loadSettings() call in start() did not interfere with
the CSV load that runs right after it.

Input:
- Add one expense, exit fully, relaunch, log in
- Open Transaction History

Expected:
- The expense is still present after relaunch
- Balance and score reflect it

Actual: Expense is recorded succesfully and persist in next session

Status: PASS

---

## Test SP-6: Corrupt settings.txt does not crash the app (new failure mode)

Purpose: Verify the general catchall in loadSettings() handles a broken file.

Input:
- Close the app
- Open settings.txt, replace all contents with garbage (e.g. @@@broken@@@), save
- Relaunch
- Then close again, DELETE settings.txt entirely, relaunch once more

Expected:
- With garbage contents: app starts normally on default settings, no crash
- With the file deleted: app starts normally, recreates settings.txt on the
  next setting change

Actual:
- Garbled settings.txt file does not crash the app on launc;
- deleting file does not prevent launch, app recreated it when settings changed.

Status: PASS

---

## Summary

| Test | Status | Notes |
|------|--------|-------|
| SP-1: Password persists | PASS   | N/A   |
| SP-2: Reminder toggle persists (Julien Test 9) | PASS   | N/A   |
| SP-3: Change Password end to end | PASS   | N/A   |
| SP-4: Change Password rejects bad input | PASS   | N/A   |
| SP-5: Transaction still saves/reloads | PASS   | N/A   |
| SP-6: Corrupt/missing settings.txt handled | PASS   | N/A   |

## Note on affected earlier tests

- Addendum Test B-10 is INVERTED by this fix: the new password now persists,
  so its Expected must be rewritten (new password works, default fails). Update
  it in the addendum before the full 71-case run.
- Julien's Test 9 (reminder bug) is now FIXED; note this where his results are
  recorded, without altering his original findings.

## Decision

- All 6 PASS: stage docs/, do the single final commit, push, submit.
- Any FAIL: capture the exact behavior and debug before committing.
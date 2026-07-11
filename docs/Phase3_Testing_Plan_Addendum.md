# Phase 3 — Testing Plan ADDENDUM
## Additional test cases for features added after July 10, 2026
### To be merged into the master `Phase3_Testing_Plan.md` before Saturday's full run

---

## HOW TO USE THIS FILE

The original testing plan (`Phase3_Testing_Plan.md`) was written before the
following features were added or changed. Add these new tests to the master
plan under the group indicated. Do NOT delete existing tests: renumber them
around the new ones as needed.

---

## GROUP A (Startup and Persistence) — ADDITIONS

### Test A-7: Export creates a properly named file
Add to Group A.
Input: Log in as student, add any transaction, go to Transaction History,
click "Export Full Ledger to CSV"
Expected: Dialog confirms save with the format:
`TransactionLedger_student_[today MM-DD-YYYY].csv saved with N record(s).`
The file appears in the project folder (not in some other location)
Actual:
Status:

### Test A-8: Export contains the full ledger, not the filtered view
Purpose: Verify the filtered date range in History does NOT affect exports
Input: With a ledger spanning more than 60 days, apply "Last 60 Days"
filter, then click Export
Expected: Exported file contains ALL transactions, including those outside
the 60-day window
Actual:
Status:

### Test A-9: MDC logo displays on login
Input: Launch the app
Expected: The Miami Dade College logo appears above the title, centered.
If the logo is missing, it should render as empty space with no crash and
no error dialog (Swing behavior)
Actual:
Status:

---

## GROUP B (Login and Lockout) — ADDITIONS

### Test B-6: Change Password happy path
Input: Log in, go to Settings, click Change Password. Enter current
password, a new password, and matching confirmation. Click OK
Expected: Success dialog with note that the password resets on relaunch.
Attempting to log in on the next attempt (log out is not implemented, so
this must wait until relaunch OR test via a fresh Change Password call)
Actual:
Status:

### Test B-7: Change Password rejects wrong current password
Input: In Settings, Change Password. Enter WRONG current password
Expected: Error dialog "Current password is incorrect."
Actual:
Status:

### Test B-8: Change Password rejects empty new password
Input: Change Password with correct current password but blank new password
Expected: Error dialog "New password cannot be empty."
Actual:
Status:

### Test B-9: Change Password rejects mismatched confirmation
Input: Correct current, new password "abc123", confirmation "abc124"
Expected: Error dialog about mismatch
Actual:
Status:

### Test B-10: Password reset on relaunch
Input: After changing the password successfully in Test B-6, exit the
app fully and relaunch
Expected: The NEW password no longer works. The DEFAULT password
(`password123` or whatever is hardcoded) works instead
Actual:
Status:

---

## GROUP D (History and Statistics Display) — ADDITIONS FOR DATE RANGE FILTER

### Test D-7: Date range filter defaults to "All Transactions"
Input: Enter several transactions, open History
Expected: Dropdown shows "All Transactions" by default; all rows visible
Actual:
Status:

### Test D-8: "Last 60 Days" filter excludes older records
Input: Manually edit a saved CSV to include a record dated more than
60 days ago. Restart the app. Open History, select "Last 60 Days"
Expected: The old record does not appear in the table; summary stats
recompute over the visible records only
Actual:
Status:

### Test D-9: Custom Date Range shows input fields
Input: Select "Custom Date Range" from the dropdown
Expected: Start Date and End Date fields with an Apply button appear.
Other filter options hide these fields when reselected
Actual:
Status:

### Test D-10: Custom Date Range rejects invalid dates
Input: Start = "abc", End = "12/31/2026". Click Apply
Expected: Red error message "Invalid date. Please use MM/DD/YYYY format..."
Actual:
Status:

### Test D-11: Custom Date Range rejects end-before-start
Input: Start = 06/30/2026, End = 06/01/2026. Click Apply
Expected: Red error "End date cannot be before start date."
Actual:
Status:

### Test D-12: Custom Date Range clamps ranges over 60 days
Input: Start = 01/01/2026, End = 12/31/2026. Click Apply
Expected: Red notice "Display range limited to 60 days. Use Export to
CSV to access full ledger history." Table displays only the first 60 days
from start
Actual:
Status:

### Test D-13: Balance always shows the full ledger even when filtered
Purpose: Verify the design decision that balance is not filtered
Input: Filter to Last 60 Days when the ledger contains older income
Expected: The "Current Balance" line shows the full-ledger balance (with
"[full ledger]" label), NOT a recomputed filtered balance
Actual:
Status:

---

## GROUP E (Spending Score Calculation) — ADDITIONS

### Test E-9: Largest anomaly displayed when multiple exceed threshold
Purpose: Verify the "keep the largest" behavior
Input: Income $2000. Expenses: $500, $600, $700 (all Entertainment, all
within the last 60 days)
Hand calculation: average = 600; threshold = min(1800, 500) = 500
All three exceed 500. The Statistics panel should flag the LARGEST ($700)
Expected: Warning shows "$700.00" transaction, not the $500 or $600
Actual:
Status:

---

## GROUP F (Settings) — ADDITIONS

### Test F-5: MDC resource button opens browser (At Risk score)
Input: Engineer an At Risk score (spend nearly all income, break the
50/30/20 rule, trigger an anomaly). Go to Statistics
Expected: A "Open: MDC Single Stop Emergency Aid" button appears.
Clicking it opens the default browser to the MDC Single Stop URL
Actual:
Status:

### Test F-6: MDC resource button opens browser (Needs Improvement score)
Input: Engineer a Needs Improvement score. Go to Statistics
Expected: A "Open: TRIO @ MDC" button appears. Clicking it opens the
default browser to https://www.mdc.edu/trio-eoc/ (note: not trioeoc,
the correct URL includes a hyphen)
Actual:
Status:

### Test F-7: Childcare resource button opens browser
Input: Add a Family / Childcare category expense. Go to Statistics
Expected: Regardless of score, an "Open: MDC Student Parents Resources"
button appears and opens https://www.mdc.edu/student-parents/
Actual:
Status:

### Test F-8: Browser link fallback when Desktop.browse fails
Purpose: Verify the graceful fallback
Input: Difficult to trigger deliberately, but if any resource button ever
fails to open the browser
Expected: A dialog appears showing the URL as text so the user can copy
and paste it manually (rather than the app crashing)
Actual:
Status: N/A unless triggered

---

## CROSS-CUTTING TEST — GENERAL BACKUP CATCHALL

### Test X-1: Corrupted CSV triggers general catchall (already in Group A-5)
This test in the master plan verifies the broad catch (Exception) block.
Expand the notes to include: verify the fallback dialog says "Error
loading data. Starting fresh." and the app continues with an empty ledger

### Test X-2: General catchall in isValueValid
Input: Amount field with garbage characters like "@@@#"
Expected: Amount error shows normally, no stack trace to console
Actual:
Status:

### Test X-3: General catchall in isDateValid
Input: Date field with garbage like "///"
Expected: Date error shows normally, no stack trace to console
Actual:
Status:

---

## UPDATED TOTALS

Master plan currently has 48 tests. After merging:

| Group | Original | Added | New Total |
|-------|----------|-------|-----------|
| A | 6 | 3 | 9 |
| B | 5 | 5 | 10 |
| C | 13 | 0 | 13 |
| D | 6 | 7 | 13 |
| E | 8 | 1 | 9 |
| F | 4 | 4 | 8 |
| G | 6 | 0 | 6 |
| X (Cross) | 0 | 3 | 3 |
| TOTAL | 48 | 23 | 71 |

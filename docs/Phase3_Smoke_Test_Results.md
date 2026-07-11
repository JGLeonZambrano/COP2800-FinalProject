# Phase 3 — Smoke Test Results (5 tests)
## Daily Student Expense Tracker with Spending Score
### Tester: Jose G. Leon Zambrano
### Date: July 11, 2026
### Version tested: consolidated single-file, revision from July 10, 2026
### Environment: IntelliJ IDEA on macOS, project in iCloud Drive

---

## Purpose

These 5 tests are a fast smoke test to confirm the app runs and its core
features work end to end. They are NOT a substitute for the full 48-test plan
in `Phase3_Testing_Plan.md`, which still needs to be executed on Saturday
before submission.

---

## Setup

1. Delete any existing `transactions.csv` and any `TransactionLedger_*.csv`
   files from the project root
2. Have a Terminal window open showing the project folder contents (see the
   `while true` command) so you can watch files appear or change
3. Note today's date for the export filename check

---

## Test S-1: App launches and login accepts default credentials

Purpose: Confirm the app builds, runs, and the login flow works.

Input:
- Run `ExpenseTrackerApp.main()` from IntelliJ
- Username: `student`, Password: `password123`, click Log In

Expected:
- Login window appears with MDC logo, title, subtitle, fields, Log In button
- Daily Reminder dialog appears after clicking Log In
- Clicking either reminder button lands on the Main Menu
- Main Menu shows "Welcome, student! | [today's date]"
- No error dialogs

Actual: Everything works as expected

Status: PASS

---

## Test S-2: Add three transactions covering the sample data pattern

Purpose: Exercise the Add Transaction panel including type switching, category
repopulation, validation success, and the running balance update.

Input:
1. Income, $1030.00, "Spring disbursement", 06/15/2026, Financial Aid
2. Expense, $5.50, "Pharmacy copay", 06/20/2026, Health / Medical
3. Expense, $25.00, "Bus pass renewal", 06/25/2026, Transport

Expected:
- After entry 1: green status "Transaction added successfully. Balance: $1030.00"
- After entry 2: balance $1024.50
- After entry 3: balance $999.50
- Category dropdown correctly switches between income categories (4 options)
  and expense categories (17 options) when Type is flipped
- Fields clear after each successful add
- No red error labels

Actual: All fields change and workd correctly, as expected. Balance changes correctly as well.

Status: PASS

---

## Test S-3: View History and Statistics with the entered data

Purpose: Confirm the display panels and the composite scoring formula.

Input: With the three transactions from S-2 loaded, navigate to Transaction
History, then to Statistics & Spending Score.

Expected in History:
- Table shows 3 rows sorted most-recent-first: Bus pass, Pharmacy copay,
  Spring disbursement (top to bottom)
- Summary shows: Balance $999.50, Average Expense $15.25 (approximately;
  precise value depends on exact rounding), Minimum $5.50, Maximum $25.00
- Date range dropdown defaults to "All Transactions"

Expected in Statistics:
- Financial summary matches History
- Spending Score displays with format "X / 100 - [status]"
- Hand-calculated expected score:
    balanceScore: 999.50/1030.00 = 97.04% >= 80% -> 60 points
    distribution: only 2 expenses, both in NEEDS bucket (Health, Transport)
    -> needs 100%/deviation 50 = 0, wants deviation 30 = 3, savings dev 20 = 6
    -> distribution = 9 points
    anomaly: average expense = 15.25; threshold = min(45.75, 257.50) = 45.75
    max expense $25 does NOT exceed threshold -> 10 points
    TOTAL: 60 + 9 + 10 = 79 -> "Good"
- No unusual expense warning appears
- No MDC resource links appear (score is "Good", not "At Risk" or "Needs
  Improvement", and no childcare category)

Actual:Values seen as expected

Status: PASS

---

## Test S-4: Export button creates a properly named CSV file

Purpose: Verify the export filename pattern and that Export writes the full
ledger, not the filtered view.

Input: From Transaction History, click "Export Full Ledger to CSV". Check the
Terminal window watching the project folder.

Expected:
- Dialog: "TransactionLedger_student_[today MM-DD-YYYY].csv saved with 3 record(s)."
- The file appears in the project folder within 2 seconds
- Opening the file (double-click, or `cat` in Terminal) shows the header row
  followed by exactly 3 records, one per line, comma-separated

Actual: TransactionLedger_student_07-11-2026 created correctly with the appropriate fields

Status: PASS

---

## Test S-5: Exit, relaunch, verify data persists

Purpose: Confirm the persistent data storage requirement end to end: save on
exit, load on next launch, balances recalculated correctly.

Input:
- From any panel, click "Back to Main Menu" then "Exit" (or close the window
  with the X button, which should trigger the same save sequence)
- Confirm the "transactions.csv saved with 3 record(s)." dialog appears
- Confirm the goodbye dialog appears
- Watch the Terminal: `transactions.csv` should exist now
- Relaunch the app in IntelliJ
- Log in with default credentials

Expected:
- Main Menu shows the current Spending Score (79 - Good) since the ledger is
  no longer empty
- Statistics panel shows the same values as in S-3 (balance, income,
  expenses, average, min, max, score)
- History panel shows the same 3 transactions

Actual: Data from Test 4 remains in place, as expected, and data from CSV matches the one in Transaction history

Status: PASS

---

## Summary

| Test | Status | Notes |
|------|--------|-------|
| S-1: Launch and login | Pass |None |
| S-2: Add 3 transactions |Pass |None |
| S-3: History and Statistics with score verification |Pass |None |
| S-4: Export button and filename |Pass |None |
| S-5: Persistence across relaunch |Pass |None |

## Decision

- If all 5 PASS: proceed to UML update, then push to GitHub. Full 48-test
  plan runs on Saturday morning

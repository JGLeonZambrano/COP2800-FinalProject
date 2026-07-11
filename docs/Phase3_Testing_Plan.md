# Phase 3 — Testing Plan
## Daily Student Expense Tracker with Spending Score
### Lead Developer: Jose G. Leon Zambrano
### Format follows the Matrix Calculator consolidated test file (Tester / Input / Expected / Actual / Status)

---

## BEFORE RUNNING ANY TEST

**STOP — P-1 check:** Do not begin testing until the Professor confirms
single-file vs. multi-file format. Test whichever version will actually be
submitted. Testing the wrong version wastes a full pass.

**Setup for each test session:**
1. Note which version is under test (consolidated single file / five files)
2. Delete or rename any existing transactions.csv to start from a known state,
   unless the test says otherwise
3. Record the date of the test run (several tests depend on today's date)

**How to use this file:** Copy it into the project repo. For each test, fill in
Actual and Status (PASS / FAIL). Failed tests come back to Claude with the
exact input, expected, and actual output.

---

## GROUP A — STARTUP AND PERSISTENCE

### Test A-1: First launch, no CSV file
Purpose: App starts fresh without crashing when no saved data exists
Input: Delete transactions.csv, launch app, log in
Expected: Login panel appears; after login, main menu shows with no
Spending Score line (ledger is empty)
Actual:
Status:

### Test A-2: Save on Exit button
Purpose: Exit sequence writes the CSV
Input: Log in, add one expense (e.g., $10.00, "Test memo", today's date,
Groceries / Food), click Exit
Expected: Dialog "transactions.csv saved with 1 record(s)." then goodbye
dialog, then app closes. transactions.csv exists on disk with header row
plus one record
Actual:
Status:

### Test A-3: Save on window X button (IMPROVEMENT — verify it works)
Purpose: Closing with the window's X button must also save (new behavior)
Input: Log in, add one expense, close the window with the X button
Expected: Same save dialogs as Test A-2 — data is NOT lost
Actual:
Status:

### Test A-4: Reload on next launch
Purpose: Saved data loads and balances recalculate
Input: After Test A-2, relaunch the app and log in
Expected: Main menu shows a Spending Score (ledger not empty); Statistics
shows the same balance, income, and expenses as before exit
Actual:
Status:

### Test A-5: Corrupted CSV file (D-1 catch block)
Purpose: A malformed CSV row must not crash the app
Input: Open transactions.csv in a text editor. Break one record (e.g.,
delete two commas or change an amount to "abc"). Launch app, log in
Expected: "Error loading data. Starting fresh." dialog; app continues
with an empty ledger; NO crash, NO frozen window
Actual:
Status:

### Test A-6: CSV with blank trailing line
Purpose: Blank lines in the file are skipped, not crashed on
Input: Add an empty line at the end of a valid transactions.csv, launch
Expected: All valid records load normally; blank line ignored
Actual:
Status:

---

## GROUP B — LOGIN AND LOCKOUT

### Test B-1: Valid login
Input: Username "student", password "password123"
Expected: Daily reminder dialog (if enabled), then main menu
Actual:
Status:

### Test B-2: Empty fields
Input: Click Log In with one or both fields empty
Expected: "Please enter both username and password." — no attempt counted
Actual:
Status:

### Test B-3: Wrong password, attempts countdown
Input: Enter wrong password once
Expected: "Incorrect credentials. Attempts remaining: 4"
Actual:
Status:

### Test B-4: Lockout after 5 failures
Input: Enter wrong password 5 times
Expected: "Too many failed attempts. Locked for 3 minutes." Log In button
is disabled (grayed out)
Actual:
Status:

### Test B-5: Lockout expires
Input: After Test B-4, wait 3 minutes
Expected: Button re-enables, "You may try again." message; correct
credentials now work
Actual:
Status:

---

## GROUP C — ADD TRANSACTION VALIDATION
(Each of these should show a red error under the specific field, and NOT
add anything to the ledger.)

### Test C-1: Non-numeric amount
Input: Amount = "abc"
Expected: "Invalid amount. Please enter a positive numeric value."
Actual:
Status:

### Test C-2: Negative amount
Input: Amount = "-10.00"
Expected: Same amount error
Actual:
Status:

### Test C-3: Zero amount
Input: Amount = "0"
Expected: Same amount error
Actual:
Status:

### Test C-4: Empty memo
Input: Valid amount, memo left blank
Expected: "Memo must be 1-50 characters, with no commas."
Actual:
Status:

### Test C-5: Memo over 50 characters
Input: Memo = 51+ characters (e.g., paste "aaaa..." 60 times)
Expected: Same memo error
Actual:
Status:

### Test C-6: Memo containing a comma (NEW RULE — CSV protection)
Purpose: Commas in memos would corrupt the CSV slicing on reload
Input: Memo = "Lunch, coffee, snacks"
Expected: Same memo error — comma rejected
Actual:
Status:

### Test C-7: Invalid date format
Input: Date = "13/45/2026"
Expected: "Invalid date. Please use MM/DD/YYYY format (not in the future)."
Actual:
Status:

### Test C-8: Future date
Input: Date = "12/31/2099"
Expected: Same date error
Actual:
Status:

### Test C-9: Valid transaction, happy path
Input: Expense, $45.00, "Grocery run at Walmart", today's date,
Groceries / Food
Expected: Green "Transaction added successfully. Balance: $-45.00" (or
correct balance); fields clear for next entry
Actual:
Status:

### Test C-10: Duplicate entry rejection
Input: Enter the exact same transaction as Test C-9 again (same amount,
memo, date, category)
Expected: Dialog "This transaction already exists in your ledger."
Ledger count unchanged
Actual:
Status:

### Test C-11: Near-duplicate accepted
Purpose: Only EXACT matches are duplicates
Input: Same as C-9 but change the amount to $45.01
Expected: Transaction accepted normally
Actual:
Status:

### Test C-12: Category dropdown switches with type
Input: Toggle Transaction Type between Income and Expense
Expected: Category dropdown repopulates — Income shows 4 income options;
Expense shows the full expense list
Actual:
Status:

### Test C-13: Multiple validation errors at once
Input: Amount = "abc", empty memo, date = "99/99/9999"
Expected: All three red error labels appear simultaneously
Actual:
Status:

---

## GROUP D — HISTORY AND STATISTICS DISPLAY

Setup: Start fresh, then enter the Phase 2 sample data set:
1. Income, $1030.00, "Spring disbursement", 06/15/2026, Financial Aid
2. Expense, $5.50, "Pharmacy copay", 06/20/2026, Health / Medical
3. Expense, $25.00, "Bus pass renewal", 06/25/2026, Transport
4. Expense, $45.00, "Grocery run at Walmart", 06/27/2026, Groceries / Food

### Test D-1: History sort order
Expected: Table sorted most recent to earliest by DATE (Grocery run first,
Spring disbursement last), regardless of entry order
Actual:
Status:

### Test D-2: History shows all types
Expected: Both the Income row and the three Expense rows appear
Actual:
Status:

### Test D-3: Summary statistics (expenses only)
Expected: Current Balance: $954.50 | Average Expense: $25.17 |
Minimum Expense: $5.50 | Maximum Expense: $45.00
(Average = (5.50 + 25.00 + 45.00) / 3 = 25.166... rounds to 25.17)
Actual:
Status:

### Test D-4: Statistics panel totals
Expected: Balance $954.50, Total Income $1030.00, Total Expenses $75.50,
same average/min/max as D-3
Actual:
Status:

### Test D-5: Empty ledger messages
Input: Fresh start, no transactions; open History, then Statistics
Expected: "No transactions recorded yet." and "No transactions available.
Please add a transaction first." respectively — no crash
Actual:
Status:

### Test D-6: Export button
Input: With the sample data, click "Export Full Ledger to CSV"
Expected: "transactions.csv saved with 4 record(s)." — file on disk matches
Actual:
Status:

---

## GROUP E — SPENDING SCORE CALCULATION
(These verify the math by hand. Use a calculator and the formulas below.)

Score = balanceScore (max 60) + distributionScore (max 30) + anomalyScore (max 10)

balanceScore: remaining% >= 80 -> 60 | >= 60 -> 48 | >= 40 -> 36 | >= 20 -> 24 | else 12
distribution: each bucket within 10% of target -> 10 pts, within 20% -> 6,
within 30% -> 3, else 0. Targets: Needs 50%, Wants 30%, Savings 20%.
anomaly: no flagged transaction -> 10, flagged -> 0
Anomaly threshold = MIN(3 x averageExpense, 0.25 x totalIncome)

### Test E-1: Sample data score (from Group D setup)
Hand calculation:
- remaining% = 954.50 / 1030.00 = 92.67% -> balanceScore = 60
- All 3 expenses are NEEDS (Health, Transport, Groceries): needs=100%,
  wants=0%, savings=0% -> deviations 50/30/20 -> all buckets 0 pts
  EXCEPT check: needs deviation = 50 -> 0 pts; wants deviation = 30 -> 3 pts;
  savings deviation = 20 -> 6 pts -> distributionScore = 9
- averageExpense = 25.17; threshold = MIN(75.51, 257.50) = 75.51;
  no expense > 75.51 -> anomalyScore = 10
- TOTAL EXPECTED: 60 + 9 + 10 = 79 -> "Good"
NOTE: The Phase 2 sample output says "92 - Excellent" but that was written
BEFORE the composite scoring redesign. 79/Good is correct for the current
formula. TODO (Jose): update the Phase 2 sample I/O for the final report.
Actual:
Status:

### Test E-2: No income recorded
Input: Fresh start, add only one expense, view Statistics
Expected: Score 0, status "No income recorded"
Actual:
Status:

### Test E-3: Anomaly flag fires
Input: Fresh start. Income $2000 (Financial Aid). Expenses: $20, $25, $30
(any needs categories), then $480 "School supplies run" (School Supplies / Books)
Hand calculation: average = (20+25+30+480)/4 = 138.75; threshold =
MIN(416.25, 500) = 416.25; 480 > 416.25 -> FLAG
Expected: Red warning "Unusual expense detected: School supplies run -
$480.00 on [date]" and score includes anomalyScore = 0
Actual:
Status:

### Test E-4: Balanced 50/30/20 distribution scores full points
Input: Fresh start. Income $1000. Expenses: $50 Rent / Housing (needs),
$30 Entertainment (wants), $20 Savings (savings)
Hand calculation: needs=50%, wants=30%, savings=20% -> all deviations 0 ->
distributionScore = 30. remaining% = 900/1000 = 90% -> 60.
average = 33.33, threshold = MIN(100, 250) = 100; max expense 50 -> no flag -> 10
Expected: Score = 100 - Excellent
Actual:
Status:

### Test E-5: "Other" category excluded from distribution
Input: Same as E-4 plus one $10 expense in category "Other"
Expected: Distribution percentages unchanged (Other not in any bucket);
balance and totals DO include the $10
Actual:
Status:

### Test E-6: Status boundaries
Input: Construct ledgers that land exactly on 80, 60, 40, 20
Expected: 80 -> Excellent, 60 -> Good, 40 -> Fair, 20 -> Needs Improvement
(each boundary belongs to the higher status, per >= comparisons)
Actual:
Status:

### Test E-7: Resource links appear by status
Input: Engineer an "At Risk" score (spend nearly all income, break the
50/30/20 rule, trigger an anomaly)
Expected: Single Stop emergency aid text + link appear. For "Needs
Improvement", TRIO text + link appear instead
Actual:
Status:

### Test E-8: Childcare resources
Input: Any ledger including one expense categorized Family / Childcare
Expected: "Resources for Student Parents @ MDC" + link appear in
Statistics regardless of score
Actual:
Status:

---

## GROUP F — SETTINGS

### Test F-1: Daily reminder toggle off
Input: In Settings, uncheck the reminder box; exit; relaunch; log in
Expected: No reminder dialog appears after login
NOTE: setting is not persistent (D-7) — after relaunch it resets to ON,
so this test verifies within-session behavior only: toggle off, log out
is not possible, so instead verify by unchecking BEFORE the next login
is not possible either. PRACTICAL TEST: uncheck, then verify the checkbox
state persists while navigating panels within the session
Actual:
Status:

### Test F-2: Clear all transactions — confirm path
Input: With data in the ledger, Settings -> Clear All Transactions -> Yes
Expected: "All transactions cleared." History and Statistics show empty
messages; balance resets to $0.00
Actual:
Status:

### Test F-3: Clear all transactions — cancel path
Input: Same but answer No
Expected: Nothing changes; ledger intact
Actual:
Status:

### Test F-4: Clear then exit then relaunch
Input: After F-2, exit the app, relaunch
Expected: CSV was overwritten as empty (header only, or no records);
app starts with empty ledger
Actual:
Status:

---

## GROUP G — STRESS AND EDGE

### Test G-1: Large amounts
Input: Income $999999.99, expense $0.01
Expected: No formatting breakage; balance $999999.98
Actual:
Status:

### Test G-2: Memo at exactly 50 characters
Input: Memo of exactly 50 characters
Expected: Accepted (boundary is inclusive)
Actual:
Status:

### Test G-3: Memo at exactly 1 character
Input: Memo = "x"
Expected: Accepted
Actual:
Status:

### Test G-4: Rapid navigation
Input: Click between all panels repeatedly 20+ times, adding transactions
in between
Expected: Panels always show current data; no visual glitches or crashes
Actual:
Status:

### Test G-5: Amount with more than 2 decimals
Input: Amount = "10.999"
Expected: Accepted and rounded to $11.00 (verify rounding in balance)
Actual:
Status:

### Test G-6: Date on today
Input: Date = today's exact date
Expected: Accepted (future is blocked, today is allowed)
Actual:
Status:

---

## KNOWN GAPS TO DECIDE BEFORE SUBMISSION (from TODOs in code)

1. Date range filter in History (All / Last 60 Days / Custom) — in the
   pseudocode, NOT implemented. Decide: implement or document as planned
   feature. (TODO in buildHistoryPanel)
2. Phase 2 sample output shows score 92/Excellent — predates the composite
   formula. Update sample I/O before the final report. (Test E-1)
3. Anomaly warning shows the LAST flagged transaction when several exceed
   the threshold — decide first/last/largest. (TODO in SpendingScore)
4. All other D-1 through D-12 decisions in the Phase3_Implementation_Tracker.

---

## SIGN-OFF

| Group | Tests | Passed | Failed | Tester | Date |
|-------|-------|--------|--------|--------|------|
| A — Startup/Persistence | 6 | | | | |
| B — Login/Lockout | 5 | | | | |
| C — Validation | 13 | | | | |
| D — History/Statistics | 6 | | | | |
| E — Spending Score | 8 | | | | |
| F — Settings | 4 | | | | |
| G — Stress/Edge | 6 | | | | |
| TOTAL | 48 | | | | |

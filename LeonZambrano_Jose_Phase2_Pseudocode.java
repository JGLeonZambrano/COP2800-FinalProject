// PSEUDOCODE ONLY — not intended for compilation
// Daily Student Expense Tracker with Spending Score
// Phase 2 – Program Flow Pseudocode
// Lead Developer: Jose G. Leon Zambrano
// Last updated: June 28, 2026

// *****
// IMPORTS AND SETUP
// *****

// Import Java Scanner for unified input handling and file reading
// Import javax.swing.* for GUI components
// (JFrame, JPanel, JButton, JLabel, JTextField,
// JComboBox, JTextArea, JScrollPane, JOptionPane)
// Import java.awt.* and java.awt.event.* for layout managers and ActionListeners
// Import java.util.ArrayList for the transaction ledger
// Import java.io.* for File I/O (FileWriter, BufferedWriter, FileReader, BufferedReader)
// Import java.time.LocalDate and java.time.format.DateTimeFormatter for date validation

// *****
// PROGRAM START — main()
// *****

// Create public class ExpenseTrackerApp extending JFrame
// Create ExpenseTracker object
// Create FileManager object with filename "transactions.csv"

// *****
// AUTHENTICATION — LOGIN WITH LOCKOUT
// *****

// badPasswordCounter = 0
// isLocked = false
// lockStartTime = 0

// Do While isLocked == true:
// Get current time >> currentTime
// If (currentTime - lockStartTime) < 180000:   // 180000 ms = 3 minutes
// Display JLabel: "Account locked. Please wait 3 minutes before trying again."
// Display JLabel: "Time remaining: X seconds"
// Disable "Log In" JButton
// Else:
// isLocked = false
// badPasswordCounter = 0
// Enable "Log In" JButton
// Display JLabel: "You may try again."
// End If
// End While

// On "Log In" button click:
// Validate that username and password fields are not empty
// Compare input against stored credentials
// If credentials match:
// badPasswordCounter = 0
// Load saved data
// Switch to Main Menu Panel
// Else:
// badPasswordCounter += 1
// Display JLabel: "Incorrect username or password."
// Display JLabel: "Attempts remaining: " + (5 - badPasswordCounter)
// If badPasswordCounter >= 5:
// isLocked = true
// lockStartTime = current system time in milliseconds
// Display JLabel: "Too many failed attempts. Account locked for 3 minutes."
// Disable "Log In" JButton
// End If
// End If

// Call start() to initialize and display the main application window

// *****
// STARTUP: LOAD SAVED DATA
// (Required — Phase 3 mandates persistent data storage across sessions)
// *****

// Call FileManager.loadLedger()
// If file exists and is not empty:
// Load saved transactions into ExpenseTracker's transactionLedger
// Recalculate balance, totalIncome, and totalExpenses from loaded transactions
// Display "Data loaded successfully." in status label
// Else If file does not exist:
// Display "No saved data found. Starting fresh." in status label
// Else If file is corrupted or unreadable:
// Display "Error loading data. Starting fresh." via JOptionPane
// End If

// On startup, after loading data:
// If dailyReminderEnabled == true:
// Show reminder dialog (JOptionPane) — see Daily Reminder in Settings Panel
// End If
// Show Main Menu Panel

// *****
// start() — BUILD AND DISPLAY MAIN APPLICATION WINDOW
// *****

// METHOD: start(): void
// Set JFrame title: "Daily Student Expense Tracker"
// Set JFrame size (eg 800 x 600)
// Set JFrame default close operation: EXIT_ON_CLOSE
// Set layout manager: CardLayout for panel switching

// Build all panels and add to mainPanel via CardLayout:
// Main Menu Panel
// Add Transaction Panel
// Transaction History Panel
// Statistics and Spending Score Panel
// Settings Panel

// Show Main Menu Panel
// Set JFrame visible = true

// *****
// MAIN MENU PANEL
// *****

// METHOD: displayMenu(): void
// Display JLabel: "Daily Student Expense Tracker" [panel title]
// Display JLabel: "Welcome, " + username + " | " + LocalDate.now() formatted as MM/DD/YYYY
// Example: "Welcome, José! | 06/28/2026"
// If transactionLedger is not empty:
// Display most recent Spending Score as JLabel
// End If

// Display the following JButtons:
// JButton: "1. Add New Transaction"         >> handleUserChoice(1)
// JButton: "2. View Transaction History"    >> handleUserChoice(2)
// JButton: "3. Statistics & Spending Score" >> handleUserChoice(3)
// JButton: "4. Settings"                    >> handleUserChoice(4)
// JButton: "Exit"                           >> handleUserChoice(-1)

// METHOD: handleUserChoice(choice: int): void
// Switch (choice):
// Case 1:  Show Add Transaction Panel
// Case 2:  Show Transaction History Panel
// Case 3:  Show Statistics and Spending Score Panel
// Case 4:  Show Settings Panel
// Case -1: Trigger exit sequence
// End Switch

// *****
// EXIT SEQUENCE
// *****

// Call FileManager.saveLedger(transactionLedger)
// Display JOptionPane: "Transactions saved."
// Display JOptionPane: "Thank you for using the Daily Student Expense Tracker. Goodbye!"
// Call System.exit(0)

// *****
// OPTION 1: ADD NEW TRANSACTION PANEL (Income or Expense)
// *****

// Display JLabel: "Add New Transaction" [panel title]
// Display JButton: "Back to Main Menu"
// On click: switch to Main Menu Panel
// Display JButton: "View Transaction History"
// On click: switch to Transaction History Panel

// Display JLabel: "Transaction Type:"
// Display JComboBox (dropdown): ["Income", "Expense"]
// Stores selected value as transactionType (String)

// Display JLabel: "Amount: $"
// Display JTextField: amountField

// Display JLabel: "Memo (50 characters max):"
// Display JTextField: memoField

// Display JLabel: "Date (MM/DD/YYYY):"
// Display JTextField: dateField

// Display JLabel: "Category:"
// Display JComboBox: categoryDropdown
// Populate dynamically based on transactionType selection:

// If transactionType == "Income":
// categoryDropdown options:
// ["Salary / Wages", "Financial Aid", "Family Support", "Other Income"]

// If transactionType == "Expense":
// categoryDropdown options — grouped by 50/30/20 bucket:
// NEEDS (50%):
// "Rent / Housing"
// "Utilities"
// "Groceries / Food"
// "Transport"
// "Health / Medical"
// "Family / Childcare"
// "Tuition / School Fees"
// WANTS (30%):
// "Entertainment"
// "Dining Out"
// "Shopping / Clothing"
// "Personal Care"
// "Travel / Vacation"
// "Subscriptions"
// SAVINGS / DEBT (20%):
// "Savings"
// "Debt Repayment"
// "School Supplies / Books"
// OTHER:
// "Other"   // excluded from 50/30/20 distribution scoring

// Note: dropdown repopulates automatically when transactionType changes
// Note: JComboBox constrains selection to predefined list — no validation needed
// Stores selected value as transactionCategory (String)

// Display JButton: "Add Transaction"
// On "Add Transaction" button click:

// *****
// DUPLICATE ENTRY CHECK
// *****

// Call isDuplicateEntry(amountField, memoField, dateField, transactionCategory)
// If isDuplicateEntry == true:
// Display JOptionPane error:
// "This transaction already exists in your ledger.
// Please verify your entries and try again."
// Clear all input fields
// Stop — do not proceed
// End If

// *****
// INPUT VALIDATION
// *****

// badData = true
// While badData == true:

// Call isValueValid(amountField.getText())
// If isValueValid == false:
// Display red error JLabel below amountField:
// "Invalid amount. Please enter a positive numeric value."
// badData = true
// Stop and wait for user to correct input
// Else:
// transactionAmount = validated double, rounded to two decimal places
// End If

// Call isMemoValid(memoField.getText())
// If isMemoValid == false:
// Display red error JLabel below memoField:
// "Memo must be between 1 and 50 characters."
// badData = true
// Stop and wait for user to correct input
// Else:
// transactionMemo = validated String value
// End If

// Call isDateValid(dateField.getText())
// If isDateValid == false:
// Display red error JLabel below dateField:
// "Invalid date. Please use MM/DD/YYYY format."
// badData = true
// Stop and wait for user to correct input
// Else:
// transactionDate = validated String value
// End If

// If badData == false:

// All fields valid — create Transaction and add to ledger
// Create new Transaction object:
// id          = transactionLedger.size() + 1
// amount      = transactionAmount
// type        = transactionType   [from JComboBox]
// memo        = transactionMemo
// date        = transactionDate
// category    = transactionCategory [from JComboBox]

// Add Transaction to ExpenseTracker's transactionLedger

// If transactionType == "Income":
// balance += transactionAmount
// totalIncome += transactionAmount
// Else If transactionType == "Expense":
// balance -= transactionAmount
// totalExpenses += transactionAmount
// End If

// Recalculate SpendingScore
// Display JOptionPane: "Transaction added successfully."
// Display updated current balance in JLabel
// Clear all input fields

// End If

// End While

// *****
// OPTION 2: VIEW TRANSACTION HISTORY PANEL
// *****

// Display JLabel: "Transaction History" [panel title]
// Display JButton: "Back to Main Menu"
// On click: switch to Main Menu Panel
// Display JButton: "View Statistics and Spending Score"
// On click: switch to Statistics Panel

// If transactionLedger is empty:
// Display JLabel: "No transactions recorded yet."
// Return to Main Menu Panel
// End If

// *****
// SORT ORDER
// Note: Transactions are stored in entry order (ie order the student typed them in),
// which is not necessarily chronological. History view always displays by date.
// *****

// Call getLedgerSortedByDate()
// Sort transactionLedger by date field (MM/DD/YYYY), most recent to earliest
// Return sorted copy as sortedLedger
// Note: Do not alter the original transactionLedger order

// Display JLabel: "Select Date Range:"
// Display JComboBox: ["All Transactions", "Last 60 Days", "Custom Date Range"]

// If user selects "Last 60 Days":
// startDate = LocalDate.now().minusDays(60)
// endDate = LocalDate.now()
// Apply filter automatically — no input needed

// If user selects "Custom Date Range":
// Display JTextField: startDateField
// Display JTextField: endDateField
// On submit:
// Call isDateValid(startDateField.getText())
// Call isDateValid(endDateField.getText())
// If either isDateValid == false:
// Display red error JLabel: "Invalid date. Please use MM/DD/YYYY format."
// Wait for corrected input
// End If
// If endDate is before startDate:
// Display red error JLabel:
// "End date cannot be before start date. Please try again."
// Wait for corrected input
// End If
// If range exceeds 60 days:
// Display JLabel: "Display range limited to 60 days.
// Use Export to CSV to access full ledger history."
// Clamp endDate to startDate + 60 days
// End If

// If user selects "All Transactions":
// No filter — show full ledger sorted by date
// End If

// Filter sortedLedger by selected date range >> filteredLedger
// If filteredLedger is empty:
// Display JLabel: "No transactions found in that date range."
// Return to History panel options
// End If

// Calculate the following from filteredLedger (expenses only):
// filteredAverage = sum of expense amounts in filteredLedger / expense count
// filteredMinimum = smallest expense amount in filteredLedger
// filteredMaximum = largest expense amount in filteredLedger

// Display summary JLabels:
// "Current Balance: $X"      [always from full ledger, not filtered]
// "Average Expense: $X"      [expenses only, from filteredLedger]
// "Minimum Expense: $X"      [expenses only, from filteredLedger]
// "Maximum Expense: $X"      [expenses only, from filteredLedger]

// Display all transactions in filteredLedger in a JTextArea wrapped in JScrollPane:
// For each Transaction in filteredLedger (most recent to earliest):
// Display: ID | Date | Type | Category | Memo | Amount

// Display JButton: "Export Full Ledger to CSV"
// On "Export Full Ledger to CSV" button click:
// Call FileManager.saveLedger(transactionLedger)   // always full ledger, never filtered
// Display JOptionPane: "Full ledger exported to transactions.csv"

// *****
// OPTION 3: STATISTICS AND SPENDING SCORE PANEL
// *****

// Display JLabel: "Transaction Statistics and Spending Score" [panel title]
// Display JButton: "Back to Main Menu"
// On click: switch to Main Menu Panel
// Display JButton: "View Transaction History"
// On click: switch to History Panel

// If transactionLedger is empty:
// Display JLabel: "No transactions available. Please add a transaction first."
// Return to Main Menu Panel
// End If

// Calculate and display as JLabels:
// "Current Balance:    $X"
// "Total Income:       $X"
// "Total Expenses:     $X"
// "Average Expense:    $X"   [expenses only]
// "Minimum Expense:    $X"   [expenses only]
// "Maximum Expense:    $X"   [expenses only]

// Call SpendingScore(currentBalance, totalIncome, transactionLedger)
// Returns composite score from balanceScore + distributionScore + anomalyScore
// See SpendingScore Methods section for full calculation

// Display JLabel: "Spending Score: X / 100 — [status]"
// Display JLabel: "Remaining: X.XX% of income retained"

// If score <= 20 (status == "At Risk"):
// Display JLabel: "Need help? Student Emergency Aid @ MDC (Single Stop)"
// Display clickable JLabel or JButton linking to:
// https://www.mdc.edu/singlestop/services/student-emergency-aid/
// Display JLabel: "TRIO @ MDC can assist you with any barriers to continue your education"
// Display clickable JLabel or JButton linking to:
// https://www.mdc.edu/trio-eoc/
// End If

// If score >= 21 AND score <= 40 (status == "Needs Improvement"):
// Display JLabel: "TRIO @ MDC can assist you with any barriers to continue your education"
// Display clickable JLabel or JButton linking to:
// https://www.mdc.edu/trio-eoc/
// End If

// Call hasChildcareEntry()
// If hasChildcareEntry == true:
// Display JLabel: "Resources for Student Parents @ MDC"
// Display clickable JLabel or JButton linking to:
// https://www.mdc.edu/student-parents/
// End If

// If anomalyScore == 0 (unexpected expense detected):
// Display JLabel: "Unusual expense detected: [memo] — $[amount] on [date]
// This may be impacting your Spending Score."
// End If

// *****
// OPTION 4: SETTINGS PANEL
// *****

// Display JLabel: "Settings" [panel title]
// Display JButton: "Back to Main Menu"
// On click: switch to Main Menu Panel

// DAILY REMINDER SETTING
// Display JCheckBox: "Show daily expense reminder on startup"
// Default: On
// If checked: on next startup, display reminder dialog before Main Menu
// If unchecked: skip reminder, go directly to Main Menu

// DAILY REMINDER DIALOG (triggered on startup if setting is On)
// Display JOptionPane or modal dialog:
// "Have you recorded today's expenses?"
// JButton: "Yes, let's go!"  >> close dialog, show Main Menu
// JButton: "Not yet"         >> close dialog, show Main Menu
// Display status JLabel on Main Menu:
// "Don't forget to log your expenses today!"

// Other possible settings (to define with group):
// JButton: "Clear all transactions"
// On click: Display JOptionPane confirmation dialog
// If confirmed: clear transactionLedger, reset balance, totalIncome, totalExpenses
// JTextField: "Change export filename"
// JButton + JPasswordField: "Change password"

// *****
// *****
// METHOD-LEVEL PSEUDOCODE
// *****
// *****

// *****
// INPUT VALIDATION METHODS
// Private methods inside ExpenseTrackerApp
// *****

// METHOD: isOptionValid(inputText: String, validOptions: int[]) >> boolean
// badData = true
// Do While badData == true:
// Try:
// Parse inputText as integer >> enteredOption
// badData = false
// If enteredOption is not contained in validOptions array:
// Display red error JLabel:
// "Invalid choice. Please select a valid option."
// badData = true
// End If
// Catch (NumberFormatException):
// Display red error JLabel:
// "Invalid input. Please enter a number."
// End While
// Return enteredOption is in validOptions

// METHOD: isValueValid(inputText: String) >> double
// badData = true
// Do While badData == true:
// Try:
// Parse inputText as double >> enteredAmount
// Round to two decimal places
// badData = false
// If enteredAmount <= 0:
// Display red error JLabel:
// "Amount must be greater than zero. Please try again."
// badData = true
// End If
// Catch (NumberFormatException):
// Display red error JLabel:
// "Invalid input. Please enter a numeric value."
// End While
// Return enteredAmount

// METHOD: isMemoValid(inputText: String) >> boolean
// enteredMemo = inputText.trim()
// If enteredMemo is empty OR enteredMemo.length() > 50:
// Display red error JLabel:
// "Memo must be between 1 and 50 characters. Please try again."
// Return false
// End If
// Return true

// METHOD: isDateValid(inputText: String) >> boolean
// Try:
// Define DateTimeFormatter with pattern "MM/dd/yyyy"
// Parse inputText using formatter >> enteredDate (LocalDate)
// If enteredDate is after LocalDate.now():
// Display red error JLabel:
// "Date cannot be in the future. Please enter a valid date."
// Return false
// End If
// Return true
// Catch (DateTimeParseException):
// Display red error JLabel:
// "Invalid date. Please use MM/DD/YYYY format."
// Return false
// End Try

// Note: isCategoryValid() is not needed as a method.
// The JComboBox dropdown constrains category input to the predefined list.
// No invalid selection is possible, so no validation loop is required.

// *****
// EXPENSETRACKER METHODS
// *****

// METHOD: addIncome(amount: double, memo: String, date: String, category: String): void
// Create new Transaction object with type = "Income"
// Add Transaction to transactionLedger
// balance += amount
// totalIncome += amount

// METHOD: addExpense(amount: double, memo: String, date: String, category: String): void
// Create new Transaction object with type = "Expense"
// Add Transaction to transactionLedger
// balance -= amount
// totalExpenses += amount

// METHOD: isDuplicateEntry(amount: double, memo: String,
//                          date: String, category: String): boolean
// For each Transaction in transactionLedger:
// If transaction.getAmount()    == amount
// AND transaction.getMemo()     == memo
// AND transaction.getDate()     == date
// AND transaction.getCategory() == category:
// Return true    // exact duplicate found — reject entry
// End If
// End For
// Return false           // no duplicate found — entry is safe to add

// METHOD: getLedgerSortedByDate(): ArrayList<Transaction>
// Create sortedLedger as a copy of transactionLedger
// Sort sortedLedger by date field (MM/DD/YYYY), most recent to earliest
// Note: Parse date strings to LocalDate for accurate chronological comparison
// Return sortedLedger
// Note: Original transactionLedger order is never altered

// METHOD: hasChildcareEntry(): boolean
// For each Transaction in transactionLedger:
// If transaction.getCategory() == "Family / Childcare":
// Return true
// End If
// End For
// Return false

// METHOD: getBalance(): double
// Return balance

// METHOD: getTotalIncome(): double
// Return totalIncome

// METHOD: getTotalExpenses(): double
// Return totalExpenses

// METHOD: getTransactionLedger(): ArrayList<Transaction>
// Return transactionLedger

// METHOD: getAverageExpense(): double
// expenseSum = 0.0
// expenseCount = 0
// For each Transaction in transactionLedger:
// If transaction.getType() == "Expense":
// expenseSum += transaction.getAmount()
// expenseCount += 1
// End If
// End For
// If expenseCount == 0:
// Return 0.0
// End If
// Return Math.round((expenseSum / expenseCount) * 100.0) / 100.0

// METHOD: getMinimumExpense(): double
// minimum = Double.MAX_VALUE
// expenseCount = 0
// For each Transaction in transactionLedger:
// If transaction.getType() == "Expense":
// If transaction.getAmount() < minimum:
// minimum = transaction.getAmount()
// End If
// expenseCount += 1
// End If
// End For
// If expenseCount == 0:
// Return 0.0
// End If
// Return minimum

// METHOD: getMaximumExpense(): double
// maximum = 0.0
// expenseCount = 0
// For each Transaction in transactionLedger:
// If transaction.getType() == "Expense":
// If transaction.getAmount() > maximum:
// maximum = transaction.getAmount()
// End If
// expenseCount += 1
// End If
// End For
// If expenseCount == 0:
// Return 0.0
// End If
// Return maximum

// Note: getAverageExpense(), getMinimumExpense(), and getMaximumExpense()
// calculate across expense entries only. Income transactions are excluded.
// This decision was confirmed with the group during class on June 28, 2026.

// METHOD: calculateSpendingScore(): String
// Call SpendingScore(balance, totalIncome, transactionLedger)
// Return SpendingScore.calculateScore()

// *****
// FILEMANAGER METHODS
// Adapted from hw07a / hw07b CSV logic (accepted, Spring 2026)
// *****

// METHOD: saveLedger(transactionLedger: ArrayList<Transaction>): void

// counterTotalRecordsWritten = 0

// Open file for writing
// Try:
// Open FileWriter at filename, wrapped in BufferedWriter >> csvWriter

// Write header row (column titles, comma-separated)
// csvWriter.write("ID,Amount,Type,Memo,Date,Category")
// csvWriter.newLine()

// For each Transaction in transactionLedger:

// Build the CSV record — entries separated by commas
// csvRecord = transaction.getId()
// + "," + transaction.getAmount()
// + "," + transaction.getType()
// + "," + transaction.getMemo()
// + "," + transaction.getDate()
// + "," + transaction.getCategory()

// Write field (record) with appropriate CSV logic
// csvWriter.write(csvRecord)
// csvWriter.newLine()

// Add value to counter
// counterTotalRecordsWritten += 1

// End For

// Close file
// csvWriter.close()

// Display, when writing finishes, final totals
// If counterTotalRecordsWritten > 0:
// Display JOptionPane:
// filename + " saved with " + counterTotalRecordsWritten + " record(s)."
// End If

// Catch (IOException):
// Display JOptionPane error:
// "Error saving file. Please check your storage permissions."
// End Try

// METHOD: loadLedger(): ArrayList<Transaction>

// counterTotalRecordsRead = 0
// loadedLedger = new ArrayList<Transaction>()

// Open file for reading
// Try:
// Open FileReader at filename, wrapped in BufferedReader >> csvReader

// Read and skip header row
// csvReader.readLine()

// Read first record
// csvRecord = csvReader.readLine()

// Do While record is not null (not end of file)
// While csvRecord != null:

// Strip any trailing whitespace or newline characters from record
// csvRecord = csvRecord.trim()

// Slice each field from the record using indexOf(',')
// Adapted from hw07b slicing approach: find comma index, slice around it

// Slice ID: from record[0] to first comma
// idComma = csvRecord.indexOf(',')
// int_ID = Integer.parseInt(csvRecord.substring(0, idComma))

// Slice Amount: from after first comma to second comma
// amountComma = csvRecord.indexOf(',', idComma + 1)
// dbl_Amount = Double.parseDouble(csvRecord.substring(idComma + 1, amountComma))

// Slice Type: from after second comma to third comma
// typeComma = csvRecord.indexOf(',', amountComma + 1)
// str_Type = csvRecord.substring(amountComma + 1, typeComma)

// Slice Memo: from after third comma to fourth comma
// memoComma = csvRecord.indexOf(',', typeComma + 1)
// str_Memo = csvRecord.substring(typeComma + 1, memoComma)

// Slice Date: from after fourth comma to fifth comma
// dateComma = csvRecord.indexOf(',', memoComma + 1)
// str_Date = csvRecord.substring(memoComma + 1, dateComma)

// Slice Category: from after fifth comma to end of record
// str_Category = csvRecord.substring(dateComma + 1)

// Build Transaction object from sliced fields
// loadedTransaction = new Transaction(
// int_ID, dbl_Amount, str_Type,
// str_Memo, str_Date, str_Category)
// loadedLedger.add(loadedTransaction)

// Add value to counter
// counterTotalRecordsRead += 1

// Read next record
// csvRecord = csvReader.readLine()

// End While

// Close file
// csvReader.close()

// Display, when reading finishes, final totals
// If counterTotalRecordsRead > 0:
// Display status label:
// filename + " loaded with " + counterTotalRecordsRead + " record(s)."
// End If

// Catch (FileNotFoundException):
// Display status label: "No saved file found. Starting fresh."
// Catch (IOException):
// Display JOptionPane error: "Error reading file."
// End Try

// Return loadedLedger

// *****
// SPENDINGSCORE METHODS — REVISED CALCULATION
// Composite score out of 100:
//     60% — Balance Health (current balance vs total income)
//     30% — Category Distribution (how closely spending matches 50-30-20 rule)
//     10% — Unexpected Expense Flag (single transaction anomaly detection)
//
// Category distribution uses expenses from last 60 days only,
// consistent with the History display window, to give the score
// a sense of progression. Full ledger available via CSV export.
// *****

// METHOD: SpendingScore(balance: double, totalIncome: double,
//                       transactionLedger: ArrayList<Transaction>) [Constructor]

// If totalIncome == 0:
// this.remainingPercentage = 0.0
// this.score = 0
// this.status = "No income recorded"
// Return
// End If

// *****
// COMPONENT 1: BALANCE HEALTH (60 points max)
// *****

// remainingPercentage = (balance / totalIncome) * 100

// If remainingPercentage >= 80:      balanceScore = 60
// Else If remainingPercentage >= 60: balanceScore = 48
// Else If remainingPercentage >= 40: balanceScore = 36
// Else If remainingPercentage >= 20: balanceScore = 24
// Else:                              balanceScore = 12
// End If

// *****
// COMPONENT 2: CATEGORY DISTRIBUTION — 50/30/20 RULE (30 points max)
// Calculated from last 60 days of expenses only
// *****

// Define category buckets:
// NEEDS_CATEGORIES   = ["Rent / Housing", "Utilities", "Groceries / Food",
//                        "Transport", "Health / Medical", "Family / Childcare",
//                        "Tuition / School Fees"]
// WANTS_CATEGORIES   = ["Entertainment", "Dining Out", "Shopping / Clothing",
//                        "Personal Care", "Travel / Vacation", "Subscriptions"]
// SAVINGS_CATEGORIES = ["Savings", "Debt Repayment", "School Supplies / Books"]

// Filter transactionLedger to last 60 days of expense entries >> recentExpenses

// needsTotal   = sum of amounts in recentExpenses where category is in NEEDS_CATEGORIES
// wantsTotal   = sum of amounts in recentExpenses where category is in WANTS_CATEGORIES
// savingsTotal = sum of amounts in recentExpenses where category is in SAVINGS_CATEGORIES
// totalExpenses = needsTotal + wantsTotal + savingsTotal
// Note: "Other" category expenses are excluded from bucket totals

// If totalExpenses == 0:
// distributionScore = 15   // neutral score if no categorized expenses yet
// Else:
// needsPct   = (needsTotal / totalExpenses) * 100
// wantsPct   = (wantsTotal / totalExpenses) * 100
// savingsPct = (savingsTotal / totalExpenses) * 100

// Needs target: 50%
// needsDeviation = abs(needsPct - 50)
// If needsDeviation <= 10:      needsBucketScore = 10
// Else If needsDeviation <= 20: needsBucketScore = 6
// Else If needsDeviation <= 30: needsBucketScore = 3
// Else:                         needsBucketScore = 0
// End If

// Wants target: 30%
// wantsDeviation = abs(wantsPct - 30)
// If wantsDeviation <= 10:      wantsBucketScore = 10
// Else If wantsDeviation <= 20: wantsBucketScore = 6
// Else If wantsDeviation <= 30: wantsBucketScore = 3
// Else:                         wantsBucketScore = 0
// End If

// Savings target: 20%
// savingsDeviation = abs(savingsPct - 20)
// If savingsDeviation <= 10:      savingsBucketScore = 10
// Else If savingsDeviation <= 20: savingsBucketScore = 6
// Else If savingsDeviation <= 30: savingsBucketScore = 3
// Else:                           savingsBucketScore = 0
// End If

// distributionScore = needsBucketScore + wantsBucketScore + savingsBucketScore
// Max 30 points
// End If

// *****
// COMPONENT 3: UNEXPECTED EXPENSE FLAG (10 points max)
// Context-sensitive — relative to income, not absolute dollar amount
// *****

// Call getAverageExpense() >> averageExpense

// anomalyThresholdByAverage = averageExpense * 3
// anomalyThresholdByIncome  = totalIncome * 0.25
// anomalyThreshold = min(anomalyThresholdByAverage, anomalyThresholdByIncome)
// Use LOWER threshold to be more sensitive to student income levels

// unexpectedFlag = false
// For each Transaction in transactionLedger:
// If transaction.getType() == "Expense":
// If transaction.getAmount() > anomalyThreshold:
// unexpectedFlag = true
// unexpectedTransaction = transaction   // store for display
// End If
// End If
// End For

// If unexpectedFlag == false:
// anomalyScore = 10   // no anomalies — full points
// Else:
// anomalyScore = 0    // anomaly detected
// End If

// *****
// FINAL SCORE ASSEMBLY
// *****

// this.score = balanceScore + distributionScore + anomalyScore
// Range: 0 to 100

// If this.score >= 80:      this.status = "Excellent"
// Else If this.score >= 60: this.status = "Good"
// Else If this.score >= 40: this.status = "Fair"
// Else If this.score >= 20: this.status = "Needs Improvement"
// Else:                     this.status = "At Risk"
// End If

// METHOD: calculateScore(): String
// Assembles balanceScore + distributionScore + anomalyScore
// Sets this.score and this.status
// Return this.score + " – " + this.status

// METHOD: getAverageExpense(): double
// expenseCount = 0
// expenseSum   = 0.0
// For each Transaction in transactionLedger:
// If transaction.getType() == "Expense":
// expenseSum += transaction.getAmount()
// expenseCount += 1
// End If
// End For
// If expenseCount == 0:
// Return 0.0
// End If
// Return Math.round((expenseSum / expenseCount) * 100.0) / 100.0

// METHOD: getStatus(): String
// Return this.status

// METHOD: getRemainingPercentage(): double
// Return this.remainingPercentage

// *****
// *****
// PROTOREADME — METHOD REFERENCE
// One-sentence description of each method in the program
// *****
// *****

// *****
// ExpenseTrackerApp
// *****
// main()                  — Launches the application and initializes the main window.
// start()                 — Builds all GUI panels, loads saved data, and displays the first screen.
// displayMenu()           — Renders the main menu panel with username, today's date, navigation buttons, and current Spending Score.
// handleUserChoice()      — Routes the user to the correct panel based on their menu selection.
// isOptionValid()         — Checks that a menu input is a number matching one of the available options.
// isValueValid()          — Checks that an amount input is a positive number with up to two decimal places.
// isMemoValid()           — Checks that a memo input is between 1 and 50 characters and not empty.
// isDateValid()           — Checks that a date input matches MM/DD/YYYY format, is a real calendar date, and is not in the future.

// *****
// ExpenseTracker
// *****
// addIncome()             — Creates an Income transaction, adds it to the ledger, and updates balance and totalIncome.
// addExpense()            — Creates an Expense transaction, adds it to the ledger, and updates balance and totalExpenses.
// isDuplicateEntry()      — Returns true if an identical transaction (same amount, memo, date, and category) already exists in the ledger.
// getLedgerSortedByDate() — Returns a copy of the ledger sorted chronologically by date, without altering the original entry order.
// hasChildcareEntry()     — Returns true if any transaction in the ledger is categorized as "Family / Childcare".
// getBalance()            — Returns the current balance (totalIncome minus totalExpenses).
// getTotalIncome()        — Returns the running total of all income entries.
// getTotalExpenses()      — Returns the running total of all expense entries.
// getTransactionLedger()  — Returns the full transaction ledger in original entry order.
// getAverageExpense()     — Returns the average amount across all expense entries only, rounded to two decimal places. Public because SpendingScore accesses it directly.
// getMinimumExpense()     — Returns the smallest expense amount in the ledger, excluding income entries.
// getMaximumExpense()     — Returns the largest expense amount in the ledger, excluding income entries.
// calculateSpendingScore()— Delegates to SpendingScore to calculate and return a composite score based on balance health (60%), 50/30/20 category distribution (30%), and unexpected expense detection (10%).

// *****
// FileManager
// *****
// FileManager()           — Constructor: sets the filename used for all save and load operations.
// saveLedger()            — Writes all transactions in the full ledger to a CSV file, one record per line.
// loadLedger()            — Reads a CSV file and reconstructs the transaction ledger for the current session.

// *****
// Transaction
// *****
// Transaction()           — Constructor: creates a new transaction record with all required fields.
// getId()                 — Returns the transaction's unique ID number.
// getAmount()             — Returns the transaction's dollar amount.
// getType()               — Returns whether the transaction is "Income" or "Expense".
// getMemo()               — Returns the user's text description of the transaction.
// getDate()               — Returns the transaction date as a MM/DD/YYYY string.
// getCategory()           — Returns the transaction's category label.

// *****
// SpendingScore
// *****
// SpendingScore()         — Constructor: receives balance, totalIncome, and transactionLedger, calculates all three score components, and assembles the final composite score.
// calculateScore()        — Assembles the final score from balanceScore, distributionScore, and anomalyScore, sets the status label, and returns the formatted result string.
// getAverageExpense()     — Returns the average expense amount used internally for anomaly threshold calculation.
// getStatus()             — Returns the current status label (eg "Good", "At Risk").
// getRemainingPercentage()— Returns the percentage of income still remaining after expenses.
// PSEUDOCODE ONLY — not intended for compilation
// Daily Student Expense Tracker with Spending Score
// Phase 2 – Program Flow Pseudocode
// Lead Developer: Jose G. Leon Zambrano

// IMPORTS AND SETUP
// Import Java Scanner for unified input handling and file reading
// Import javax.swing.* for GUI components
// (JFrame, JPanel, JButton, JLabel, JTextField,
// JComboBox, JTextArea, JScrollPane, JOptionPane)
// Import java.awt.* and java.awt.event.* for layout managers and ActionListeners
// Import java.util.ArrayList for the transaction ledger
// Import java.io.* for File I/O (FileWriter, BufferedWriter, FileReader, BufferedReader)
// Import java.time.LocalDate and java.time.format.DateTimeFormatter for date validation


// PROGRAM START — main()

// Create public class ExpenseTrackerApp extending JFrame
// Create ExpenseTracker object
// Create FileManager object with filename "transactions.csv"

// TODO: Define with group whether to include username/password authentication
// IF group decides to include authentication:
// badPasswordCounter = 0
// Display login panel:
// JTextField for username
// JPasswordField for password
// JButton: "Log In"
// On "Log In" button click:
// Validate that fields are not empty
// Compare input against stored credentials
// If credentials match:
// Load saved data, proceed to Daily Reminder panel
// Else:
// badPasswordCounter += 1
// Display error message via JOptionPane
// TODO: Define with group if login locks after N failed attempts
// End If
// End If

// Call start() to initialize and display the main application window


// STARTUP: LOAD SAVED DATA
// (Required — Phase 3 mandates persistent data storage across sessions)


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


// start() BUILD AND DISPLAY MAIN APPLICATION WINDOW


// METHOD: start(): void
// Set JFrame title: "Daily Student Expense Tracker"
// Set JFrame size (eg 800 x 600)
// Set JFrame default close operation: EXIT_ON_CLOSE
// Set layout manager: CardLayout for panel switching

// Build all panels and add to mainPanel via CardLayout:
// Daily Reminder Panel   [Optional — see below]
// Main Menu Panel
// Add Transaction Panel
// Transaction History Panel
// Statistics and Spending Score Panel
// Settings Panel

// If Daily Reminder is approved:
// Show Daily Reminder Panel first
// Else:
// Show Main Menu Panel directly
// End If

// Set JFrame visible = true


// DAILY REMINDER PANEL
// (Optional: pending Professor Dujour’s approval before implementing)


// TODO: Confirm with Professor whether this feature is OK or not

// If approved:
// Display JLabel: "Have you recorded today's expenses?"
// Display JButton: "Yes, let's go!"
// On click: switch to Main Menu Panel
// Display JButton: "Not yet — remind me later"
// On click:
// Display "Don't forget to log your expenses before you close the app!"
// Switch to Main Menu Panel
// TODO: Define with group if this reminder can be disabled in Settings
// End If
// If not approved or undecided:
// Skip this panel — go directly to Main Menu Panel on startup
// End If


// MAIN MENU PANEL


// METHOD: displayMenu(): void
// Display JLabel: "Daily Student Expense Tracker" [panel title]
// If transactionLedger is not empty:
// Display most recent Spending Score as JLabel
// End If
// TODO: Define with group what else appears here (username, last transaction, date?)

// Display the following JButtons:
// JButton: “1. Add New Transaction” >> handleUserChoice(1)
// JButton: “2. View Transaction History” >> handleUserChoice(2)
// JButton: “3. Statistics & Spending Score” >> handleUserChoice(3)
// JButton: “4. Settings” >>  handleUserChoice(4)
// TODO: Define with group: "5. Daily Expense Reminder" — Settings option or separate?
// TODO: Define with group: "6. Export Transactions to CSV" — own button or inside History?
// JButton: "Exit"                           >> handleUserChoice(-1)

// METHOD: handleUserChoice(choice: int): void
// Switch (choice):
// Case 1:  Show Add Transaction Panel
// Case 2:  Show Transaction History Panel
// Case 3:  Show Statistics and Spending Score Panel
// Case 4:  Show Settings Panel
// Case -1: Trigger exit sequence
// End Switch


// EXIT SEQUENCE

// Call FileManager.saveLedger(transactionLedger)
// Display JOptionPane: "Transactions saved."
// Display JOptionPane: "Thank you for using the Daily Student Expense Tracker. Goodbye!"
// Call System.exit(0)


// OPTION 1: ADD NEW TRANSACTION PANEL (Income or Expense)

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
// Display JComboBox (dropdown):
// If transactionType == "Income":
// ["Salary / Wages", "Financial Aid", "Family Support", "Other Income"]
// If transactionType == "Expense":
// ["Food", "Transport", "Rent", "Utilities",
// "Entertainment", "Health",
// "Family / Childcare",   // TODO: Define with group if this category is included
// "Other"]
// Note: JComboBox constrains category input automatically — no validation loop needed
// Stores selected value as transactionCategory (String)

// Display JButton: "Add Transaction"
// On "Add Transaction" button click:


// DUPLICATE ENTRY CHECK


// Call isDuplicateEntry(amountField, memoField, dateField, transactionCategory)
// If isDuplicateEntry == true:
// Display JOptionPane error:
// "This transaction already exists in your ledger.
// Please verify your entries and try again."
// Clear all input fields
// Stop — do not proceed
// End If


// INPUT VALIDATION


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


// OPTION 2: VIEW TRANSACTION HISTORY PANEL

// Display JLabel: "Transaction History" [panel title]
// Display JButton: "Back to Main Menu"
// On click: switch to Main Menu Panel
// Display JButton: "View Statistics and Spending Score"
// On click: switch to Statistics Panel

// If transactionLedger is empty:
// Display JLabel: "No transactions recorded yet."
// Return to Main Menu Panel
// End If


// SORT ORDER
// Note: Transactions are stored in entry order (ie order the student typed them in), which is not necessarily chronological. History view always displays by date.


// Call getLedgerSortedByDate()
// Sort transactionLedger by date field (MM/DD/YYYY), most recent to earliest
// Return sorted copy as sortedLedger
// Note: Do not alter the original transactionLedger order

// Display JLabel: "Select Date Range:"
// Display JComboBox: ["All Transactions", "Custom Date Range"]

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
// TODO: Define with group if there is a max date range (30 days? 60 days? Unlimited?)
// End If

// Filter sortedLedger by selected date range >> filteredLedger
// If filteredLedger is empty:
// Display JLabel: "No transactions found in that date range."
// Return to History panel options
// End If

// Calculate the following from filteredLedger:
// filteredAverage = sum of all amounts in filteredLedger / filteredLedger.size()
// filteredMinimum = smallest amount in filteredLedger
// filteredMaximum = largest amount in filteredLedger

// Display summary JLabels:
// "Current Balance: $X"          [always from full ledger, not filtered]
// "Average Transaction: $X"      [from filteredLedger]
// "Minimum Transaction: $X"      [from filteredLedger]
// "Maximum Transaction: $X"      [from filteredLedger]

// Display all transactions in filteredLedger in a JTextArea wrapped in JScrollPane:
// For each Transaction in filteredLedger (most recent to earliest):
// Display: ID | Date | Type | Category | Memo | Amount

// Display JButton: "Export to CSV"
// On "Export to CSV" button click:
// Call FileManager.saveLedger(filteredLedger)
// TODO: Define with group — export filtered range only, or full ledger?


// OPTION 3: STATISTICS AND SPENDING SCORE PANEL

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
// "Current Balance:           $X"
// "Total Income:              $X"
// "Total Expenses:            $X"
// "Average Transaction:       $X"
// "Minimum Transaction:       $X"
// "Maximum Transaction:       $X"

// Call SpendingScore(currentBalance, totalIncome)
// remainingPercentage = (currentBalance / totalIncome) * 100
// If remainingPercentage >= 80: score = 100, status = "Excellent"
// Else If remainingPercentage >= 60: score = 80, status = "Good"
// Else If remainingPercentage >= 40: score = 60, status = "Fair"
// Else If remainingPercentage >= 20: score = 40, status = "Needs Improvement"
// Else:                              score = 20,  status = "At Risk"
// End If

// Display JLabel: “Spending Score: X / 100 — [status]”
// Display JLabel: “Remaining: X.XX% of income retained”

// TODO: Define with classmates if this is OK?
// If score <= 20 (status == "At Risk"):
// Display JLabel: "Need help? Student Emergency Aid @ MDC (Single Stop)"
// Display clickable JLabel or JButton linking to:
// https://www.mdc.edu/singlestop/services/student-emergency-aid/
// End If

// TODO: Define with classmates if this is OK?
// Call hasChildcareEntry()
// If hasChildcareEntry == true:
// Display JLabel: "Resources for Student Parents @ MDC"
// Display clickable JLabel or JButton linking to:
// https://www.mdc.edu/student-parents/
// End If


// OPTION 4: SETTINGS PANEL


// Display JLabel: "Settings" [panel title]
// Display JButton: "Back to Main Menu"
// On click: switch to Main Menu Panel

// TODO: Define with group what settings are available. Possible options:
// JCheckBox: "Enable daily reminder" [On/Off] [Optional feature]
// JButton: "Clear all transactions"
// On click: Display JOptionPane confirmation dialog
// If confirmed: clear transactionLedger, reset balance, totalIncome, totalExpenses
// JTextField: "Change export filename"
// JButton + JPasswordField: "Change password" [If basic authentication implemented within app]


// METHOD-LEVEL PSEUDOCODE



// INPUT VALIDATION METHODS
// Private methods inside ExpenseTrackerApp


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
// “Invalid input. Please enter a number.”
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
// “Amount must be greater than zero. Please try again.”
// badData = true
// End If
// Catch (NumberFormatException):
// Display red error JLabel:
// “Invalid input. Please enter a numeric value.”
// End While
// Return enteredAmount

// METHOD: isMemoValid(inputText: String) >> boolean
// enteredMemo = inputText.trim()
// If enteredMemo is empty OR enteredMemo.length() > 50:
// Display red error JLabel:
// “Memo must be between 1 and 50 characters. Please try again.”
// Return false
// End If
// Return true

// METHOD: isDateValid(inputText: String) >> boolean
// Try:
// Define DateTimeFormatter with pattern “MM/dd/yyyy”
// Parse inputText using formatter >> enteredDate (LocalDate)
// TODO: Define with group whether future dates are allowed
// Return true
// Catch (DateTimeParseException):
// Display red error JLabel:
// “Invalid date. Please use MM/DD/YYYY format.”
// Return false
// End Try

// Note: isCategoryValid() is not needed as a method: the JComboBox dropdown constrains category input to the predefined list, so no invalid selection is possible, ie no validation loop is required.


// EXPENSETRACKER METHODS


// METHOD: addIncome(amount: double, memo: String, date: String, category: String): void
// Create new Transaction object with type = “Income”
// Add Transaction to transactionLedger
// balance += amount
// totalIncome += amount

// METHOD: addExpense(amount: double, memo: String, date: String, category: String): void
// Create new Transaction object with type = “Expense”
// Add Transaction to transactionLedger
// balance -= amount
// totalExpenses += amount

// METHOD: isDuplicateEntry(amount: double, memo: String,
//                          date: String, category: String): boolean
// For each Transaction in transactionLedger:
// If transaction.getAmount() == amount
// && transaction.getMemo() == memo
// && transaction.getDate() == date
// && transaction.getCategory() == category:
// Return true
// IF an exact duplicate is found, reject entry
// End If
// End For
// Return false	// No duplicate found so entry is safe to add

// METHOD: getLedgerSortedByDate(): ArrayList<Transaction>
// Create sortedLedger as a copy of transactionLedger
// Sort sortedLedger by date field (MM/DD/YYYY), most recent to earliest
// Note: Parse date strings to LocalDate for accurate chronological comparison
// Return sortedLedger
// Note: Original transactionLedger order is never altered

// METHOD: hasChildcareEntry(): Boolean (TO BE DEFINED WITH TEAM)
// For each Transaction in transactionLedger:
// If transaction.getCategory() == “Family / Childcare”:
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

// METHOD: getAverageTransaction(): double
// If transactionLedger is empty:
// Return 0.0
// End If
// sum = 0.0
// For each Transaction in transactionLedger:
// sum += transaction.getAmount()
// End For
// Return Math.round((sum / transactionLedger.size()) * 100.0) / 100.0

// METHOD: getMinimumTransaction(): double
// If transactionLedger is empty:
// Return 0.0
// End If
// minimum = transactionLedger.get(0).getAmount()
// For each Transaction in transactionLedger:
// If transaction.getAmount() < minimum:
// minimum = transaction.getAmount()
// End If
// End For
// Return minimum

// METHOD: getMaximumTransaction(): double
// If transactionLedger is empty:
// Return 0.0
// End If
// maximum = transactionLedger.get(0).getAmount()
// For each Transaction in transactionLedger:
// If transaction.getAmount() > maximum:
// maximum = transaction.getAmount()
// End If
// End For
// Return maximum

// TODO: Define with group whether getAverageTransaction(), getMinimumTransaction(), and getMaximumTransaction() calculate across ALL transactions (income + expenses), expenses only, or are user-selectable by type filter.
// Current Sample I/O assumes expenses only for these three methods. If income entries are included, the displayed values will differ significantly from expense-only calculations and should be labeled accordingly.

// METHOD: calculateSpendingScore(): String
// If totalIncome == 0:
// Return "No income recorded. Cannot calculate Spending Score."
// End If
// remainingPercentage = (balance / totalIncome) * 100
// If remainingPercentage >= 80:Return "100 – Excellent"
// Else If remainingPercentage >= 60: Return "80 – Good"
// Else If remainingPercentage >= 40: Return "60 – Fair"
// Else If remainingPercentage >= 20: Return "40 – Needs Improvement"
// Else: Return "20 – At Risk"
// End If


// FILEMANAGER METHODS

// METHOD: saveLedger(transactionLedger: ArrayList<Transaction>): void

// counterTotalRecordsWritten = 0

// Open file for writing
// Try:
// Open FileWriter at filename, wrapped in BufferedWriter >> csvWriter

// Write header row (column titles, comma-separated)
// csvWriter.write("ID,Amount,Type,Memo,Date,Category")
// csvWriter.newLine()

// Do While there are transactions to write
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
// “Error saving file. Please check your storage permissions.”
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


// SPENDINGSCORE METHODS
// METHOD: SpendingScore(balance: double, totalIncome: double) [Constructor]
// If totalIncome == 0:
// this.remainingPercentage = 0.0
// Else:
// this.remainingPercentage = (balance / totalIncome) * 100
// End If
// Call calculateScore()

// METHOD: calculateScore(): String
// If remainingPercentage >= 80:      this.score = 100, this.status = "Excellent"
// Else If remainingPercentage >= 60: this.score = 80,  this.status = "Good"
// Else If remainingPercentage >= 40: this.score = 60,  this.status = "Fair"
// Else If remainingPercentage >= 20: this.score = 40,  this.status = "Needs Improvement"
// Else:                              this.score = 20,  this.status = "At Risk"
// End If
// Return this.score + " – " + this.status

// METHOD: getStatus(): String
// Return this.status

// METHOD: getRemainingPercentage(): double
// Return this.remainingPercentage

// PROTOREADME — METHOD REFERENCE
// One-sentence description of each method in the program

// ExpenseTrackerApp

// main(): Launches the application and initializes the main window.
// start():Builds all GUI panels, loads saved data, and displays the first screen.
// displayMenu(): Renders the main menu panel with navigation buttons and current Spending Score
// handleUserChoice() Routes the user to the correct panel based on their menu selection.
// displayDailyReminder(): [Optional] Displays a prompt asking if the user has logged today's expenses.
// isOptionValid(): Checks that a menu input is a number matching one of the available options.
// isValueValid() : Checks that an amount input is a positive number with up to two decimal places.
// isMemoValid(): Checks that a memo input is between 1 and 50 characters and not empty.
// isDateValid(): Checks that a date input matches MM/DD/YYYY format and is a real calendar date.


// ExpenseTracker

// addIncome(): Creates an Income transaction, adds it to the ledger, and updates balance and totalIncome.
// addExpense(): Creates an Expense transaction, adds it to the ledger, and updates balance and totalExpenses.
// isDuplicateEntry(): Returns true if an identical transaction (same amount, memo, date, and category) already exists in the ledger.
// getLedgerSortedByDate(): Returns a copy of the ledger sorted chronologically by date, without altering the original entry order.
// hasChildcareEntry(): Returns true if any transaction in the ledger is categorized as "Family / Childcare".
// getBalance(: Returns the current balance (totalIncome minus totalExpenses).
// getTotalIncome(): Returns the running total of all income entries.
// getTotalExpenses(): Returns the running total of all expense entries.
// getTransactionLedger(): Returns the full transaction ledger in original entry order.
// getAverageTransaction(): Returns the average amount across all transactions, rounded to two decimal places.
// getMinimumTransaction(): Returns the smallest transaction amount in the ledger.
// getMaximumTransaction():  Returns the largest transaction amount in the ledger.
// calculateSpendingScore(): Calculates and returns the Spending Score label and status based on remaining balance percentage.


// FileManager

// FileManager() : Constructor: sets the filename used for all save and load operations.
// saveLedger(): Writes all transactions in the ledger to a CSV file, one record per line.
// loadLedger(): Reads a CSV file and reconstructs the transaction ledger for the current session.


// Transaction

// Transaction(): Constructor: creates a new transaction record with all required fields.
// getId(): Returns the transaction's unique ID number.
// getAmount(): Returns the transaction's dollar amount.
// getType(): Returns whether the transaction is "Income" or "Expense".
// getMemo(): Returns the user's text description of the transaction.
// getDate(): Returns the transaction date as a MM/DD/YYYY string.
// getCategory(): Returns the transaction's category label.


// SpendingScore

// SpendingScore(): Constructor: calculates remainingPercentage from balance and totalIncome and calls calculateScore().
// calculateScore(): Sets score and status based on remainingPercentage thresholds and returns the formatted result.
// getStatus(): Returns the current status label (eg "Good", "At Risk").
// getRemainingPercentage(): Returns the percentage of income still remaining after expenses.


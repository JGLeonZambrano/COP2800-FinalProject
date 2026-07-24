# Daily Student Expense Tracker with Spending Score
## Team Name: "Team Start-Up"

## Project Overview

The **Daily Student Expense Tracker with Spending Score** is a Java Swing desktop application developed for the COP2800 Final Team Project. The program helps students record income and expenses, review their transaction history, calculate financial statistics, and evaluate their spending habits through a custom Spending Score.

The application also provides links to Miami Dade College support resources when certain financial conditions are detected.

## Team Members

- Jose G. Leon Zambrano — Lead Developer
- Manuel Salazar
- Diego Cabrera
- Julien Morales

## Main Features

- Secure login using a username and password
- Five-attempt login limit with a three-minute lockout
- Persistent password and reminder settings
- Daily expense reminder
- Add income and expense transactions
- Dynamic category selection based on transaction type
- Validation for amount, memo, and date fields
- Exact duplicate transaction detection
- Transaction history sorted from newest to oldest
- History filters: All Transactions, Last 60 Days, and Custom Date Range
- Summary statistics for expenses: average, minimum, and maximum
- Total income, total expenses, and current balance
- Composite Spending Score based on balance health, 50/30/20 spending distribution, and unusual expense detection
- MDC Single Stop, TRIO, and Student Parents resource links
- CSV export with a dated filename
- Automatic save and reload of transaction data
- Clear-all-transactions option
- Miami Dade College logo on the login screen

## Java Concepts Used

The project applies several Java programming concepts:

- Object-oriented programming
- Classes and objects
- Encapsulation
- Constructors
- Inheritance through `JFrame`
- `ArrayList` collections
- Loops and conditional statements
- Methods and modular program design
- Exception handling
- File input and output
- Java Swing GUI components
- Event-driven programming with action listeners
- `CardLayout` panel navigation
- `LocalDate` and `DateTimeFormatter`
- Sorting with `Collections` and `Comparator`

## Project Classes

The single-file submission contains five classes:

1. **ExpenseTrackerApp** — launches the program, builds the GUI, handles navigation, login, settings, and validation.
2. **Transaction** — stores the information for one income or expense entry.
3. **ExpenseTracker** — manages the transaction ledger and calculates totals and statistics.
4. **SpendingScore** — calculates the composite score and identifies unusual expenses.
5. **FileManager** — saves and loads transaction data using CSV files.

## Requirements

To run the application, the computer must have:

- Java Development Kit (JDK) 17 or later
- A Java-compatible IDE, such as IntelliJ IDEA or Visual Studio Code, or access to a command-line terminal

## Required Project Files

Keep the following files in the same project folder:

- `ExpenseTrackerApp.java`
- `mdc_logo.png`

The following files are created automatically by the application:

- `transactions.csv`
- `settings.txt`
- `TransactionLedger_student_MM-DD-YYYY.csv` when the user exports the ledger

`transactions.csv` stores the saved transaction history.  
`settings.txt` stores the current password and daily-reminder preference.

## How to Run the Program

### Using IntelliJ IDEA or Visual Studio Code

1. Open the project folder.
2. Confirm that `ExpenseTrackerApp.java` and `mdc_logo.png` are in the same folder.
3. Open `ExpenseTrackerApp.java`.
4. Run the `main()` method.
5. Wait for the login window to appear.

### Using the Command Line

Open the terminal in the project folder itself: the program loads mdc_logo.png and transactions.csv by relative path; and then enter:

```bash
javac ExpenseTrackerApp.java
java ExpenseTrackerApp
```

## Default Login

Use the following credentials on the first launch:

```text
Username: student
Password: password123
```

The password can be changed from the **Settings** panel. After it is changed, the new password is saved in `settings.txt` and remains active after the application is closed and reopened.

Deleting `settings.txt` restores the built-in default password and reminder setting.

## How to Use the Program

### 1. Log In

Enter the username and password, then select **Log In**.

After five incorrect attempts, the login button is disabled for three minutes.

### 2. Add a Transaction

Select **Add New Transaction** from the main menu.

Enter:

- Transaction type
- Amount
- Memo
- Date in `MM/DD/YYYY` format
- Category

The amount must be greater than zero. The memo must contain between 1 and 50 characters and cannot contain commas. Future dates are not accepted.

### 3. View Transaction History

Select **View Transaction History** to review saved transactions.

The history is sorted from the most recent date to the oldest date. The user can display all transactions, the last 60 days, or a custom date range.

The current balance always reflects the complete ledger. Average, minimum, and maximum expense values are calculated from the visible filtered records.

### 4. Export the Ledger

From the Transaction History panel, select **Export Full Ledger to CSV**.

The application creates a file using this format:

```text
TransactionLedger_student_MM-DD-YYYY.csv
```

The export always includes the full ledger, even when a date filter is active.

### 5. View Statistics and Spending Score

Select **Statistics & Spending Score** to view:

- Current balance
- Total income
- Total expenses
- Average expense
- Minimum expense
- Maximum expense
- Spending Score
- Percentage of income remaining

The score is calculated from:

- 60% balance health
- 30% spending distribution
- 10% unusual expense detection

Possible score statuses are:

- Excellent
- Good
- Fair
- Needs Improvement
- At Risk
- No income recorded

### 6. Use Student Resource Links

Depending on the score or recorded categories, the Statistics panel may display links to:

- MDC Single Stop Emergency Aid
- TRIO @ MDC
- MDC Student Parents Resources

Selecting a resource button opens the corresponding webpage in the computer's default browser.

### 7. Change Settings

The Settings panel allows the user to:

- Enable or disable the daily reminder
- Change the password
- Clear all transactions

Password and reminder changes are saved automatically in `settings.txt`.

### 8. Exit the Program

Return to the main menu and select **Exit**, or close the window using the **X** button.

The application saves the complete ledger to `transactions.csv` before closing.

## Testing Overview

Testing was performed by running the live application rather than relying only on static code review.

### Testing Files

- **Phase3_Smoke_Test_Results.md** — five end-to-end smoke tests covering launch, login, transaction entry, history and statistics, export, and persistence. All five tests passed.
- **Phase3_Testing_Plan.md** — 48 base test cases covering startup, persistence, login, validation, history, statistics, spending score, settings, and edge cases.
- **Phase3_Testing_Plan_Addendum.md** — 23 additional test cases covering the MDC logo, date filters, password persistence, dated exports, student resource links, anomaly selection, and general exception handling.
- **ExpenseTrackerApp_Test_Julien_Morales.docx** — an independent team test run that identified issues with settings persistence and very large numerical values.
- **Settings Persistence (Phase 3 Sanity Check).md** — six regression tests confirming that password and reminder settings persist correctly and that the changes did not interfere with transaction storage.
- **Diego_Cabrera_COP_2800_Final_Project_Screenshots.docx** — 16 captured screenshots documenting login, lockout, password change, input validation, category behavior, clear transactions, and save-on-exit.
- **Manuel_Salazar_Testing_Screenshots.docx** — 18 captured screenshots documenting transaction history, summary statistics, export, date filters, spending score outcomes, anomaly detection, and student resource displays.

Together, the master plan and addendum contain **71 test cases**.

## Testing Status

The smoke test and settings-persistence regression tests passed. Issues found during team testing involving password persistence and the daily reminder were corrected using `settings.txt`.

The application was also tested for:

- Valid and invalid login attempts
- Transaction validation
- Duplicate detection
- CSV save and reload
- Transaction sorting and filtering
- Spending Score calculations
- Unusual expense detection
- Student resource buttons
- Large and small transaction amounts
- Export filename and file contents

## Known Limitation

Java `double` values may lose precision when extremely large monetary values are entered. The application is intended for normal student budgeting amounts and formats displayed values to two decimal places.

## Notes

- Do not place commas inside transaction memos because the CSV reader uses commas to separate fields.
- Do not manually edit `transactions.csv` unless performing a test.
- Keep `mdc_logo.png` in the project folder so the logo appears on the login screen.
- If the saved password is unknown, close the application and delete `settings.txt` to restore the default credentials.

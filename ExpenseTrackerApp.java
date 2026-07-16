// ExpenseTrackerApp.java (single-file submission version)
// Daily Student Expense Tracker with Spending Score
// COP2800 Java Programming (Final Team Project, Phase 3)
// Lead Developer: Jose G. Leon Zambrano
// Team: Manuel Salazar, Diego Cabrera, Julien Morales
//
// FILE STRUCTURE: this single file contains all five classes of the project:
//	1. ExpenseTrackerApp: (public: entry point, GUI window, panel routing, validation)
//	2. Transaction: (data object: one income or expense entry)
//	3. ExpenseTracker: (core logic: ledger management and calculations)
//	4. SpendingScore: (composite score: 60% balance, 30% distribution, 10% anomaly)
//	5. FileManager: (CSV save and load)
// In Java, only ONE class per file may be declared public, and the file must be named after it. That is why
// ExpenseTrackerApp keeps the public keyword and the other four classes do not. Their behavior is identical to the
// separate-file version.
//
// TODO (Jose): FINAL UML AND DOCUMENTATION UPDATE (do this once, at the very end).
// The implementation deviates from the Phase 2 UML in the following ways, all agreed
// during Phase 3. Fold ALL of these into the final UML diagram and the Phase 4 report:
//	1. SpendingScore constructor takes 3 arguments (adds transactionLedger)
//	2. SpendingScore gains attribute unexpectedTransaction and getter getUnexpectedTransaction()
//	3. ExpenseTracker.calculateSpendingScore() returns SpendingScore (was String)
//	4. ExpenseTracker gains method loadFromLedger(ArrayList) for startup recalculation
//	5. FileManager gains method saveLedgerToFile(ArrayList, String) for named exports
//	6. ExpenseTrackerApp: STORED_PASSWORD is now mutable storedPassword (Change Password feature)
//	7. isMemoValid() also rejects commas (protects the CSV indexOf() slicing on reload)
//	8. All file and parse operations have a general backup catchall catch (Exception) block
//	9. History panel gains the date range filter (All / Last 60 Days / Custom, 60-day clamp)
//	10. Phase 2 sample output score (92/Excellent) predates the composite formula; recalculate

// IMPORTS
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

// CLASS 1: ExpenseTrackerApp (extends JFrame)
// Entry point, GUI window, panel routing, and all input validation methods.
public class ExpenseTrackerApp extends JFrame {

	// ATTRIBUTES
	private ExpenseTracker transactionLedger;
	private FileManager fileManager;
	private CardLayout cardLayout;
	private JPanel mainPanel;
	private String username;
	private LocalDate currentDate;

	// Authentication credentials. The username is fixed; the password starts at the default below but can be changed in
	// Settings during the session.
	private static final String STORED_USERNAME = "student";
	// Not static: loadSettings() writes the saved password into this instance field on
	// startup, so a changed password persists across sessions instead of resetting.
	private String storedPassword = "password123";

	// Daily reminder setting. Persisted to settings.txt, so the toggle survives relaunch
	private boolean dailyReminderEnabled = true;

	// Category options for the Add Transaction dropdowns.
	private static final String[] INCOME_CATEGORIES = {
			"Salary / Wages", "Financial Aid", "Family Support", "Other Income"
	};

	private static final String[] EXPENSE_CATEGORIES = {
			// NEEDS (50%)
			"Rent / Housing", "Utilities", "Groceries / Food", "Transport",
			"Health / Medical", "Family / Childcare", "Tuition / School Fees",
			// WANTS (30%)
			"Entertainment", "Dining Out", "Shopping / Clothing",
			"Personal Care", "Travel / Vacation", "Subscriptions",
			// SAVINGS / DEBT (20%)
			"Savings", "Debt Repayment", "School Supplies / Books",
			// OTHER (excluded from 50/30/20 scoring)
			"Other"
	};

	// CONSTRUCTOR
	public ExpenseTrackerApp() {
		this.transactionLedger = new ExpenseTracker();
		this.fileManager = new FileManager("transactions.csv");
		this.currentDate = LocalDate.now();
	}

	// METHOD: main (Entry point)
	public static void main(String[] args) {
		ExpenseTrackerApp application = new ExpenseTrackerApp();
		application.start();
	}

	// METHOD: start
	// Builds all GUI panels, loads saved data, and displays the first screen.
	public void start() {
		setTitle("Daily Student Expense Tracker");
		setSize(800, 600);
		setLocationRelativeTo(null);

		// Closing the window with the X button runs the same exit sequence as the Exit button, so the ledger is
		// ALWAYS saved. EXIT_ON_CLOSE would skip the save and lose unsaved transactions, breaking the persistent
		// data storage requirement.
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				handleUserChoice(-1);
			}
		});

		// Set up CardLayout for panel switching, then build and add all panels.
		cardLayout = new CardLayout();
		mainPanel = new JPanel(cardLayout);

		mainPanel.add(buildLoginPanel(), "login");
		mainPanel.add(buildMainMenuPanel(), "menu");
		mainPanel.add(buildAddTransactionPanel(), "addTransaction");
		mainPanel.add(buildHistoryPanel(), "history");
		mainPanel.add(buildStatisticsPanel(), "statistics");
		mainPanel.add(buildSettingsPanel(), "settings");

		add(mainPanel);

		// Load saved settings (password, daily reminder) before showing the login screen
		loadSettings();

		// Load saved data from the CSV file and recalculate all running totals
		ArrayList<Transaction> loadedLedger = fileManager.loadLedger();
		if (!loadedLedger.isEmpty()) {
			transactionLedger.loadFromLedger(loadedLedger);
		}

		// Show the login panel first.
		cardLayout.show(mainPanel, "login");
		setVisible(true);
	}

	// METHOD: handleUserChoice
	// Routes the user to the correct panel based on their menu selection. Data panels (history, statistics, menu) are
	// rebuilt on each visit so they always show the current ledger contents.
	// TODO (Jose): Reconsider rebuilding panels (old panel objects linger in memory until garbage collection
	//  (NOTE: negligible at this scale, but worth revisiting).
	public void handleUserChoice(int choice) {
		switch (choice) {
			case 1:
				mainPanel.add(buildAddTransactionPanel(), "addTransaction");
				cardLayout.show(mainPanel, "addTransaction");
				break;
			case 2:
				mainPanel.add(buildHistoryPanel(), "history");
				cardLayout.show(mainPanel, "history");
				break;
			case 3:
				mainPanel.add(buildStatisticsPanel(), "statistics");
				cardLayout.show(mainPanel, "statistics");
				break;
			case 4:
				cardLayout.show(mainPanel, "settings");
				break;
			case -1:
				// Exit sequence: save the ledger, thank the user, close the application.
				fileManager.saveLedger(transactionLedger.getTransactionLedger());
				JOptionPane.showMessageDialog(this,
						"Thank you for using the Daily Student Expense Tracker. Goodbye!");
				System.exit(0);
				break;
		}
	}

	// METHOD: displayMenu
	// Rebuilds and shows the main menu panel with current data.
	public void displayMenu() {
		mainPanel.add(buildMainMenuPanel(), "menu");
		cardLayout.show(mainPanel, "menu");
	}

	// PANEL BUILDER: LOGIN PANEL
	// Login with a 5-attempt lockout and a 3-minute wait, per the Phase 2 pseudocode.
	private JPanel buildLoginPanel() {
		JPanel loginPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridConstraints = new GridBagConstraints();
		gridConstraints.insets = new Insets(5, 5, 5, 5);
		gridConstraints.fill = GridBagConstraints.HORIZONTAL;

		// MDC logo displayed above the application title
		ImageIcon mdcLogo = new ImageIcon("mdc_logo.png");
		JLabel logoLabel = new JLabel(mdcLogo, SwingConstants.CENTER);
		gridConstraints.gridx = 0; gridConstraints.gridy = 0; gridConstraints.gridwidth = 2;
		loginPanel.add(logoLabel, gridConstraints);

		JLabel titleLabel = new JLabel("Daily Student Expense Tracker", SwingConstants.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
		gridConstraints.gridx = 0; gridConstraints.gridy = 1; gridConstraints.gridwidth = 2;
		loginPanel.add(titleLabel, gridConstraints);

		JLabel subtitleLabel = new JLabel("Please log in to continue", SwingConstants.CENTER);
		gridConstraints.gridy = 2;
		loginPanel.add(subtitleLabel, gridConstraints);

		gridConstraints.gridwidth = 1;

		JLabel usernameLabel = new JLabel("Username:");
		gridConstraints.gridx = 0; gridConstraints.gridy = 3;
		loginPanel.add(usernameLabel, gridConstraints);

		JTextField usernameField = new JTextField(15);
		gridConstraints.gridx = 1; gridConstraints.gridy = 3;
		loginPanel.add(usernameField, gridConstraints);

		JLabel passwordLabel = new JLabel("Password:");
		gridConstraints.gridx = 0; gridConstraints.gridy = 4;
		loginPanel.add(passwordLabel, gridConstraints);

		JPasswordField passwordField = new JPasswordField(15);
		gridConstraints.gridx = 1; gridConstraints.gridy = 4;
		loginPanel.add(passwordField, gridConstraints);

		JLabel loginErrorLabel = new JLabel(" ");
		loginErrorLabel.setForeground(Color.RED);
		gridConstraints.gridx = 0; gridConstraints.gridy = 5; gridConstraints.gridwidth = 2;
		loginPanel.add(loginErrorLabel, gridConstraints);

		JButton loginButton = new JButton("Log In");
		gridConstraints.gridy = 6;
		loginPanel.add(loginButton, gridConstraints);

		// Failed-attempt tracking for the lockout: badPasswordCounter counts wrong passwords, and at 5 the login locks
		// for 3 minutes. These are one-element arrays (not plain variables) because Java only lets an anonymous
		// inner class read outer local variables that are effectively final: the array reference stays final while its
		// contents can still change. Standard Swing workaround found online
		// TODO: Find if this is acceptable, for assignment and/or in general
		final int[] badPasswordCounter = {0};
		final long[] lockStartTime = {0};
		final boolean[] isLocked = {false};

		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Check the lockout status before processing the attempt.
				if (isLocked[0]) {
					long elapsedMilliseconds = System.currentTimeMillis() - lockStartTime[0];
					if (elapsedMilliseconds < 180000) {
						// 180000 milliseconds = 3 minutes
						int secondsRemaining = (int) ((180000 - elapsedMilliseconds) / 1000);
						loginErrorLabel.setText("Account locked. Wait " + secondsRemaining + " seconds.");
						return;
					} else {
						// The lockout period has expired: reset and allow attempts again.
						isLocked[0] = false;
						badPasswordCounter[0] = 0;
						loginButton.setEnabled(true);
						loginErrorLabel.setText("You may try again.");
					}
				}

				String enteredUsername = usernameField.getText().trim();
				String enteredPassword = new String(passwordField.getPassword());

				// Validate that both fields are filled in before comparing credentials.
				if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
					loginErrorLabel.setText("Please enter both username and password.");
					return;
				}

				// Compare input against stored credentials.
				if (enteredUsername.equals(STORED_USERNAME) && enteredPassword.equals(storedPassword)) {
					// Successful login: reset the counter and record the username.
					badPasswordCounter[0] = 0;
					username = enteredUsername;

					// Show the daily reminder dialog if the setting is enabled.
					if (dailyReminderEnabled) {
						JOptionPane.showOptionDialog(
								ExpenseTrackerApp.this,
								"Have you recorded today's expenses?",
								"Daily Reminder",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								null,
								new String[]{"Yes, let's go!", "Not yet"},
								"Yes, let's go!");
						// Either answer proceeds to the main menu.
					}

					displayMenu();
				} else {
					// Failed login attempt: count it and lock out after the fifth one.
					badPasswordCounter[0] += 1;
					int attemptsRemaining = 5 - badPasswordCounter[0];

					if (badPasswordCounter[0] >= 5) {
						isLocked[0] = true;
						lockStartTime[0] = System.currentTimeMillis();
						loginErrorLabel.setText("Too many failed attempts. Locked for 3 minutes.");
						loginButton.setEnabled(false);

						// A Swing Timer re-enables the button after the 3 minutes pass.
						Timer lockoutTimer = new Timer(180000, new ActionListener() {
							public void actionPerformed(ActionEvent timerEvent) {
								isLocked[0] = false;
								badPasswordCounter[0] = 0;
								loginButton.setEnabled(true);
								loginErrorLabel.setText("You may try again.");
							}
						});
						lockoutTimer.setRepeats(false);
						lockoutTimer.start();
					} else {
						loginErrorLabel.setText(
								"Incorrect credentials. Attempts remaining: " + attemptsRemaining);
					}
				}
			}
		});

		return loginPanel;
	}

	// PANEL BUILDER: MAIN MENU PANEL
	private JPanel buildMainMenuPanel() {
		JPanel menuPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridConstraints = new GridBagConstraints();
		gridConstraints.insets = new Insets(8, 8, 8, 8);
		gridConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridConstraints.gridx = 0;

		JLabel titleLabel = new JLabel("Daily Student Expense Tracker", SwingConstants.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
		gridConstraints.gridy = 0;
		menuPanel.add(titleLabel, gridConstraints);

		// Username and today's date, formatted MM/dd/yyyy, per the pseudocode header.
		DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		String welcomeText = "Welcome, " + (username != null ? username : "Student")
				+ "! | " + currentDate.format(displayFormatter);
		JLabel welcomeLabel = new JLabel(welcomeText, SwingConstants.CENTER);
		gridConstraints.gridy = 1;
		menuPanel.add(welcomeLabel, gridConstraints);

		// Current Spending Score, shown only if the ledger has entries.
		if (!transactionLedger.getTransactionLedger().isEmpty()) {
			SpendingScore currentSpendingScore = transactionLedger.calculateSpendingScore();
			JLabel scoreLabel = new JLabel("Current Spending Score: "
					+ currentSpendingScore.calculateScore(), SwingConstants.CENTER);
			scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
			gridConstraints.gridy = 2;
			menuPanel.add(scoreLabel, gridConstraints);
		}

		JButton addTransactionButton = new JButton("1. Add New Transaction");
		gridConstraints.gridy = 3;
		menuPanel.add(addTransactionButton, gridConstraints);
		addTransactionButton.addActionListener(event -> handleUserChoice(1));

		JButton viewHistoryButton = new JButton("2. View Transaction History");
		gridConstraints.gridy = 4;
		menuPanel.add(viewHistoryButton, gridConstraints);
		viewHistoryButton.addActionListener(event -> handleUserChoice(2));

		JButton viewStatisticsButton = new JButton("3. Statistics & Spending Score");
		gridConstraints.gridy = 5;
		menuPanel.add(viewStatisticsButton, gridConstraints);
		viewStatisticsButton.addActionListener(event -> handleUserChoice(3));

		JButton settingsButton = new JButton("4. Settings");
		gridConstraints.gridy = 6;
		menuPanel.add(settingsButton, gridConstraints);
		settingsButton.addActionListener(event -> handleUserChoice(4));

		JButton exitButton = new JButton("Exit");
		gridConstraints.gridy = 7;
		menuPanel.add(exitButton, gridConstraints);
		exitButton.addActionListener(event -> handleUserChoice(-1));

		return menuPanel;
	}

	// PANEL BUILDER: ADD TRANSACTION PANEL
	private JPanel buildAddTransactionPanel() {
		JPanel addPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridConstraints = new GridBagConstraints();
		gridConstraints.insets = new Insets(5, 5, 5, 5);
		gridConstraints.fill = GridBagConstraints.HORIZONTAL;

		JLabel titleLabel = new JLabel("Add New Transaction", SwingConstants.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		gridConstraints.gridx = 0; gridConstraints.gridy = 0; gridConstraints.gridwidth = 2;
		addPanel.add(titleLabel, gridConstraints);

		gridConstraints.gridwidth = 1;

		JLabel typeLabel = new JLabel("Transaction Type:");
		gridConstraints.gridx = 0; gridConstraints.gridy = 1;
		addPanel.add(typeLabel, gridConstraints);

		JComboBox<String> typeDropdown = new JComboBox<>(new String[]{"Income", "Expense"});
		// Default to Expense since students record expenses more often than income.
		typeDropdown.setSelectedItem("Expense");
		gridConstraints.gridx = 1; gridConstraints.gridy = 1;
		addPanel.add(typeDropdown, gridConstraints);

		JLabel amountLabel = new JLabel("Amount: $");
		gridConstraints.gridx = 0; gridConstraints.gridy = 2;
		addPanel.add(amountLabel, gridConstraints);

		JTextField amountField = new JTextField(15);
		gridConstraints.gridx = 1; gridConstraints.gridy = 2;
		addPanel.add(amountField, gridConstraints);

		JLabel amountErrorLabel = new JLabel(" ");
		amountErrorLabel.setForeground(Color.RED);
		gridConstraints.gridx = 0; gridConstraints.gridy = 3; gridConstraints.gridwidth = 2;
		addPanel.add(amountErrorLabel, gridConstraints);
		gridConstraints.gridwidth = 1;

		// The memo label warns about the comma rule up front, so the user learns the constraint BEFORE typing
		// rather than only from the error message afterward.
		JLabel memoLabel = new JLabel("Memo (50 chars max, no commas):");
		gridConstraints.gridx = 0; gridConstraints.gridy = 4;
		addPanel.add(memoLabel, gridConstraints);

		JTextField memoField = new JTextField(15);
		gridConstraints.gridx = 1; gridConstraints.gridy = 4;
		addPanel.add(memoField, gridConstraints);

		JLabel memoErrorLabel = new JLabel(" ");
		memoErrorLabel.setForeground(Color.RED);
		gridConstraints.gridx = 0; gridConstraints.gridy = 5; gridConstraints.gridwidth = 2;
		addPanel.add(memoErrorLabel, gridConstraints);
		gridConstraints.gridwidth = 1;

		JLabel dateLabel = new JLabel("Date (MM/DD/YYYY):");
		gridConstraints.gridx = 0; gridConstraints.gridy = 6;
		addPanel.add(dateLabel, gridConstraints);

		JTextField dateField = new JTextField(15);
		gridConstraints.gridx = 1; gridConstraints.gridy = 6;
		addPanel.add(dateField, gridConstraints);

		JLabel dateErrorLabel = new JLabel(" ");
		dateErrorLabel.setForeground(Color.RED);
		gridConstraints.gridx = 0; gridConstraints.gridy = 7; gridConstraints.gridwidth = 2;
		addPanel.add(dateErrorLabel, gridConstraints);
		gridConstraints.gridwidth = 1;

		JLabel categoryLabel = new JLabel("Category:");
		gridConstraints.gridx = 0; gridConstraints.gridy = 8;
		addPanel.add(categoryLabel, gridConstraints);

		JComboBox<String> categoryDropdown = new JComboBox<>(EXPENSE_CATEGORIES);
		gridConstraints.gridx = 1; gridConstraints.gridy = 8;
		addPanel.add(categoryDropdown, gridConstraints);

		// The category list depends on the transaction type, so when the user flips the Type dropdown this listener
		// empties the category dropdown and refills it with the matching list (4 income categories or 17 expenses).
		// JComboBox constrains selection to the predefined list, so no isCategoryValid() is needed.
		typeDropdown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				categoryDropdown.removeAllItems();
				if (typeDropdown.getSelectedItem().equals("Income")) {
					for (String categoryOption : INCOME_CATEGORIES) {
						categoryDropdown.addItem(categoryOption);
					}
				} else {
					for (String categoryOption : EXPENSE_CATEGORIES) {
						categoryDropdown.addItem(categoryOption);
					}
				}
			}
		});

		JLabel statusLabel = new JLabel(" ");
		statusLabel.setForeground(new Color(0, 128, 0));
		gridConstraints.gridx = 0; gridConstraints.gridy = 9; gridConstraints.gridwidth = 2;
		addPanel.add(statusLabel, gridConstraints);

		JButton addTransactionButton = new JButton("Add Transaction");
		gridConstraints.gridy = 10;
		addPanel.add(addTransactionButton, gridConstraints);

		addTransactionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Clear any previous error messages before re-validating.
				amountErrorLabel.setText(" ");
				memoErrorLabel.setText(" ");
				dateErrorLabel.setText(" ");
				statusLabel.setText(" ");

				boolean badData = false;

				// Validate amount: isValueValid returns the parsed amount, or -1.0 when the input is invalid
				// (valid amounts are always positive).
				double validatedAmount = isValueValid(amountField.getText());
				if (validatedAmount < 0) {
					amountErrorLabel.setText("Invalid amount. Please enter a positive numeric value.");
					badData = true;
				}

				// Validate memo (length rule plus the no-commas rule for CSV integrity).
				if (!isMemoValid(memoField.getText())) {
					memoErrorLabel.setText("Memo must be 1-50 characters. Commas are not allowed.");
					badData = true;
				}

				// Validate date (format, real calendar date, not in the future).
				if (!isDateValid(dateField.getText())) {
					dateErrorLabel.setText("Invalid date. Please use MM/DD/YYYY format (not in the future).");
					badData = true;
				}

				// Stop and wait for the user to correct input if anything failed.
				if (badData) {
					return;
				}

				String transactionType = (String) typeDropdown.getSelectedItem();
				String transactionMemo = memoField.getText().trim();
				String transactionDate = dateField.getText().trim();
				String transactionCategory = (String) categoryDropdown.getSelectedItem();

				// Duplicate entry check: reject exact matches on all four fields.
				if (transactionLedger.isDuplicateEntry(
						validatedAmount, transactionMemo, transactionDate, transactionCategory)) {
					JOptionPane.showMessageDialog(ExpenseTrackerApp.this,
							"This transaction already exists in your ledger.\n"
									+ "Please verify your entries and try again.",
							"Duplicate Entry", JOptionPane.ERROR_MESSAGE);
					return;
				}

				// All fields valid: create the Transaction and add it to the ledger.
				if (transactionType.equals("Income")) {
					transactionLedger.addIncome(
							validatedAmount, transactionMemo, transactionDate, transactionCategory);
				} else {
					transactionLedger.addExpense(
							validatedAmount, transactionMemo, transactionDate, transactionCategory);
				}

				statusLabel.setText("Transaction added successfully. Balance: $"
						+ String.format("%.2f", transactionLedger.getBalance()));

				// Clear all input fields for the next entry.
				amountField.setText("");
				memoField.setText("");
				dateField.setText("");
			}
		});

		JButton backToMenuButton = new JButton("Back to Main Menu");
		gridConstraints.gridy = 11;
		addPanel.add(backToMenuButton, gridConstraints);
		backToMenuButton.addActionListener(event -> displayMenu());

		JButton viewHistoryButton = new JButton("View Transaction History");
		gridConstraints.gridy = 12;
		addPanel.add(viewHistoryButton, gridConstraints);
		viewHistoryButton.addActionListener(event -> handleUserChoice(2));

		return addPanel;
	}

	// PANEL BUILDER: TRANSACTION HISTORY PANEL
	// Includes the date range filter from the Phase 2 pseudocode:
	// All Transactions, Last 60 Days, or a Custom Date Range clamped to a maximum span of 60 days.
	private JPanel buildHistoryPanel() {
		JPanel historyPanel = new JPanel(new BorderLayout(10, 10));
		historyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Early exit for an empty ledger: message only, no filter controls needed.
		if (transactionLedger.getTransactionLedger().isEmpty()) {
			JLabel titleLabel = new JLabel("Transaction History", SwingConstants.CENTER);
			titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
			historyPanel.add(titleLabel, BorderLayout.NORTH);
			JLabel emptyLedgerLabel = new JLabel("No transactions recorded yet.", SwingConstants.CENTER);
			historyPanel.add(emptyLedgerLabel, BorderLayout.CENTER);

			JPanel emptyButtonPanel = new JPanel(new FlowLayout());
			JButton backButton = new JButton("Back to Main Menu");
			backButton.addActionListener(event -> displayMenu());
			emptyButtonPanel.add(backButton);
			historyPanel.add(emptyButtonPanel, BorderLayout.SOUTH);
			return historyPanel;
		}

		// TOP SECTION: title plus the date range filter controls.
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

		JLabel titleLabel = new JLabel("Transaction History", SwingConstants.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		topPanel.add(titleLabel);

		JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
		filterRow.add(new JLabel("Select Date Range:"));
		JComboBox<String> rangeDropdown = new JComboBox<>(
				new String[]{"All Transactions", "Last 60 Days", "Custom Date Range"});
		filterRow.add(rangeDropdown);
		topPanel.add(filterRow);

		// Custom range row: hidden unless "Custom Date Range" is selected.
		JPanel customRangeRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
		customRangeRow.add(new JLabel("Start (MM/DD/YYYY):"));
		JTextField startDateField = new JTextField(8);
		customRangeRow.add(startDateField);
		customRangeRow.add(new JLabel("End (MM/DD/YYYY):"));
		JTextField endDateField = new JTextField(8);
		customRangeRow.add(endDateField);
		JButton applyRangeButton = new JButton("Apply");
		customRangeRow.add(applyRangeButton);
		customRangeRow.setVisible(false);
		topPanel.add(customRangeRow);

		// Message label for range errors and the 60-day clamp notice.
		JLabel rangeMessageLabel = new JLabel(" ");
		rangeMessageLabel.setForeground(Color.RED);
		topPanel.add(rangeMessageLabel);

		historyPanel.add(topPanel, BorderLayout.NORTH);

		// CENTER SECTION: summary statistics plus the transaction table.
		JLabel balanceSummaryLabel = new JLabel();
		JLabel averageSummaryLabel = new JLabel();
		JLabel minimumSummaryLabel = new JLabel();
		JLabel maximumSummaryLabel = new JLabel();

		JPanel summaryPanel = new JPanel(new GridLayout(4, 1));
		summaryPanel.add(balanceSummaryLabel);
		summaryPanel.add(averageSummaryLabel);
		summaryPanel.add(minimumSummaryLabel);
		summaryPanel.add(maximumSummaryLabel);

		JTextArea historyTextArea = new JTextArea();
		historyTextArea.setEditable(false);
		historyTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		JScrollPane historyScrollPane = new JScrollPane(historyTextArea);

		JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
		centerPanel.add(summaryPanel, BorderLayout.NORTH);
		centerPanel.add(historyScrollPane, BorderLayout.CENTER);
		historyPanel.add(centerPanel, BorderLayout.CENTER);

		// REFRESH LOGIC: reads the dropdown (and custom fields when relevant), filters the sorted ledger to the
		// selected window; then rewrites the summary labels and the table.
		// NOTE: Balance always comes from the FULL ledger; average, minimum, and maximum come from the
		// FILTERED expenses only (group decision June 28).
		Runnable refreshHistoryView = () -> {
			rangeMessageLabel.setText(" ");

			ArrayList<Transaction> sortedLedger = transactionLedger.getLedgerSortedByDate();
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

			// Determine the start and end of the display window from the selection.
			LocalDate windowStart = null;
			LocalDate windowEnd = null;
			String selectedRange = (String) rangeDropdown.getSelectedItem();

			if (selectedRange.equals("Last 60 Days")) {
				windowStart = LocalDate.now().minusDays(60);
				windowEnd = LocalDate.now();
			} else if (selectedRange.equals("Custom Date Range")) {
				// Validate both custom dates before applying anything.
				if (!isDateValid(startDateField.getText()) || !isDateValid(endDateField.getText())) {
					rangeMessageLabel.setText("Invalid date. Please use MM/DD/YYYY format (not in the future).");
					return;
				}
				windowStart = LocalDate.parse(startDateField.getText().trim(), dateFormatter);
				windowEnd = LocalDate.parse(endDateField.getText().trim(), dateFormatter);

				// The end date cannot come before the start date.
				if (windowEnd.isBefore(windowStart)) {
					rangeMessageLabel.setText("End date cannot be before start date. Please try again.");
					return;
				}

				// Clamp ranges longer than 60 days, per the Phase 2 design:
				// the display window is limited so the panel stays readable; the full ledger history is always
				// available through Export to CSV.
				if (windowStart.plusDays(60).isBefore(windowEnd)) {
					windowEnd = windowStart.plusDays(60);
					rangeMessageLabel.setText(
							"Display range limited to 60 days. Use Export to CSV to access full ledger history.");
				}
			}
			// "All Transactions" leaves windowStart and windowEnd as null (no filter).

			// Filter the sorted ledger down to the selected window (inclusive bounds).
			ArrayList<Transaction> filteredLedger = new ArrayList<Transaction>();
			for (Transaction transaction : sortedLedger) {
				if (windowStart == null) {
					filteredLedger.add(transaction);
				} else {
					try {
						LocalDate transactionDate = LocalDate.parse(transaction.getDate(), dateFormatter);
						if (!transactionDate.isBefore(windowStart) && !transactionDate.isAfter(windowEnd)) {
							filteredLedger.add(transaction);
						}
					} catch (Exception generalException) {
						// General backup catchall: a transaction with an unparseable date (possible only in a
						// hand-edited CSV) is skipped, not crashed on
						// TODO: Is this appropriate error handling?
					}
				}
			}

			// Balance is always from the full ledger, never from the filtered view.
			balanceSummaryLabel.setText("Current Balance: $"
					+ String.format("%.2f", transactionLedger.getBalance()) + "  [full ledger]");

			if (filteredLedger.isEmpty()) {
				averageSummaryLabel.setText("Average Expense: n/a");
				minimumSummaryLabel.setText("Minimum Expense: n/a");
				maximumSummaryLabel.setText("Maximum Expense: n/a");
				historyTextArea.setText("No transactions found in that date range.");
				return;
			}

			// Compute average, minimum, and maximum across the FILTERED expenses only.
			double filteredExpenseSum = 0.0;
			int filteredExpenseCount = 0;
			double filteredMinimum = Double.MAX_VALUE;
			double filteredMaximum = 0.0;

			for (Transaction transaction : filteredLedger) {
				if (transaction.getType().equals("Expense")) {
					double transactionAmount = transaction.getAmount();
					filteredExpenseSum += transactionAmount;
					filteredExpenseCount += 1;
					if (transactionAmount < filteredMinimum) {
						filteredMinimum = transactionAmount;
					}
					if (transactionAmount > filteredMaximum) {
						filteredMaximum = transactionAmount;
					}
				}
			}

			if (filteredExpenseCount == 0) {
				averageSummaryLabel.setText("Average Expense: n/a  [no expenses in range]");
				minimumSummaryLabel.setText("Minimum Expense: n/a");
				maximumSummaryLabel.setText("Maximum Expense: n/a");
			} else {
				double filteredAverage =
						Math.round((filteredExpenseSum / filteredExpenseCount) * 100.0) / 100.0;
				averageSummaryLabel.setText("Average Expense: $"
						+ String.format("%.2f", filteredAverage) + "  [filtered, expenses only]");
				minimumSummaryLabel.setText("Minimum Expense: $"
						+ String.format("%.2f", filteredMinimum) + "  [filtered, expenses only]");
				maximumSummaryLabel.setText("Maximum Expense: $"
						+ String.format("%.2f", filteredMaximum) + "  [filtered, expenses only]");
			}

			// Rewrite the transaction table (most recent to earliest).
			StringBuilder historyText = new StringBuilder();
			historyText.append(String.format("%-4s | %-12s | %-8s | %-22s | %-30s | %10s%n",
					"ID", "Date", "Type", "Category", "Memo", "Amount"));
			historyText.append("-".repeat(100)).append("\n");

			for (Transaction transaction : filteredLedger) {
				historyText.append(String.format("%-4d | %-12s | %-8s | %-22s | %-30s | $%9.2f%n",
						transaction.getId(), transaction.getDate(), transaction.getType(),
						transaction.getCategory(), transaction.getMemo(), transaction.getAmount()));
			}

			historyTextArea.setText(historyText.toString());
			historyTextArea.setCaretPosition(0);
		};

		// Wire the filter controls to the refresh logic. Switching the dropdown applies All/Last-60 immediately
		// and shows or hides the custom range row; the Apply button triggers the refresh for a typed custom range.
		rangeDropdown.addActionListener(event -> {
			String selectedRange = (String) rangeDropdown.getSelectedItem();
			customRangeRow.setVisible(selectedRange.equals("Custom Date Range"));
			historyPanel.revalidate();
			if (!selectedRange.equals("Custom Date Range")) {
				refreshHistoryView.run();
			}
		});
		applyRangeButton.addActionListener(event -> refreshHistoryView.run());

		// Initial display: all transactions.
		refreshHistoryView.run();

		// BOTTOM SECTION: export and navigation buttons.
		JPanel buttonPanel = new JPanel(new FlowLayout());

		JButton exportButton = new JButton("Export Full Ledger to CSV");
		exportButton.addActionListener(event -> {
			// The export ALWAYS contains the full ledger, never the filtered view.
			// The filename includes the username and today's date to avoid overwriting earlier exports:
			// TransactionLedger_{Username}_{MM-DD-YYYY}.csv (hyphens,not slashes, because slashes
			// are illegal in filenames).
			DateTimeFormatter filenameFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
			String exportFilename = "TransactionLedger_"
					+ (username != null ? username : "Student")
					+ "_" + LocalDate.now().format(filenameFormatter) + ".csv";
			fileManager.saveLedgerToFile(transactionLedger.getTransactionLedger(), exportFilename);
		});
		buttonPanel.add(exportButton);

		JButton viewStatisticsButton = new JButton("View Statistics");
		viewStatisticsButton.addActionListener(event -> handleUserChoice(3));
		buttonPanel.add(viewStatisticsButton);

		JButton backToMenuButton = new JButton("Back to Main Menu");
		backToMenuButton.addActionListener(event -> displayMenu());
		buttonPanel.add(backToMenuButton);

		historyPanel.add(buttonPanel, BorderLayout.SOUTH);
		return historyPanel;
	}

	// PANEL BUILDER: STATISTICS AND SPENDING SCORE PANEL
	private JPanel buildStatisticsPanel() {
		JPanel statisticsPanel = new JPanel(new BorderLayout(10, 10));
		statisticsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JLabel titleLabel = new JLabel("Transaction Statistics and Spending Score", SwingConstants.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		statisticsPanel.add(titleLabel, BorderLayout.NORTH);

		if (transactionLedger.getTransactionLedger().isEmpty()) {
			JLabel emptyLedgerLabel = new JLabel(
					"No transactions available. Please add a transaction first.", SwingConstants.CENTER);
			statisticsPanel.add(emptyLedgerLabel, BorderLayout.CENTER);
		} else {
			JPanel statsContentPanel = new JPanel();
			statsContentPanel.setLayout(new BoxLayout(statsContentPanel, BoxLayout.Y_AXIS));
			statsContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

			statsContentPanel.add(new JLabel("Current Balance: $"
					+ String.format("%.2f", transactionLedger.getBalance())));
			statsContentPanel.add(new JLabel("Total Income: $"
					+ String.format("%.2f", transactionLedger.getTotalIncome())));
			statsContentPanel.add(new JLabel("Total Expenses: $"
					+ String.format("%.2f", transactionLedger.getTotalExpenses())));
			statsContentPanel.add(new JLabel("Average Expense: $"
					+ String.format("%.2f", transactionLedger.getAverageExpense())));
			statsContentPanel.add(new JLabel("Minimum Expense: $"
					+ String.format("%.2f", transactionLedger.getMinimumExpense())));
			statsContentPanel.add(new JLabel("Maximum Expense: $"
					+ String.format("%.2f", transactionLedger.getMaximumExpense())));

			statsContentPanel.add(Box.createVerticalStrut(15));

			SpendingScore spendingScore = transactionLedger.calculateSpendingScore();

			JLabel scoreLabel = new JLabel("Spending Score: " + spendingScore.calculateScore());
			scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
			statsContentPanel.add(scoreLabel);

			statsContentPanel.add(new JLabel("Remaining: "
					+ String.format("%.2f", spendingScore.getRemainingPercentage())
					+ "% of income retained"));

			statsContentPanel.add(Box.createVerticalStrut(10));

			// Unexpected expense warning, if the anomaly flag fired. When several transactions exceed the threshold,
			// the LARGEST one is displayed.
			Transaction unexpectedTransaction = spendingScore.getUnexpectedTransaction();
			if (unexpectedTransaction != null) {
				JLabel anomalyWarningLabel = new JLabel(
						"Unusual expense detected: " + unexpectedTransaction.getMemo()
								+ " - $" + String.format("%.2f", unexpectedTransaction.getAmount())
								+ " on " + unexpectedTransaction.getDate());
				anomalyWarningLabel.setForeground(Color.RED);
				statsContentPanel.add(anomalyWarningLabel);

				JLabel anomalyImpactLabel = new JLabel("This may be impacting your Spending Score.");
				anomalyImpactLabel.setForeground(Color.RED);
				statsContentPanel.add(anomalyImpactLabel);

				statsContentPanel.add(Box.createVerticalStrut(10));
			}

			// MDC resource links, shown based on the score status. These are real clickable buttons that open the
			// page in the default browser.
			String scoreStatus = spendingScore.getStatus();

			if (scoreStatus.equals("At Risk")) {
				statsContentPanel.add(new JLabel("Need help? Student Emergency Aid @ MDC (Single Stop)"));
				JButton emergencyAidLinkButton = new JButton("Open: MDC Single Stop Emergency Aid");
				emergencyAidLinkButton.addActionListener(event -> openResourceLink(
						"https://www.mdc.edu/singlestop/services/student-emergency-aid/"));
				statsContentPanel.add(emergencyAidLinkButton);
			}

			if (scoreStatus.equals("Needs Improvement")) {
				statsContentPanel.add(new JLabel(
						"TRIO @ MDC can assist you with any barriers to continue your education"));
				JButton trioLinkButton = new JButton("Open: TRIO @ MDC");
				trioLinkButton.addActionListener(event -> openResourceLink(
						"https://www.mdc.edu/trio-eoc/"));
				statsContentPanel.add(trioLinkButton);
			}

			// Student parent resources, shown whenever any childcare entry exists.
			if (transactionLedger.hasChildcareEntry()) {
				statsContentPanel.add(Box.createVerticalStrut(10));
				statsContentPanel.add(new JLabel("Resources for Student Parents @ MDC"));
				JButton studentParentsLinkButton = new JButton("Open: MDC Student Parents Resources");
				studentParentsLinkButton.addActionListener(event -> openResourceLink(
						"https://www.mdc.edu/student-parents/"));
				statsContentPanel.add(studentParentsLinkButton);
			}

			JScrollPane statisticsScrollPane = new JScrollPane(statsContentPanel);
			statisticsPanel.add(statisticsScrollPane, BorderLayout.CENTER);
		}

		JPanel buttonPanel = new JPanel(new FlowLayout());

		JButton viewHistoryButton = new JButton("View Transaction History");
		viewHistoryButton.addActionListener(event -> handleUserChoice(2));
		buttonPanel.add(viewHistoryButton);

		JButton backToMenuButton = new JButton("Back to Main Menu");
		backToMenuButton.addActionListener(event -> displayMenu());
		buttonPanel.add(backToMenuButton);

		statisticsPanel.add(buttonPanel, BorderLayout.SOUTH);
		return statisticsPanel;
	}

	// PANEL BUILDER: SETTINGS PANEL
	private JPanel buildSettingsPanel() {
		JPanel settingsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridConstraints = new GridBagConstraints();
		gridConstraints.insets = new Insets(8, 8, 8, 8);
		gridConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridConstraints.gridx = 0;

		JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		gridConstraints.gridy = 0;
		settingsPanel.add(titleLabel, gridConstraints);

		// Daily reminder toggle (session-only, resets to enabled on relaunch).
		JCheckBox dailyReminderCheckbox = new JCheckBox(
				"Show daily expense reminder on startup", dailyReminderEnabled);
		gridConstraints.gridy = 1;
		settingsPanel.add(dailyReminderCheckbox, gridConstraints);
		dailyReminderCheckbox.addActionListener(event -> {
			dailyReminderEnabled = dailyReminderCheckbox.isSelected();
			// Persist immediately so the toggle survives relaunch
			// (fixes Julien Test 9, where turning the reminder off still showed it on the next startup)
			saveSettings();
		});

		// Change password: asks for the current password, the new one, and a confirmation, all in one dialog.
		// The change lasts for this session only.
		// TODO: consider making this ongoing
		JButton changePasswordButton = new JButton("Change Password");
		gridConstraints.gridy = 2;
		settingsPanel.add(changePasswordButton, gridConstraints);
		changePasswordButton.addActionListener(event -> {
			JPasswordField currentPasswordField = new JPasswordField(15);
			JPasswordField newPasswordField = new JPasswordField(15);
			JPasswordField confirmPasswordField = new JPasswordField(15);

			JPanel passwordDialogPanel = new JPanel(new GridLayout(3, 2, 5, 5));
			passwordDialogPanel.add(new JLabel("Current password:"));
			passwordDialogPanel.add(currentPasswordField);
			passwordDialogPanel.add(new JLabel("New password:"));
			passwordDialogPanel.add(newPasswordField);
			passwordDialogPanel.add(new JLabel("Confirm new password:"));
			passwordDialogPanel.add(confirmPasswordField);

			int dialogResult = JOptionPane.showConfirmDialog(this, passwordDialogPanel,
					"Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (dialogResult == JOptionPane.OK_OPTION) {
				String enteredCurrentPassword = new String(currentPasswordField.getPassword());
				String enteredNewPassword = new String(newPasswordField.getPassword());
				String enteredConfirmPassword = new String(confirmPasswordField.getPassword());

				if (!enteredCurrentPassword.equals(storedPassword)) {
					JOptionPane.showMessageDialog(this,
							"Current password is incorrect.", "Change Password",
							JOptionPane.ERROR_MESSAGE);
				} else if (enteredNewPassword.isEmpty()) {
					JOptionPane.showMessageDialog(this,
							"New password cannot be empty.", "Change Password",
							JOptionPane.ERROR_MESSAGE);
				} else if (!enteredNewPassword.equals(enteredConfirmPassword)) {
					JOptionPane.showMessageDialog(this,
							"New password and confirmation do not match.", "Change Password",
							JOptionPane.ERROR_MESSAGE);
				} else {
					storedPassword = enteredNewPassword;
					// Persist immediately so the new password works on the next relaunch, instead of reverting to the
					// default (the original session-only limitation)
					saveSettings();
					JOptionPane.showMessageDialog(this,
							"Password changed successfully.",
							"Change Password", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

		// Clear all transactions, guarded by a confirmation dialog.
		JButton clearTransactionsButton = new JButton("Clear All Transactions");
		gridConstraints.gridy = 3;
		settingsPanel.add(clearTransactionsButton, gridConstraints);
		clearTransactionsButton.addActionListener(event -> {
			int userConfirmation = JOptionPane.showConfirmDialog(this,
					"Are you sure you want to clear ALL transactions?\n"
							+ "This cannot be undone.",
					"Confirm Clear", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (userConfirmation == JOptionPane.YES_OPTION) {
				transactionLedger.loadFromLedger(new ArrayList<Transaction>());
				JOptionPane.showMessageDialog(this, "All transactions cleared.");
			}
		});

		JButton backToMenuButton = new JButton("Back to Main Menu");
		gridConstraints.gridy = 4;
		settingsPanel.add(backToMenuButton, gridConstraints);
		backToMenuButton.addActionListener(event -> displayMenu());

		return settingsPanel;
	}

	// HELPER: openResourceLink
	// Opens the given URL in the system's default web browser. Used by the clickable MDC resource buttons in the
	// Statistics panel.
	private void openResourceLink(String url) {
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (Exception generalException) {
			// General backup catchall: browsing can fail if the platform does not support Desktop,
			// the URL is malformed, or no browser is available. The fallback shows the URL so the user can
			// copy and paste it manually.
			JOptionPane.showMessageDialog(this,
					"Could not open the browser automatically.\nPlease visit: " + url,
					"Link", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	// Loads the saved password and daily-reminder setting from settings.txt, if it exists.
	// Called once on startup. Format is one key=value pair per line. This is what makes a changed password and the
	// reminder toggle persist across sessions, fixing the bug where both reset every launch
	// (found in team testing: Julien Test 9, and the password TODO).
	private void loadSettings() {
		try {
			java.io.BufferedReader settingsReader =
					new java.io.BufferedReader(new java.io.FileReader("settings.txt"));
			String settingsLine = settingsReader.readLine();
			while (settingsLine != null) {
				int equalsPosition = settingsLine.indexOf('=');
				if (equalsPosition > 0) {
					String settingKey = settingsLine.substring(0, equalsPosition);
					String settingValue = settingsLine.substring(equalsPosition + 1);
					if (settingKey.equals("password")) {
						storedPassword = settingValue;
					} else if (settingKey.equals("dailyReminderEnabled")) {
						dailyReminderEnabled = settingValue.equals("true");
					}
				}
				settingsLine = settingsReader.readLine();
			}
			settingsReader.close();
		} catch (java.io.FileNotFoundException fileNotFoundException) {
			// No settings file yet: keep the built-in defaults. Normal on first launch.
		} catch (Exception generalException) {
			// General backup catchall: a corrupt settings file falls back to defaults rather than crashing the
			// application on startup.
		}
	}

	// Writes the current password and daily-reminder setting to settings.txt. Called whenever either setting changes,
	// so the change survives the next relaunch.
	private void saveSettings() {
		try {
			java.io.BufferedWriter settingsWriter =
					new java.io.BufferedWriter(new java.io.FileWriter("settings.txt"));
			settingsWriter.write("password=" + storedPassword);
			settingsWriter.newLine();
			settingsWriter.write("dailyReminderEnabled=" + dailyReminderEnabled);
			settingsWriter.newLine();
			settingsWriter.close();
		} catch (Exception generalException) {
			// General backup catchall: if settings cannot be saved, the app still runs; the setting simply will
			// not persist to the next session.
			JOptionPane.showMessageDialog(this,
					"Could not save settings. Your change applies to this session only.",
					"Settings", JOptionPane.WARNING_MESSAGE);
		}
	}

	// INPUT VALIDATION METHODS (private methods inside ExpenseTrackerApp, per the UML)

	// METHOD: isOptionValid
	// Checks that a menu input is a number matching one of the valid options.
	// With the GUI, menu choices come from buttons, so this method is currently unused. It is kept because it appears
	// in the UML and would be required if the app ever adds free-typed option input.
	private boolean isOptionValid(String inputText, int[] validOptions) {
		try {
			int enteredOption = Integer.parseInt(inputText.trim());
			for (int validOption : validOptions) {
				if (enteredOption == validOption) {
					return true;
				}
			}
			return false;
		} catch (Exception generalException) {
			// General backup catchall: any non-numeric input lands here (normally a
			// NumberFormatException) and is reported as invalid rather than crashing.
			return false;
		}
	}

	// METHOD: isValueValid
	// Checks that an amount input is a positive number. Returns the validated amount rounded to two decimal places,
	// or -1.0 if the input is invalid. The caller checks success with (result < 0), which is unambiguous because valid
	// transaction amounts are always positive. The name reads like a yes/no check but the method parses and  validates
	// in one step, matching the UML and pseudocode.
	// Kept for naming convention consistency with the other validation methods.
	private double isValueValid(String inputText) {
		try {
			double enteredAmount = Double.parseDouble(inputText.trim());
			if (enteredAmount <= 0) {
				return -1.0;
			}
			// Round to two decimal places before returning.
			return Math.round(enteredAmount * 100.0) / 100.0;
		} catch (Exception generalException) {
			// General backup catchall: any non-numeric input lands here (normally a NumberFormatException) and is
			// reported as invalid rather than crashing.
			return -1.0;
		}
	}

	// METHOD: isMemoValid
	// Checks that a memo input is between 1 and 50 characters, not empty, and contains no commas. The comma rule
	// protects the CSV file: records are sliced by comma position on reload, so a comma inside a memo would shift every
	// field after it and corrupt the data. Blocking commas at entry is the simplest fix that keeps the indexOf()
	// slicing approach the team chose
	// TODO: Build a comma functionality?
	private boolean isMemoValid(String inputText) {
		String enteredMemo = inputText.trim();
		if (enteredMemo.isEmpty() || enteredMemo.length() > 50) {
			return false;
		}
		if (enteredMemo.contains(",")) {
			return false;
		}
		return true;
	}

	// METHOD: isDateValid
	// Checks that a date input matches MM/DD/YYYY format, is a real calendar date, and is not in the future.
	private boolean isDateValid(String inputText) {
		try {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			LocalDate enteredDate = LocalDate.parse(inputText.trim(), dateFormatter);

			if (enteredDate.isAfter(LocalDate.now())) {
				// Future dates are not allowed.
				return false;
			}

			return true;
		} catch (Exception generalException) {
			// General backup catchall: any unparseable date lands here (normally a DateTimeParseException) and
			// is reported as invalid rather than crashing.
			return false;
		}
	}
}

// CLASS 2: Transaction
// Data object: one income or expense entry. Holds no logic beyond construction and getters; all calculations happen in
// ExpenseTracker and SpendingScore.
class Transaction {

	// ATTRIBUTES
	private int id;
	private double amount;
	private String type;		// "Income" or "Expense"
	private String memo;
	private String date;		// stored as MM/DD/YYYY string
	private String category;

	// CONSTRUCTOR
	public Transaction(int id, double amount, String type, String memo, String date, String category) {
		this.id = id;
		this.amount = amount;
		this.type = type;
		this.memo = memo;
		this.date = date;
		this.category = category;
	}

	// GETTERS
	public int getId() {
		return id;
	}

	public double getAmount() {
		return amount;
	}

	public String getType() {
		return type;
	}

	public String getMemo() {
		return memo;
	}

	public String getDate() {
		return date;
	}

	public String getCategory() {
		return category;
	}
}

// CLASS 3: ExpenseTracker
// Core logic: manages the transaction ledger and all calculations. Owns the ArrayList of Transaction objects.
// Delegates scoring to SpendingScore and file persistence to FileManager.
class ExpenseTracker {

	// ATTRIBUTES
	private double balance;
	private double totalIncome;
	private double totalExpenses;
	private ArrayList<Transaction> transactionLedger;

	// CONSTRUCTOR
	public ExpenseTracker() {
		this.balance = 0.0;
		this.totalIncome = 0.0;
		this.totalExpenses = 0.0;
		this.transactionLedger = new ArrayList<Transaction>();
	}

	// METHOD: addIncome
	// Creates an Income transaction, adds it to the ledger, and updates balance and totalIncome.
	public void addIncome(double amount, String memo, String date, String category) {
		int newTransactionId = transactionLedger.size() + 1;
		Transaction newTransaction = new Transaction(
				newTransactionId, amount, "Income", memo, date, category);
		transactionLedger.add(newTransaction);
		balance += amount;
		totalIncome += amount;
	}

	// METHOD: addExpense
	// Creates an Expense transaction, adds it to the ledger, and updates balance and totalExpenses.
	public void addExpense(double amount, String memo, String date, String category) {
		int newTransactionId = transactionLedger.size() + 1;
		Transaction newTransaction = new Transaction(
				newTransactionId, amount, "Expense", memo, date, category);
		transactionLedger.add(newTransaction);
		balance -= amount;
		totalExpenses += amount;
	}

	// METHOD: isDuplicateEntry
	// Returns true if an identical transaction (same amount, memo, date, and category) already exists in the ledger.
	// Exact duplicates are rejected at entry.
	public boolean isDuplicateEntry(double amount, String memo, String date, String category) {
		for (Transaction transaction : transactionLedger) {
			if (transaction.getAmount() == amount
					&& transaction.getMemo().equals(memo)
					&& transaction.getDate().equals(date)
					&& transaction.getCategory().equals(category)) {
				return true;
			}
		}
		// No duplicate found, so the entry is safe to add.
		return false;
	}

	// METHOD: getLedgerSortedByDate
	// Returns a copy of the ledger sorted chronologically by date (most recent to earliest), without altering the
	// original entry order. Date strings are parsed to LocalDate for accurate comparison.
	public ArrayList<Transaction> getLedgerSortedByDate() {
		ArrayList<Transaction> sortedLedger = new ArrayList<Transaction>(transactionLedger);
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

		try {
			Collections.sort(sortedLedger, new Comparator<Transaction>() {
				public int compare(Transaction firstTransaction, Transaction secondTransaction) {
					LocalDate firstDate = LocalDate.parse(firstTransaction.getDate(), dateFormatter);
					LocalDate secondDate = LocalDate.parse(secondTransaction.getDate(), dateFormatter);
					return secondDate.compareTo(firstDate);
				}
			});
		} catch (Exception generalException) {
			// General backup catchall: if any date string fails to parse during the sort (possible only in a
			// hand-edited CSV), fall back to the unsorted copy in original entry order rather than
			// crash the History panel.
		}

		return sortedLedger;
	}

	// METHOD: hasChildcareEntry
	// Returns true if any transaction in the ledger is categorized "Family / Childcare".
	public boolean hasChildcareEntry() {
		for (Transaction transaction : transactionLedger) {
			if (transaction.getCategory().equals("Family / Childcare")) {
				return true;
			}
		}
		return false;
	}

	// GETTERS
	public double getBalance() {
		return balance;
	}

	public double getTotalIncome() {
		return totalIncome;
	}

	public double getTotalExpenses() {
		return totalExpenses;
	}

	public ArrayList<Transaction> getTransactionLedger() {
		return transactionLedger;
	}

	// METHOD: getAverageExpense
	// Returns the average amount across all expense entries only, rounded to two decimal places. Income transactions
	// are excluded.
	// NOTE: the same calculation also exists inside SpendingScore, which needs it for the anomaly threshold; the two
	// copies are intentional (each class computes it for its own purpose).
	public double getAverageExpense() {
		double expenseSum = 0.0;
		int expenseCount = 0;

		for (Transaction transaction : transactionLedger) {
			if (transaction.getType().equals("Expense")) {
				expenseSum += transaction.getAmount();
				expenseCount += 1;
			}
		}

		if (expenseCount == 0) {
			return 0.0;
		}

		return Math.round((expenseSum / expenseCount) * 100.0) / 100.0;
	}

	// METHOD: getMinimumExpense
	// Returns the smallest expense amount in the ledger, excluding income entries.
	public double getMinimumExpense() {
		double minimumExpense = Double.MAX_VALUE;
		int expenseCount = 0;

		for (Transaction transaction : transactionLedger) {
			if (transaction.getType().equals("Expense")) {
				if (transaction.getAmount() < minimumExpense) {
					minimumExpense = transaction.getAmount();
				}
				expenseCount += 1;
			}
		}

		if (expenseCount == 0) {
			return 0.0;
		}

		return minimumExpense;
	}

	// METHOD: getMaximumExpense
	// Returns the largest expense amount in the ledger, excluding income entries.
	public double getMaximumExpense() {
		double maximumExpense = 0.0;
		int expenseCount = 0;

		for (Transaction transaction : transactionLedger) {
			if (transaction.getType().equals("Expense")) {
				if (transaction.getAmount() > maximumExpense) {
					maximumExpense = transaction.getAmount();
				}
				expenseCount += 1;
			}
		}

		if (expenseCount == 0) {
			return 0.0;
		}

		return maximumExpense;
	}

	// METHOD: calculateSpendingScore
	// Delegates to SpendingScore to calculate the composite score based on balance:
		// Health (60%);
		// 50/30/20 category distribution (30%); and
		// unexpected expense detection (10%).
	// Returns the SpendingScore OBJECT (the UML said String) so the GUI can also read the status, the remaining
	// percentage, and any flagged anomaly transaction. Listed in the final UML update at the top of this file.
	public SpendingScore calculateSpendingScore() {
		SpendingScore spendingScore = new SpendingScore(balance, totalIncome, transactionLedger);
		return spendingScore;
	}

	// METHOD: loadFromLedger
	// Replaces the current ledger with a loaded one and recalculates balance, totalIncome, and totalExpenses from the
	// loaded transactions. Called on startup after FileManager.loadLedger(), and by Clear All Transactions (with
	// an empty list). Not in the Phase 2 UML; listed in the final UML update at the top of this file.
	public void loadFromLedger(ArrayList<Transaction> loadedLedger) {
		this.transactionLedger = loadedLedger;
		this.balance = 0.0;
		this.totalIncome = 0.0;
		this.totalExpenses = 0.0;

		for (Transaction transaction : transactionLedger) {
			if (transaction.getType().equals("Income")) {
				balance += transaction.getAmount();
				totalIncome += transaction.getAmount();
			} else if (transaction.getType().equals("Expense")) {
				balance -= transaction.getAmount();
				totalExpenses += transaction.getAmount();
			}
		}
	}
}

// CLASS 4: SpendingScore
// Calculates and holds a composite spending score out of 100:
	//	60% Balance Health (current balance vs total income);
	//	30% Category Distribution (closeness to the 50/30/20 rule, last 60 days only); and
	//	10% Unexpected Expense Flag (single transaction anomaly detection)
// Category distribution uses only the last 60 days so the score reflects recent habits; full history stays available
// via CSV export.
class SpendingScore {

	// ATTRIBUTES
	private int score;
	private String status;
	private double remainingPercentage;

	// The transaction that triggered the anomaly flag (null when no anomaly). When several transactions exceed the
	// threshold, the LARGEST one is kept, since the biggest outlier is the most useful one to show the user. Not in the
	// Phase 2 UML; listed in the final UML update at the top of this file.
	private Transaction unexpectedTransaction;

	// Category buckets for the 50/30/20 rule. The "Other" category is intentionally in no bucket: it is excluded from
	// distribution scoring, per the Phase 2 design.
	private static final String[] NEEDS_CATEGORIES = {
			"Rent / Housing", "Utilities", "Groceries / Food",
			"Transport", "Health / Medical", "Family / Childcare", "Tuition / School Fees"
	};

	private static final String[] WANTS_CATEGORIES = {
			"Entertainment", "Dining Out", "Shopping / Clothing",
			"Personal Care", "Travel / Vacation", "Subscriptions"
	};

	private static final String[] SAVINGS_CATEGORIES = {
			"Savings", "Debt Repayment", "School Supplies / Books"
	};

	// CONSTRUCTOR
	// Receives balance, totalIncome, and the transactionLedger; calculates all three score components and assembles the
	// final composite score.
	public SpendingScore(double balance, double totalIncome, ArrayList<Transaction> transactionLedger) {

		// Guard clause: no income recorded means no meaningful score can be computed.
		if (totalIncome == 0) {
			this.remainingPercentage = 0.0;
			this.score = 0;
			this.status = "No income recorded";
			return;
		}

		// COMPONENT 1: BALANCE HEALTH (60 points max)
		remainingPercentage = (balance / totalIncome) * 100;
		int balanceScore;

		if (remainingPercentage >= 80) {
			balanceScore = 60;
		} else if (remainingPercentage >= 60) {
			balanceScore = 48;
		} else if (remainingPercentage >= 40) {
			balanceScore = 36;
		} else if (remainingPercentage >= 20) {
			balanceScore = 24;
		} else {
			balanceScore = 12;
		}

		// COMPONENT 2: CATEGORY DISTRIBUTION, 50/30/20 RULE (30 points max)
		// Only expenses from the last 60 days are considered, consistent with the History display window.
		ArrayList<Transaction> recentExpenses = getExpensesFromLast60Days(transactionLedger);

		double needsTotal = sumByCategory(recentExpenses, NEEDS_CATEGORIES);
		double wantsTotal = sumByCategory(recentExpenses, WANTS_CATEGORIES);
		double savingsTotal = sumByCategory(recentExpenses, SAVINGS_CATEGORIES);
		double bucketedExpenseTotal = needsTotal + wantsTotal + savingsTotal;

		int distributionScore;

		if (bucketedExpenseTotal == 0) {
			// Neutral score if there are no bucketed expenses in the window.
			distributionScore = 15;
		} else {
			double needsPercentage = (needsTotal / bucketedExpenseTotal) * 100;
			double wantsPercentage = (wantsTotal / bucketedExpenseTotal) * 100;
			double savingsPercentage = (savingsTotal / bucketedExpenseTotal) * 100;

			// Full points if within 10% of target, tapering off from there.
			int needsBucketScore = scoreBucketDeviation(Math.abs(needsPercentage - 50));
			int wantsBucketScore = scoreBucketDeviation(Math.abs(wantsPercentage - 30));
			int savingsBucketScore = scoreBucketDeviation(Math.abs(savingsPercentage - 20));

			distributionScore = needsBucketScore + wantsBucketScore + savingsBucketScore;
		}

		// COMPONENT 3: UNEXPECTED EXPENSE FLAG (10 points max)
		// A transaction is "unexpected" if it exceeds the anomaly threshold, which is the MINIMUM of:
			// (a) 3x the average expense; and
			// (b) 25% of total income
		// Using the minimum keeps the flag context-sensitive: As mentioned on the presentation in class,
		// $500 on school supplies flags on a $2000 income, but $2000 on entertainment does not flag on a $1,000,000
		// income by the average-based rule alone
		double averageExpense = getAverageExpense(transactionLedger);
		double anomalyThresholdByAverage = averageExpense * 3;
		double anomalyThresholdByIncome = totalIncome * 0.25;
		double anomalyThreshold = Math.min(anomalyThresholdByAverage, anomalyThresholdByIncome);

		boolean unexpectedFlag = false;
		double largestUnexpectedAmount = 0.0;

		// When several transactions exceed the threshold, keep the LARGEST one for display (decision on July 10):
		// the biggest outlier matters most.
		for (Transaction transaction : transactionLedger) {
			if (transaction.getType().equals("Expense")) {
				if (transaction.getAmount() > anomalyThreshold
						&& transaction.getAmount() > largestUnexpectedAmount) {
					unexpectedFlag = true;
					largestUnexpectedAmount = transaction.getAmount();
					this.unexpectedTransaction = transaction;
				}
			}
		}

		int anomalyScore;
		if (unexpectedFlag) {
			// Anomaly detected: zero points, and the transaction is flagged for display.
			anomalyScore = 0;
		} else {
			// No anomalies: full points.
			anomalyScore = 10;
		}

		// FINAL SCORE ASSEMBLY (range: 0 to 100)
		this.score = balanceScore + distributionScore + anomalyScore;

		if (this.score >= 80) {
			this.status = "Excellent";
		} else if (this.score >= 60) {
			this.status = "Good";
		} else if (this.score >= 40) {
			this.status = "Fair";
		} else if (this.score >= 20) {
			this.status = "Needs Improvement";
		} else {
			this.status = "At Risk";
		}
	}

	// HELPER: scoreBucketDeviation
	// Shared scoring curve used by all three 50/30/20 buckets: full points if within 10% of target, partial if close,
	// zero if far off.
	private int scoreBucketDeviation(double deviation) {
		if (deviation <= 10) {
			return 10;
		} else if (deviation <= 20) {
			return 6;
		} else if (deviation <= 30) {
			return 3;
		} else {
			return 0;
		}
	}

	// HELPER: sumByCategory
	// Sums expense amounts whose category falls within the given bucket array.
	private double sumByCategory(ArrayList<Transaction> expenses, String[] bucketCategories) {
		double bucketTotal = 0.0;

		for (Transaction transaction : expenses) {
			for (String bucketCategory : bucketCategories) {
				if (transaction.getCategory().equals(bucketCategory)) {
					bucketTotal += transaction.getAmount();
					break;
				}
			}
		}

		return bucketTotal;
	}

	// HELPER: getExpensesFromLast60Days
	// Filters the full ledger down to Expense transactions dated within the last 60 days, consistent with the History
	// display window.
	private ArrayList<Transaction> getExpensesFromLast60Days(ArrayList<Transaction> transactionLedger) {
		ArrayList<Transaction> recentExpenses = new ArrayList<Transaction>();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		LocalDate cutoffDate = LocalDate.now().minusDays(60);

		for (Transaction transaction : transactionLedger) {
			if (transaction.getType().equals("Expense")) {
				try {
					LocalDate transactionDate = LocalDate.parse(transaction.getDate(), dateFormatter);
					if (!transactionDate.isBefore(cutoffDate)) {
						recentExpenses.add(transaction);
					}
				} catch (Exception generalException) {
					// General backup catchall: a transaction with an unparseable date
					// (possible only in a hand-edited CSV) is skipped rather than
					// crashing the score calculation, since isDateValid() gates entry.
				}
			}
		}

		return recentExpenses;
	}

	// HELPER: getAverageExpense
	// Average across ALL expense transactions in the ledger (not just the 60-day window), so the anomaly threshold
	// reflects the student's overall spending pattern.
	// Note: the same calculation also exists in ExpenseTracker, which needs it for panel display; the two copies are
	// intentional.
	public double getAverageExpense(ArrayList<Transaction> transactionLedger) {
		double expenseSum = 0.0;
		int expenseCount = 0;

		for (Transaction transaction : transactionLedger) {
			if (transaction.getType().equals("Expense")) {
				expenseSum += transaction.getAmount();
				expenseCount += 1;
			}
		}

		if (expenseCount == 0) {
			return 0.0;
		}

		return Math.round((expenseSum / expenseCount) * 100.0) / 100.0;
	}

	// METHOD: calculateScore
	// Returns a formatted summary string of the final composite score.
	public String calculateScore() {
		return score + " / 100 - " + status;
	}

	// GETTERS
	public String getStatus() {
		return status;
	}

	public double getRemainingPercentage() {
		return remainingPercentage;
	}

	// Returns the Transaction that triggered the anomaly flag (the largest one when sveeral qualify), or null if no
	// anomaly was detected. Used by the Statistics panel warning message.
	public Transaction getUnexpectedTransaction() {
		return unexpectedTransaction;
	}
}

// CLASS 5: FileManager
// Handles saving and loading the transaction ledger to and from a CSV file. Uses indexOf() slicing instead of split(),
// per group decision, to promote efficiency and avoid the overhead of regex-based splitting.
class FileManager {

	// ATTRIBUTES
	private String filename;

	// CONSTRUCTOR
	// Sets the default filename used for automatic save and load operations.
	public FileManager(String fileName) {
		this.filename = fileName;
	}

	// METHOD: saveLedger
	// Saves the ledger to the default persistence file (transactions.csv). Used by the exit sequence and window close.
	// Delegates to saveLedgerToFile.
	public void saveLedger(ArrayList<Transaction> transactionLedger) {
		saveLedgerToFile(transactionLedger, this.filename);
	}

	// METHOD: saveLedgerToFile
	// Writes all transactions in the ledger to the given CSV file, one record per line, with a header row first. Used
	// directly by the Export button, which builds a dated filename (TransactionLedger_{Username}_{MM-DD-YYYY}.csv)
	// so exports do not overwrite each other. Not in the Phase 2 UML; listed in the final UML update at the top of
	// this file.
	public void saveLedgerToFile(ArrayList<Transaction> transactionLedger, String targetFilename) {
		int counterTotalRecordsWritten = 0;

		try {
			// Open FileWriter at the target filename, wrapped in BufferedWriter.
			BufferedWriter csvWriter = new BufferedWriter(new FileWriter(targetFilename));

			// Write the header row (column titles, comma-separated).
			csvWriter.write("ID,Amount,Type,Memo,Date,Category");
			csvWriter.newLine();

			// Write one record per transaction, fields separated by commas.
			for (Transaction transaction : transactionLedger) {
				String csvRecord = transaction.getId()
						+ "," + transaction.getAmount()
						+ "," + transaction.getType()
						+ "," + transaction.getMemo()
						+ "," + transaction.getDate()
						+ "," + transaction.getCategory();

				csvWriter.write(csvRecord);
				csvWriter.newLine();
				counterTotalRecordsWritten += 1;
			}

			// Close the file, then display the final totals.
			csvWriter.close();

			if (counterTotalRecordsWritten > 0) {
				JOptionPane.showMessageDialog(null,
						targetFilename + " saved with " + counterTotalRecordsWritten + " record(s).");
			}

		} catch (IOException ioException) {
			JOptionPane.showMessageDialog(null,
					"Error saving file. Please check your storage permissions.",
					"Save Error", JOptionPane.ERROR_MESSAGE);
		} catch (Exception generalException) {
			// General backup catchall: any unexpected failure during writing lands here
			// instead of crashing the application mid-save.
			JOptionPane.showMessageDialog(null,
					"Unexpected error while saving the file.",
					"Save Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// METHOD: loadLedger
	// Reads the CSV file and reconstructs the transaction ledger for the current session. Returns an empty ledger if no
	// file exists or the file cannot be read.
	public ArrayList<Transaction> loadLedger() {
		int counterTotalRecordsRead = 0;
		ArrayList<Transaction> loadedLedger = new ArrayList<Transaction>();

		try {
			// Open FileReader at the filename, wrapped in BufferedReader.
			BufferedReader csvReader = new BufferedReader(new FileReader(filename));

			// Read and skip the header row, then read the first record.
			csvReader.readLine();
			String csvRecord = csvReader.readLine();

			// Loop while there are records to read (not end of file).
			while (csvRecord != null) {
				// Strip any trailing whitespace or newline characters from the record.
				csvRecord = csvRecord.trim();

				// Skip blank lines rather than crash on them.
				if (csvRecord.isEmpty()) {
					csvRecord = csvReader.readLine();
					continue;
				}

				// Slice each field from the record using indexOf(',').

				// Slice ID: from the start of the record to the first comma.
				int idComma = csvRecord.indexOf(',');
				int slicedId = Integer.parseInt(csvRecord.substring(0, idComma));

				// Slice Amount: from after the first comma to the second comma.
				int amountComma = csvRecord.indexOf(',', idComma + 1);
				double slicedAmount = Double.parseDouble(
						csvRecord.substring(idComma + 1, amountComma));

				// Slice Type: from after the second comma to the third comma.
				int typeComma = csvRecord.indexOf(',', amountComma + 1);
				String slicedType = csvRecord.substring(amountComma + 1, typeComma);

				// Slice Memo: from after the third comma to the fourth comma.
				int memoComma = csvRecord.indexOf(',', typeComma + 1);
				String slicedMemo = csvRecord.substring(typeComma + 1, memoComma);

				// Slice Date: from after the fourth comma to the fifth comma.
				int dateComma = csvRecord.indexOf(',', memoComma + 1);
				String slicedDate = csvRecord.substring(memoComma + 1, dateComma);

				// Slice Category: from after the fifth comma to the end of the record.
				String slicedCategory = csvRecord.substring(dateComma + 1);

				// Build the Transaction object from the sliced fields and add it.
				Transaction loadedTransaction = new Transaction(
						slicedId, slicedAmount, slicedType,
						slicedMemo, slicedDate, slicedCategory);

				loadedLedger.add(loadedTransaction);
				counterTotalRecordsRead += 1;

				// Read the next record.
				csvRecord = csvReader.readLine();
			}

			// Close the file.
			csvReader.close();

			// Return not needed here as the caller uses the same numnber as the ledger's size

		} catch (java.io.FileNotFoundException fileNotFoundException) {
			// No saved file found: return the empty ledger and start fresh.
			return loadedLedger;
		} catch (IOException ioException) {
			JOptionPane.showMessageDialog(null,
					"Error reading file.", "Load Error", JOptionPane.ERROR_MESSAGE);
		} catch (Exception generalException) {
			// General backup catchall: a malformed CSV row (missing comma, bad number) throws NumberFormatException or
			// StringIndexOutOfBoundsException, neither of which is an IOException. Without this catch the application
			// would crash instead of falling back to a fresh start.
			JOptionPane.showMessageDialog(null,
					"Error loading data. Starting fresh.",
					"Load Error", JOptionPane.ERROR_MESSAGE);
			return new ArrayList<Transaction>();
		}

		return loadedLedger;
	}
}
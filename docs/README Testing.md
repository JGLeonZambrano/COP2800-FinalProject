# Testing Overview

Testing artifacts for the Daily Student Expense Tracker (COP2800 Final Project, Phase 3).
All results below come from actually running the application, not from static review.

## Files

- **Phase3_Smoke_Test_Results.md** — 5-test smoke run (launch, add transactions,
  history/statistics with a hand-verified score of 79/Good, dated export, and
  persistence across relaunch). Executed by Jose Leon Zambrano, all 5 PASS.

- **Phase3_Testing_Plan.md** — the master test plan (48 base cases across startup,
  login/lockout, validation, history/statistics, scoring, settings, and stress/edge).
  This is the full pass ideally to be completed before final submission.

- **Phase3_Testing_Plan_Addendum.md** — 23 additional cases for features added after
  July 10 (MDC logo, date-range filter, Change Password, dated exports, clickable
  resource links, largest-anomaly display, general catchall handlers). Merges into the
  master plan for a combined total of 71 cases.

- **ExpenseTrackerApp_Test_Julien_Morales.docx** — independent 9-test pass executed by
  Julien Morales. Found four real issues: the lockout timer resets on relaunch, the
  daily-reminder toggle and password did not persist (both since fixed via settings.txt),
  and a floating-point precision limit on extremely large amounts (documented as a known
  limitation). This was the extra team-testing deliverable requested by the professor.

## Status

Smoke tests and Julien's pass are complete and were run against the live application.
The full 71-case plan is the remaining testing task before submission.
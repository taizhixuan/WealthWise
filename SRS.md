# Software Requirements Specification (SRS)

## WealthWise - Personal Finance Management Application

**Version:** 1.0.0
**Date:** February 7, 2026
**Platform:** Android (Min SDK 26 / Android 8.0+)

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Overall Description](#2-overall-description)
3. [System Architecture](#3-system-architecture)
4. [Functional Requirements](#4-functional-requirements)
5. [Data Requirements](#5-data-requirements)
6. [Non-Functional Requirements](#6-non-functional-requirements)
7. [External Interface Requirements](#7-external-interface-requirements)
8. [Appendices](#8-appendices)

---

## 1. Introduction

### 1.1 Purpose

This document specifies the software requirements for WealthWise, an Android personal finance management application. It serves as a reference for development, testing, and stakeholder review.

### 1.2 Scope

WealthWise enables users to track income and expenses, set budgets per category, manage recurring transactions, view analytics with interactive charts, receive AI-powered financial recommendations, and forecast future spending. Data is stored locally using Room database with optional cloud sync via Firebase.

### 1.3 Definitions and Acronyms

| Term | Definition |
|------|-----------|
| MVVM | Model-View-ViewModel architectural pattern |
| Room | Android persistence library (SQLite abstraction) |
| LiveData | Lifecycle-aware observable data holder |
| DAO | Data Access Object |
| SRS | Software Requirements Specification |
| FAB | Floating Action Button |
| CRUD | Create, Read, Update, Delete |
| DTO | Data Transfer Object |

### 1.4 Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 17 |
| UI Framework | Material Design 3 | 1.12.0 |
| Local Database | Room | 2.6.1 |
| Authentication | Firebase Auth | BOM 33.7.0 |
| Cloud Database | Cloud Firestore | BOM 33.7.0 |
| Charts | MPAndroidChart | 3.1.0 |
| Background Work | WorkManager | 2.9.1 |
| Navigation | Jetpack Navigation | 2.8.5 |
| Lifecycle | AndroidX Lifecycle | 2.8.7 |
| Build System | Gradle | 8.13 |
| Compile SDK | Android 14 | API 34 |
| Min SDK | Android 8.0 | API 26 |
| Target SDK | Android 14 | API 34 |

---

## 2. Overall Description

### 2.1 Product Perspective

WealthWise is a standalone Android application that operates offline-first. All data is persisted locally in a Room (SQLite) database. When the user is authenticated with Firebase, data synchronizes bidirectionally with Cloud Firestore on a 6-hour interval or on-demand.

### 2.2 User Classes

| User Class | Description |
|------------|-------------|
| Guest User | Uses the app without authentication. All data is local only. No cloud sync. |
| Authenticated User | Signed in via email/password or Google SSO. Data syncs to Firebase. |

### 2.3 Operating Environment

- Android 8.0 (API 26) or higher
- Internet connection required only for authentication and cloud sync
- All core functionality works offline

### 2.4 Design Constraints

- Single-activity architecture with Fragment-based navigation
- MVVM pattern enforced: Fragments observe ViewModels, ViewModels call Repositories
- All database write operations execute on a 4-thread background pool
- Soft-delete pattern: records are marked `isDeleted=true` rather than physically removed
- Material You dynamic colors on Android 12+ (API 31+), teal fallback palette on older devices

### 2.5 Assumptions and Dependencies

- Firebase project is configured with valid `google-services.json`
- Google Play Services available on device for Google Sign-In
- Device has at least one active account (cash) seeded on first launch

---

## 3. System Architecture

### 3.1 Architecture Pattern

```
Fragment (View) ──observes──> ViewModel ──calls──> Repository ──queries──> DAO ──> Room DB
                                                       │
                                                       └──> FirestoreDataSource ──> Cloud Firestore
```

### 3.2 Package Structure

```
com.wealthwise.app
├── WealthWiseApplication.java          # Application class, DynamicColors, WorkManager
├── ui/
│   ├── MainActivity.java               # Single activity, NavController, bottom nav
│   ├── auth/
│   │   ├── LoginFragment.java
│   │   ├── RegisterFragment.java
│   │   └── AuthViewModel.java
│   ├── dashboard/
│   │   ├── DashboardFragment.java
│   │   ├── DashboardViewModel.java
│   │   ├── RecentTransactionAdapter.java
│   │   └── BudgetProgressAdapter.java
│   ├── transactions/
│   │   ├── TransactionListFragment.java
│   │   ├── AddEditTransactionFragment.java
│   │   ├── TransactionAdapter.java
│   │   ├── TransactionViewModel.java
│   │   └── TransactionFilterBottomSheet.java
│   ├── budget/
│   │   ├── BudgetListFragment.java
│   │   ├── AddEditBudgetFragment.java
│   │   ├── BudgetAdapter.java
│   │   └── BudgetViewModel.java
│   ├── category/
│   │   ├── CategoryListFragment.java
│   │   ├── AddEditCategoryFragment.java
│   │   ├── CategoryAdapter.java
│   │   └── CategoryViewModel.java
│   ├── analytics/
│   │   ├── AnalyticsFragment.java
│   │   └── AnalyticsViewModel.java
│   ├── forecast/
│   │   ├── ForecastFragment.java
│   │   └── ForecastViewModel.java
│   ├── recurring/
│   │   ├── RecurringListFragment.java
│   │   ├── AddEditRecurringFragment.java
│   │   ├── RecurringAdapter.java
│   │   └── RecurringViewModel.java
│   ├── recommendation/
│   │   ├── RecommendationFragment.java
│   │   ├── RecommendationAdapter.java
│   │   └── RecommendationViewModel.java
│   └── settings/
│       ├── SettingsFragment.java
│       └── SettingsViewModel.java
├── data/
│   ├── local/
│   │   ├── AppDatabase.java            # Room database, version 1
│   │   ├── Converters.java             # Date, Enum type converters
│   │   ├── entity/                     # 5 entities + 3 relation classes
│   │   └── dao/                        # 5 DAOs
│   ├── remote/
│   │   ├── FirestoreDataSource.java    # Firestore CRUD
│   │   └── SyncManager.java           # Bidirectional sync
│   └── repository/                     # 6 repositories
├── engine/
│   ├── forecast/
│   │   ├── ForecastEngine.java         # Ensemble forecasting
│   │   ├── MovingAverageCalculator.java
│   │   ├── LinearRegressionCalculator.java
│   │   ├── WeightedAverageCalculator.java
│   │   └── SeasonalityDetector.java
│   ├── recommendation/
│   │   ├── RecommendationEngine.java   # 7 rule-based recommendations
│   │   ├── RuleBasedAdvisor.java
│   │   ├── SpendingAnalyzer.java
│   │   └── TrendDetector.java
│   └── recurring/
│       └── RecurringTransactionProcessor.java
├── worker/
│   ├── RecurringTransactionWorker.java  # Daily
│   ├── BudgetAlertWorker.java          # Every 12 hours
│   └── SyncWorker.java                # Every 6 hours
└── util/
    ├── Constants.java
    ├── CurrencyFormatter.java
    ├── DateUtils.java
    ├── NotificationHelper.java
    ├── PreferenceManager.java
    └── Resource.java
```

### 3.3 Navigation Map

```
Login ─────────────> Dashboard <─────────── Register
                         │
           ┌─────────────┼──────────────┐
           v             v              v
   Transaction List   Analytics     Settings
           │             │              │
           v             │              └─> Dark Mode, Currency, Export, Sync, Sign Out
   Add/Edit Transaction  │
           │             ├──> Pie Chart
           │             ├──> Bar Chart
           │             └──> Line Chart
           │
   ┌───────┴────────┐
   v                v
Budget List    Forecast
   │               │
   v               └──> 30/60/90 day projections
Add/Edit Budget
   │
   ├──> Category List ──> Add/Edit Category
   ├──> Recurring List ──> Add/Edit Recurring
   └──> Recommendations
```

**Bottom Navigation Tabs:** Dashboard | Transactions | Analytics | Settings

---

## 4. Functional Requirements

### 4.1 Authentication (FR-AUTH)

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-AUTH-01 | The system shall allow users to register with email and password | High |
| FR-AUTH-02 | The system shall allow users to sign in with email and password | High |
| FR-AUTH-03 | The system shall allow users to sign in with Google account | High |
| FR-AUTH-04 | The system shall allow users to continue as a guest without authentication | High |
| FR-AUTH-05 | The system shall redirect authenticated users to the dashboard on successful login | High |
| FR-AUTH-06 | The system shall allow users to sign out from the settings screen | High |
| FR-AUTH-07 | The system shall validate email format before submission | Medium |
| FR-AUTH-08 | The system shall require a minimum password length | Medium |
| FR-AUTH-09 | The system shall confirm password match during registration | Medium |
| FR-AUTH-10 | The system shall display appropriate error messages for auth failures | Medium |

### 4.2 Dashboard (FR-DASH)

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-DASH-01 | The dashboard shall display the user's total account balance | High |
| FR-DASH-02 | The dashboard shall display the current month's total income | High |
| FR-DASH-03 | The dashboard shall display the current month's total expenses | High |
| FR-DASH-04 | The dashboard shall display the 5 most recent transactions | High |
| FR-DASH-05 | The dashboard shall display current month budget progress as horizontal cards | High |
| FR-DASH-06 | The dashboard shall provide quick action buttons for: add income, add expense, budgets, and reports | Medium |
| FR-DASH-07 | The dashboard shall include a mini spending overview chart | Medium |
| FR-DASH-08 | The dashboard shall provide "View All" links to transactions and budgets | Medium |
| FR-DASH-09 | The dashboard shall show an empty state message when no transactions exist | Low |
| FR-DASH-10 | The dashboard shall refresh data when the fragment resumes | High |

### 4.3 Transaction Management (FR-TXN)

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-TXN-01 | The system shall allow creating transactions with: type (income/expense/transfer), amount, category, account, date, payee, and note | High |
| FR-TXN-02 | The system shall allow editing existing transactions | High |
| FR-TXN-03 | The system shall allow soft-deleting transactions (swipe to delete with undo) | High |
| FR-TXN-04 | The system shall display all transactions in a scrollable list ordered by date (newest first) | High |
| FR-TXN-05 | The system shall allow searching transactions by note or payee text | High |
| FR-TXN-06 | The system shall allow filtering transactions by type (All/Income/Expense) using chips | High |
| FR-TXN-07 | The system shall provide a bottom sheet filter with: date range, category selection, transaction type, and amount range | Medium |
| FR-TXN-08 | The system shall display each transaction with: category icon (colored), category name, payee/note, amount (color-coded), and date | High |
| FR-TXN-09 | The system shall update the associated account balance when a transaction is created, edited, or deleted | High |
| FR-TXN-10 | The system shall allow optionally marking a new transaction as recurring (with interval and end date) | Medium |
| FR-TXN-11 | Transaction type shall be one of: INCOME, EXPENSE, or TRANSFER | High |
| FR-TXN-12 | Amount must be a positive decimal number | High |

### 4.4 Category Management (FR-CAT)

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-CAT-01 | The system shall provide 10 default expense categories and 5 default income categories on first launch | High |
| FR-CAT-02 | The system shall allow creating custom categories with: name, type (expense/income), icon, and color | High |
| FR-CAT-03 | The system shall allow editing existing categories | High |
| FR-CAT-04 | The system shall allow soft-deleting categories | Medium |
| FR-CAT-05 | The system shall display categories in separate tabs for Expense and Income | High |
| FR-CAT-06 | Each category shall display: icon (with color background), name, and transaction count | High |
| FR-CAT-07 | The system shall provide a grid of selectable icons for category creation | Medium |
| FR-CAT-08 | The system shall provide a grid of selectable colors for category creation | Medium |
| FR-CAT-09 | Category name shall be limited to 30 characters | Low |

**Default Expense Categories:** Food & Dining, Transportation, Shopping, Bills & Utilities, Entertainment, Health & Fitness, Education, Personal Care, Home, Other Expense

**Default Income Categories:** Salary, Freelance, Investments, Gifts, Other Income

### 4.5 Budget Management (FR-BUD)

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-BUD-01 | The system shall allow creating monthly budgets per expense category with a spending limit | High |
| FR-BUD-02 | The system shall enforce unique budgets per category per month/year | High |
| FR-BUD-03 | The system shall allow editing existing budgets | High |
| FR-BUD-04 | The system shall allow soft-deleting budgets | Medium |
| FR-BUD-05 | The system shall display budgets for a selected month/year with navigation arrows | High |
| FR-BUD-06 | Each budget shall display: category icon/name, linear progress bar, spent/limit text, remaining amount, and percentage badge | High |
| FR-BUD-07 | The progress bar and remaining text shall be color-coded: green (< 80%), orange (80-99%), red (>= 100%) | High |
| FR-BUD-08 | The system shall support budget rollover (carry over unused amount to next month) | Medium |
| FR-BUD-09 | The system shall send notifications when a budget reaches 80% or more utilization | Medium |
| FR-BUD-10 | The system shall display an empty state when no budgets exist for the selected month | Low |

### 4.6 Recurring Transactions (FR-REC)

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-REC-01 | The system shall allow creating recurring transactions with: type, amount, category, account, interval, start date, optional end date, payee, and note | High |
| FR-REC-02 | Supported intervals shall be: Daily, Weekly, Biweekly, Monthly, Quarterly, Yearly | High |
| FR-REC-03 | The system shall automatically generate actual transactions when a recurring transaction is due | High |
| FR-REC-04 | The system shall process due recurring transactions daily via a background worker | High |
| FR-REC-05 | The system shall update the next occurrence date after each processing | High |
| FR-REC-06 | The system shall allow editing existing recurring transactions | Medium |
| FR-REC-07 | The system shall allow soft-deleting recurring transactions | Medium |
| FR-REC-08 | Each recurring item shall display: category icon, title (payee), interval, next occurrence date, amount, and active/inactive chip | High |
| FR-REC-09 | The system shall automatically deactivate a recurring transaction when it reaches its end date | Medium |
| FR-REC-10 | The system shall list recurring transactions ordered by next occurrence | Medium |

### 4.7 Analytics (FR-ANA)

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-ANA-01 | The system shall display spending data in a Pie Chart view (category breakdown for a single month) | High |
| FR-ANA-02 | The system shall display spending data in a Bar Chart view (income vs expense by month) | High |
| FR-ANA-03 | The system shall display spending data in a Line Chart view (balance trend over time) | High |
| FR-ANA-04 | The system shall allow switching between chart types via tabs | High |
| FR-ANA-05 | The system shall allow selecting the analysis period (3, 6, or 12 months) | Medium |
| FR-ANA-06 | Category summaries shall include: category name, color, total amount, and transaction count | High |
| FR-ANA-07 | Monthly snapshots shall include: month/year, total income, and total expense | High |

### 4.8 Financial Forecast (FR-FOR)

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-FOR-01 | The system shall project the user's balance for 30, 60, or 90 days ahead | High |
| FR-FOR-02 | The forecast shall use an ensemble model: 30% Moving Average + 40% Linear Regression + 30% Weighted Average | High |
| FR-FOR-03 | The forecast shall calculate a 95% confidence interval (z-score = 1.96) with upper and lower bounds | Medium |
| FR-FOR-04 | The system shall display projected balance in a hero card with change percentage | High |
| FR-FOR-05 | The system shall display a line chart of daily balance projections | High |
| FR-FOR-06 | The system shall display per-category forecasts with: category name, monthly average, projected amount, and change indicator (arrow + percentage) | Medium |
| FR-FOR-07 | The forecast shall use the last 12 months of transaction history as input data | High |
| FR-FOR-08 | The system shall allow switching forecast period via toggle buttons (30d / 60d / 90d) | Medium |

### 4.9 Recommendations (FR-REC-AI)

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-REC-AI-01 | The system shall generate rule-based financial recommendations | High |
| FR-REC-AI-02 | Recommendations shall be prioritized as HIGH, MEDIUM, or LOW | High |
| FR-REC-AI-03 | The system shall detect and alert on budget overruns | High |
| FR-REC-AI-04 | The system shall identify high-spend categories without budgets | Medium |
| FR-REC-AI-05 | The system shall detect spending spikes (unusual increases) | Medium |
| FR-REC-AI-06 | The system shall detect subscription creep (growing recurring costs) | Medium |
| FR-REC-AI-07 | The system shall identify upward spending trends | Medium |
| FR-REC-AI-08 | The system shall flag low savings rate (income vs expense ratio) | Medium |
| FR-REC-AI-09 | The system shall identify "latte factor" spending (many small transactions in a category) | Low |
| FR-REC-AI-10 | The system shall allow users to dismiss individual recommendations | Medium |
| FR-REC-AI-11 | Dismissed recommendations shall persist across sessions (SharedPreferences) | Medium |
| FR-REC-AI-12 | Each recommendation shall display: priority chip, title, description, action button, and dismiss button | High |

### 4.10 Settings (FR-SET)

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-SET-01 | The system shall allow toggling dark mode | High |
| FR-SET-02 | The system shall allow selecting a preferred currency from 12 supported options | High |
| FR-SET-03 | The system shall allow toggling budget alert notifications | Medium |
| FR-SET-04 | The system shall allow toggling recurring transaction reminder notifications | Medium |
| FR-SET-05 | The system shall allow exporting all transactions to CSV format | Medium |
| FR-SET-06 | The system shall allow triggering an on-demand sync with Firebase | Medium |
| FR-SET-07 | The system shall display the last sync timestamp | Low |
| FR-SET-08 | The system shall display the app version | Low |
| FR-SET-09 | The system shall allow signing out | High |

**Supported Currencies:** USD, EUR, GBP, JPY, CAD, AUD, CHF, CNY, INR, KRW, MYR, SGD

### 4.11 Cloud Sync (FR-SYNC)

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-SYNC-01 | The system shall sync data bidirectionally between Room and Firestore | High |
| FR-SYNC-02 | The sync strategy shall be last-write-wins based on timestamps | High |
| FR-SYNC-03 | The system shall push locally pending changes (inserts/updates/deletes) to Firestore | High |
| FR-SYNC-04 | The system shall pull remote changes from Firestore since the last sync time | High |
| FR-SYNC-05 | Background sync shall run every 6 hours when a network connection is available | Medium |
| FR-SYNC-06 | Sync shall be scoped to the authenticated user's Firestore path: `users/{userId}/` | High |
| FR-SYNC-07 | The system shall track sync status per entity (PENDING / SYNCED) | High |
| FR-SYNC-08 | The system shall sync: transactions, categories, budgets, recurring transactions, and accounts | High |
| FR-SYNC-09 | Guest users shall not have sync capabilities | High |

### 4.12 Notifications (FR-NOT)

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-NOT-01 | The system shall create notification channels on app startup | High |
| FR-NOT-02 | The system shall send budget alert notifications when a budget reaches 80% utilization | Medium |
| FR-NOT-03 | Budget alerts shall be checked every 12 hours via a background worker | Medium |
| FR-NOT-04 | The system shall request POST_NOTIFICATIONS permission on Android 13+ | High |

---

## 5. Data Requirements

### 5.1 Database Schema

**Database:** `wealthwise_db` (Room/SQLite, Version 1)

#### 5.1.1 `accounts` Table

| Column | Type | Constraints |
|--------|------|-------------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT |
| firebase_id | TEXT | |
| name | TEXT | NOT NULL |
| account_type | TEXT | NOT NULL |
| balance | REAL | NOT NULL, DEFAULT 0.0 |
| initial_balance | REAL | NOT NULL, DEFAULT 0.0 |
| currency_code | TEXT | DEFAULT 'USD' |
| sync_status | TEXT | DEFAULT 'PENDING' |
| is_deleted | INTEGER | DEFAULT 0 |
| created_at | INTEGER | Timestamp |
| updated_at | INTEGER | Timestamp |

#### 5.1.2 `categories` Table

| Column | Type | Constraints |
|--------|------|-------------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT |
| firebase_id | TEXT | |
| name | TEXT | NOT NULL |
| type | TEXT | NOT NULL (INCOME/EXPENSE/TRANSFER) |
| icon_name | TEXT | |
| color_hex | TEXT | |
| is_default | INTEGER | DEFAULT 0 |
| sync_status | TEXT | DEFAULT 'PENDING' |
| is_deleted | INTEGER | DEFAULT 0 |
| created_at | INTEGER | Timestamp |
| updated_at | INTEGER | Timestamp |

#### 5.1.3 `transactions` Table

| Column | Type | Constraints |
|--------|------|-------------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT |
| firebase_id | TEXT | |
| type | TEXT | NOT NULL (INCOME/EXPENSE/TRANSFER) |
| amount | REAL | NOT NULL |
| category_id | INTEGER | FK -> categories(id) ON DELETE SET NULL |
| account_id | INTEGER | FK -> accounts(id) ON DELETE SET NULL |
| date | INTEGER | Timestamp, INDEXED |
| note | TEXT | |
| payee | TEXT | |
| sync_status | TEXT | DEFAULT 'PENDING' |
| is_deleted | INTEGER | DEFAULT 0 |
| created_at | INTEGER | Timestamp |
| updated_at | INTEGER | Timestamp |

**Indices:** category_id, account_id, date, type

#### 5.1.4 `budgets` Table

| Column | Type | Constraints |
|--------|------|-------------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT |
| firebase_id | TEXT | |
| category_id | INTEGER | FK -> categories(id) ON DELETE CASCADE |
| limit_amount | REAL | NOT NULL |
| month | INTEGER | NOT NULL (1-12) |
| year | INTEGER | NOT NULL |
| rollover | INTEGER | DEFAULT 0 |
| sync_status | TEXT | DEFAULT 'PENDING' |
| is_deleted | INTEGER | DEFAULT 0 |
| created_at | INTEGER | Timestamp |
| updated_at | INTEGER | Timestamp |

**Indices:** category_id, UNIQUE(category_id, month, year)

#### 5.1.5 `recurring_transactions` Table

| Column | Type | Constraints |
|--------|------|-------------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT |
| firebase_id | TEXT | |
| type | TEXT | NOT NULL |
| amount | REAL | NOT NULL |
| category_id | INTEGER | FK -> categories(id) ON DELETE SET NULL |
| account_id | INTEGER | FK -> accounts(id) ON DELETE SET NULL |
| interval | TEXT | NOT NULL (DAILY/WEEKLY/BIWEEKLY/MONTHLY/QUARTERLY/YEARLY) |
| start_date | INTEGER | Timestamp, NOT NULL |
| end_date | INTEGER | Timestamp, NULLABLE |
| next_occurrence | INTEGER | Timestamp, INDEXED |
| note | TEXT | |
| payee | TEXT | |
| is_active | INTEGER | DEFAULT 1 |
| sync_status | TEXT | DEFAULT 'PENDING' |
| is_deleted | INTEGER | DEFAULT 0 |
| created_at | INTEGER | Timestamp |
| updated_at | INTEGER | Timestamp |

**Indices:** category_id, account_id, next_occurrence

### 5.2 Cloud Data Structure (Firestore)

```
users/{userId}/
├── transactions/{documentId}
│   └── { type, amount, categoryId, accountId, date, note, payee, updatedAt }
├── categories/{documentId}
│   └── { name, type, iconName, colorHex, isDefault, updatedAt }
├── budgets/{documentId}
│   └── { categoryId, limitAmount, month, year, rollover, updatedAt }
├── recurring_transactions/{documentId}
│   └── { type, amount, categoryId, interval, startDate, endDate, nextOccurrence, payee, isActive, updatedAt }
└── accounts/{documentId}
    └── { name, accountType, balance, initialBalance, currencyCode, updatedAt }
```

### 5.3 Local Preferences (SharedPreferences)

**File name:** `wealthwise_prefs`

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| currency | String | "USD" | Selected currency code |
| dark_mode | boolean | false | Dark theme enabled |
| notifications_enabled | boolean | true | Push notifications enabled |
| last_sync_time | long | 0 | Timestamp of last successful sync |
| budget_alert_threshold | int | 80 | Percentage threshold for budget alerts |
| first_launch | boolean | true | Whether this is the first app launch |
| user_id | String | null | Firebase user ID |

### 5.4 Seed Data

On first database creation, the system inserts:
- 10 default expense categories with predefined icons and colors
- 5 default income categories with predefined icons and colors
- 2 default accounts: "Cash" and "Bank Account"

---

## 6. Non-Functional Requirements

### 6.1 Performance

| ID | Requirement |
|----|-------------|
| NFR-PERF-01 | Database write operations shall execute on a background thread pool (4 threads) and never block the UI thread |
| NFR-PERF-02 | All list screens shall use RecyclerView with ViewHolder pattern for efficient scrolling |
| NFR-PERF-03 | Dashboard shall load and display data within 2 seconds of fragment creation |
| NFR-PERF-04 | Forecast calculations shall complete within 3 seconds for 12 months of data |

### 6.2 Reliability

| ID | Requirement |
|----|-------------|
| NFR-REL-01 | The application shall function fully offline with local Room database |
| NFR-REL-02 | Data integrity shall be maintained through foreign key constraints and unique indices |
| NFR-REL-03 | Soft-delete pattern shall prevent accidental data loss |
| NFR-REL-04 | Background workers shall use KEEP policy to prevent duplicate scheduling |
| NFR-REL-05 | Sync operations shall require network connectivity (WorkManager constraint) |

### 6.3 Usability

| ID | Requirement |
|----|-------------|
| NFR-USE-01 | The app shall follow Material Design 3 guidelines |
| NFR-USE-02 | The app shall support Material You dynamic colors on Android 12+ |
| NFR-USE-03 | The app shall support both light and dark themes |
| NFR-USE-04 | All interactive elements shall have content descriptions for accessibility |
| NFR-USE-05 | Form validation errors shall be displayed inline within TextInputLayout |
| NFR-USE-06 | Destructive actions (delete) shall provide an undo option via Snackbar |

### 6.4 Security

| ID | Requirement |
|----|-------------|
| NFR-SEC-01 | Firebase Authentication shall be used for user identity management |
| NFR-SEC-02 | Firestore data shall be scoped to individual users (`users/{userId}/`) |
| NFR-SEC-03 | No sensitive data shall be stored in plain text SharedPreferences beyond user preferences |
| NFR-SEC-04 | External storage permission is only requested on API 28 and below for CSV export |

### 6.5 Maintainability

| ID | Requirement |
|----|-------------|
| NFR-MNT-01 | MVVM architecture shall enforce separation of concerns |
| NFR-MNT-02 | Repository pattern shall abstract data source details from ViewModels |
| NFR-MNT-03 | All UI updates shall flow through LiveData observation (reactive) |
| NFR-MNT-04 | Database schema shall be versioned for future migration support |
| NFR-MNT-05 | Room schema export shall be enabled for migration validation |

### 6.6 Scalability

| ID | Requirement |
|----|-------------|
| NFR-SCA-01 | The database schema shall support unlimited transactions, categories, budgets, and accounts |
| NFR-SCA-02 | Indexed columns shall ensure query performance at scale |
| NFR-SCA-03 | Pagination-ready RecyclerView architecture for large datasets |

---

## 7. External Interface Requirements

### 7.1 User Interface

The application contains 16 screens organized across 4 bottom navigation tabs:

| Screen | Type | Navigation Tab |
|--------|------|---------------|
| Login | Fragment | Auth (no tabs) |
| Register | Fragment | Auth (no tabs) |
| Dashboard | Fragment | Dashboard |
| Transaction List | Fragment | Transactions |
| Add/Edit Transaction | Fragment | (pushed from list) |
| Transaction Filter | Bottom Sheet Dialog | (modal over list) |
| Budget List | Fragment | (from Dashboard/More) |
| Add/Edit Budget | Fragment | (pushed from list) |
| Category List | Fragment | (from More) |
| Add/Edit Category | Fragment | (pushed from list) |
| Analytics | Fragment + ViewPager2 | Analytics |
| Forecast | Fragment | (from More) |
| Recommendations | Fragment | (from More) |
| Recurring List | Fragment | (from More) |
| Add/Edit Recurring | Fragment | (pushed from list) |
| Settings | Fragment | Settings |

### 7.2 Hardware Interfaces

- No specific hardware interfaces required
- Standard Android touch input

### 7.3 Software Interfaces

| Interface | Purpose | Protocol |
|-----------|---------|----------|
| Firebase Auth | User authentication | HTTPS/gRPC |
| Cloud Firestore | Cloud data storage and sync | HTTPS/gRPC |
| Google Play Services | Google Sign-In | Android SDK |
| Android WorkManager | Background task scheduling | OS-level |
| Android NotificationManager | Push notifications | OS-level |

### 7.4 Communication Interfaces

| Interface | Details |
|-----------|---------|
| Internet | Required for auth and sync only |
| Network Type | WiFi or mobile data |
| Sync Protocol | Firestore SDK (auto-retry, offline persistence) |

---

## 8. Appendices

### 8.1 Application Constants

| Constant | Value | Description |
|----------|-------|-------------|
| SYNC_INTERVAL_HOURS | 6 | Background sync frequency |
| DEFAULT_CURRENCY | "USD" | Default currency code |
| BUDGET_WARNING_THRESHOLD | 80% | Orange warning level |
| BUDGET_DANGER_THRESHOLD | 100% | Red danger level |
| FORECAST_WEIGHT_MA | 0.30 | Moving average weight |
| FORECAST_WEIGHT_LR | 0.40 | Linear regression weight |
| FORECAST_WEIGHT_WA | 0.30 | Weighted average weight |
| CONFIDENCE_Z_SCORE | 1.96 | 95% confidence interval |
| MAX_RECENT_TRANSACTIONS | 5 | Dashboard recent list |
| CSV_DATE_FORMAT | "yyyy-MM-dd" | Export date format |

### 8.2 Background Worker Schedule

| Worker | Interval | Constraints | Policy |
|--------|----------|-------------|--------|
| RecurringTransactionWorker | 24 hours | Initial delay: 1 hour | KEEP |
| SyncWorker | 6 hours | Network: CONNECTED | KEEP |
| BudgetAlertWorker | 12 hours | None | KEEP |

### 8.3 Android Permissions

| Permission | Purpose |
|------------|---------|
| INTERNET | Firebase auth and Firestore sync |
| ACCESS_NETWORK_STATE | Check connectivity for sync |
| POST_NOTIFICATIONS | Budget alerts and reminders |
| RECEIVE_BOOT_COMPLETED | Reschedule workers after reboot |
| WRITE_EXTERNAL_STORAGE (max SDK 28) | CSV export on older devices |

### 8.4 Enum Definitions

**TransactionType:** INCOME, EXPENSE, TRANSFER

**SyncStatus:** PENDING, SYNCED

**RecurrenceInterval:** DAILY, WEEKLY, BIWEEKLY, MONTHLY, QUARTERLY, YEARLY

**RecommendationPriority:** HIGH, MEDIUM, LOW

### 8.5 CSV Export Format

```
Date,Type,Amount,Category,Payee,Note,Account
2026-02-07,EXPENSE,45.50,Food & Dining,Starbucks,Morning coffee,Cash
2026-02-06,INCOME,5200.00,Salary,Company Inc,,Bank Account
```

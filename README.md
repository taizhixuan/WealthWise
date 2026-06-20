# WealthWise

A personal finance app for Android. Track your income and expenses, set budgets, see where your money goes, and get recommendations on how to spend smarter.

## What it does

- **Track transactions** — log income and expenses with categories, notes, and payees
- **Budgets** — set monthly spending limits per category, get alerts when you're close to going over
- **Recurring transactions** — set up bills and subscriptions once, the app creates them automatically
- **Analytics** — pie charts for category breakdown, bar charts for monthly comparisons, line charts for trends over time
- **Forecasting** — projects your income and spending for the next 7, 30, or 90 days using a mix of moving averages, linear regression, and weighted averages
- **Recommendations** — analyzes your spending and flags things like budget overruns, spending spikes, subscription creep, and low savings rate
- **Cloud sync** — optional Firebase sync so your data stays consistent across devices
- **Offline first** — everything works locally with Room. Firebase sync runs in the background when you have internet

## Screen Screenshots

<p align="center">
  <img src="screenshots/dashboard.png" alt="Dashboard" width="270" />
  &nbsp;&nbsp;
  <img src="screenshots/analytics.png" alt="Analytics" width="270" />
  &nbsp;&nbsp;
  <img src="screenshots/transactions.png" alt="Add Transaction" width="270" />
</p>
<p align="center">
  <img src="screenshots/more.png" alt="Settings" width="270" />
</p> 

## How to build and run

**Requirements:**
- Android Studio (Arctic Fox or newer)
- JDK 17
- Android SDK with API 34 installed

**Steps:**

1. Clone the repo
   ```
   git clone <repo-url>
   cd WealthWise
   ```

2. Open the project in Android Studio and let Gradle sync

3. Set up Firebase (if you want cloud sync):
   - Create a project in [Firebase Console](https://console.firebase.google.com/)
   - Enable Email/Password and Google Sign-In under Authentication
   - Create a Firestore database
   - Download `google-services.json` and place it in the `app/` folder

4. Run the app:
   - Connect a device or start an emulator
   - Hit **Run** (or `Shift+F10`)

   Or from the terminal:
   ```
   ./gradlew installDebug
   ```

**Build APK only (no device needed):**
```
./gradlew assembleDebug
```
APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Background workers

The app schedules three background jobs:

| Worker | Interval | What it does |
|--------|----------|--------------|
| `RecurringTransactionWorker` | Every 24h | Creates transactions from recurring entries that are due |
| `SyncWorker` | Every 6h | Pushes pending local changes to Firestore, pulls remote changes |
| `BudgetAlertWorker` | Every 12h | Checks budgets and sends a notification if you've hit 80% or 100% |

## Database

Five main tables:

- **TransactionEntity** — every income/expense entry
- **CategoryEntity** — expense and income categories (comes with 15 defaults)
- **BudgetEntity** — monthly budget limits per category
- **RecurringTransactionEntity** — templates for auto-created transactions
- **AccountEntity** — accounts like Cash, Bank (comes with 2 defaults)

Sync status is tracked per row (`PENDING` / `SYNCED`) so the app knows what to push to Firebase.



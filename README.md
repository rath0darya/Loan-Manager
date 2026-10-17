# FundNiti

<p align="center">
  <strong>Private by design. Clear by nature.</strong><br/>
  An elegant, offline-first loan ledger for money lent, money borrowed, and every repayment in between.
</p>

<p align="center">
  <img alt="Platform" src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white"/>
  <img alt="Language" src="https://img.shields.io/badge/Kotlin-Native-7F52FF?logo=kotlin&logoColor=white"/>
  <img alt="UI" src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white"/>
  <img alt="Storage" src="https://img.shields.io/badge/Storage-Offline%20%7C%20Room-126B55"/>
  <img alt="Backend" src="https://img.shields.io/badge/Backend-None-success"/>
</p>

## ✨ The FundNiti experience

FundNiti helps you keep personal lending records organised without a cloud account or external service. A calm emerald-and-gold visual direction, clear balances, and quick access to each person's loan history make day-to-day tracking easier.

### Designed for everyday lending

- **One place for both directions** — track money you lent and money you borrowed.
- **Multiple people and loan records** — keep the parties, direction, principal, rate, dates, and notes for each record.
- **Daily-prorated interest** — calculate simple interest for full monthly periods and partial days.
- **Repayment ledger** — record payments, dates, and optional receipt references.
- **Outstanding balance** — review accrued interest and payments alongside the principal.
- **People overview** — combine a person's loan records and separate lent and borrowed totals.
- **Financial insights** — view portfolio split, balance trends, repayment activity, and monthly comparisons.
- **Search and reminders** — find records quickly and keep due dates visible.
- **Backup and restore** — create local automatic backups and export or restore backup files.
- **Offline-first privacy** — records are stored in a local Room database; no login, cloud database, or analytics service is required.

## 🎨 Modern adaptive appearance

FundNiti supports **System**, **Light**, and **Dark** appearance modes. On Android 12 and newer, the app uses Android dynamic colors from the user's wallpaper when available. In System mode, the app follows the device's light/dark setting and updates the status and navigation bars to match.

## 🧮 Interest calculation

FundNiti uses **simple interest on the original principal** at a fixed monthly rate. It does not compound interest and does not reduce the interest base when a repayment is entered. Recorded repayments are subtracted from principal plus accrued interest when displaying outstanding balance.

For principal P, monthly rate R%, and elapsed time:

- **Full monthly anniversary period:** monthly interest = P × R / 100.
- **Partial monthly period:** interest = monthly interest × elapsed days ÷ actual calendar days in that particular monthly anniversary period.
- **Total accrued interest:** full-period interest + prorated interest for the current partial period.

The monthly periods follow the loan's calendar-date anniversaries. Partial periods therefore use the actual length of that period, which may be 28, 29, 30, or 31 days. The app uses calendar dates in the device's local time zone and decimal arithmetic for the interest calculation. Accrued interest is rounded to the nearest paise (two decimal places, half-up).

**Example:** ₹10,000 at 2% per month earns ₹200 for one complete monthly period. If the current monthly period is 30 days long and 10 days have elapsed in it, that partial-period interest is ₹200 × 10 ÷ 30 = ₹66.67.

> Interest conventions differ between agreements and lenders. Confirm the method with the loan agreement before using the result as a settlement figure. FundNiti is a tracking tool, not financial or legal advice.

## 🔐 Device storage, recovery and backups

- The Room database remains the app's working database. Android sandboxes app databases, so the app cannot safely promise an undeletable, shared, live database file with read-only permissions.
- FundNiti automatically mirrors all loans and repayments to a portable JSON snapshot at `Android/media/com.rath0darya.loanmanager/FundNiti/backup/FundNiti-Data.json`.
- When FundNiti is installed again and the new in-app database is empty, it checks this shared Android/media snapshot and restores the records automatically if the file is present and readable.
- **One-tap backup:** open the Backup tab and tap **Back up all data now**. The app reads every loan and repayment, writes the snapshot to the phone folder, and displays the saved record counts and path in the app.
- Manual `.lendora` exports remain AES-GCM encrypted using Android Keystore. Those encrypted exports are tied to the installation/device key and generally cannot be restored after reinstalling or on another device.
- **Privacy note:** the automatic JSON snapshot is not encrypted, because a new installation must be able to read it without the old installation's Keystore key or a password. It contains sensitive financial records. Protect your device and do not share the file.
- The app has no delete-backup button and does not request broad storage permission; however, Android or a user/file manager can still delete files. No app can guarantee a file is undeletable after uninstall, storage cleanup, or device failure. Keep a second copy in a location you control.

## 🛠️ Build from source

**Requirements:** Android Studio, JDK 17, and an Android SDK compatible with the project configuration.

1. Clone this repository.
2. Open the project in Android Studio.
3. Allow Gradle to sync.
4. Run the app configuration on an Android device or emulator.

A GitHub Actions workflow builds the debug APK and, on success, places it at:

[Download/LoanManager.apk](Download/LoanManager.apk)

You can also follow the [latest GitHub Actions runs](https://github.com/rath0darya/Loan-Manager/actions).

## 🧱 Technology

- Kotlin
- Native Android
- Jetpack Compose and Material 3
- Room local database
- WorkManager for scheduled reminders
- Android Keystore and AES-GCM encrypted backups

## 📱 App details

| Property | Value |
|---|---|
| App name | FundNiti |
| Application ID | com.rath0darya.loanmanager |
| Data storage | On-device Room database |
| Network backend | None required |
| Interest method | Fixed monthly simple interest with daily proration for partial periods |

---

<p align="center"><strong>FundNiti — know what is lent, what is owed, and what comes next.</strong></p>

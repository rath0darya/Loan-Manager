<div align="center">

# FundNiti

### Private by design. Clear by nature.

An elegant, offline-first Android loan ledger for money lent, money borrowed, and every repayment in between.

[**Open the styled FundNiti experience ↗**](docs/index.html)

[**Download APK**](Download/LoanManager.apk) · [**View build status**](https://github.com/rath0darya/Loan-Manager/actions) · [**Browse source**](https://github.com/rath0darya/Loan-Manager)

</div>

<p align="center">
  <img alt="Platform" src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white">
  <img alt="Language" src="https://img.shields.io/badge/Kotlin-Native-7F52FF?logo=kotlin&logoColor=white">
  <img alt="UI" src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white">
  <img alt="Storage" src="https://img.shields.io/badge/Storage-Offline%20%7C%20Room-126B55">
  <img alt="Backend" src="https://img.shields.io/badge/Backend-None-success">
</p>

---

## ✨ Features

<table>
  <tr>
    <td width="50%"><h3>👥 People &amp; loans</h3>Track multiple people, loan direction, principal, monthly rate, dates, and notes.</td>
    <td width="50%"><h3>🧮 Interest calculation</h3>Fixed monthly simple interest with daily proration for partial monthly periods.</td>
  </tr>
  <tr>
    <td><h3>💸 Repayment ledger</h3>Keep payment dates, methods, transaction references, and notes together.</td>
    <td><h3>📊 Balances &amp; insights</h3>Review paid amounts, outstanding balances, portfolio totals, and monthly comparisons.</td>
  </tr>
  <tr>
    <td><h3>🔔 Search &amp; reminders</h3>Find loan records and schedule reminders for due dates.</td>
    <td><h3>🛡️ Backup &amp; restore</h3>Save local snapshots and export or restore backup files.</td>
  </tr>
</table>

## 🎨 Appearance

- **System, Light, and Dark** modes.
- Emerald accents and warm gold highlights.
- Android dynamic colours on supported Android versions.
- Native Jetpack Compose and Material 3 interface.

## 🧮 Interest method

FundNiti uses **simple interest on the original principal**. Interest is not compounded, and repayments do not reduce the interest calculation base.

- Full monthly anniversary period: `monthly interest = principal × monthly rate ÷ 100`.
- Partial monthly period: monthly interest × elapsed days ÷ actual days in that calendar-month anniversary period.
- Interest is rounded to the nearest paise (two decimal places, half-up).

**Example:** ₹10,000 at 2% per month earns ₹200 for one complete monthly period. If 10 days have elapsed in a 30-day monthly period, the prorated interest is ₹66.67.

> Interest conventions differ between agreements and lenders. Confirm the applicable method before using a result as a settlement figure. FundNiti is a tracking tool, not financial or legal advice.

## 🔐 Storage, privacy &amp; recovery

- The Room database is the app's working database and remains inside Android's app sandbox.
- Automatic backup path: `Android/media/com.rath0darya.loanmanager/FundNiti/backup/FundNiti-Data.json`.
- If the app is reinstalled and its new database is empty, FundNiti checks the Android/media snapshot and restores records if the file is present and readable.
- Open **Backup → Back up all data now** to create a current snapshot.
- Manual `.lendora` exports use AES-GCM encryption with Android Keystore and are generally tied to the original installation/device key.
- **Important:** the automatic JSON snapshot is unencrypted so a fresh installation can read it without the old Keystore key. It contains sensitive financial records. Protect your device and do not share the file.
- Keep a second backup copy in a location you control; no app can guarantee recovery after storage cleanup, device failure, or file deletion.

## 🛠️ Download or build

<div align="center">

| Get the app | Build from source |
|---|---|
| [**Download/LoanManager.apk**](Download/LoanManager.apk) | Android Studio + JDK 17 + compatible Android SDK |
| [Latest Actions runs](https://github.com/rath0darya/Loan-Manager/actions) | Clone the repo, sync Gradle, then run on a device or emulator |

</div>

## 🧱 Technology

- Kotlin · Native Android
- Jetpack Compose · Material 3
- Room local database
- WorkManager for scheduled reminders
- Android Keystore · AES-GCM encrypted manual backups

## 📱 App details

| Property | Value |
|---|---|
| App name | FundNiti |
| Application ID | `com.rath0darya.loanmanager` |
| Data storage | On-device Room database |
| Network backend | None required |
| Interest method | Fixed monthly simple interest with daily proration for partial periods |

---

<div align="center">

**FundNiti — know what is lent, what is owed, and what comes next.**

</div>

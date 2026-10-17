# Loan Manager

Offline-first native Android loan tracking app built with Kotlin and Jetpack Compose. All loan records stay on the device in a Room database; no external backend or account is required.

## Build

Open this repository in Android Studio (JDK 17), or use the GitHub Actions workflow to build a debug APK. The workflow places the APK at `Download/LoanManager.apk` in this repository after a successful build.

## Features

- Borrowed and lent loans, multiple people
- Fixed monthly interest and payment ledger
- Outstanding balance and due-date tracking
- Dashboard and statistics
- Search and filtering
- Local notifications/reminders
- JSON backup and restore using Android's file picker
- No external server, cloud database, or analytics

Package: `com.rath0darya.loanmanager`

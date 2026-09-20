# KK Reminder

An Android reminder app that keeps notifying you until you actually deal with the task.

Most reminder apps fire once and give up. KK Reminder re-notifies on an interval you choose, repeats tasks per-day or per-minute, and survives reboots and Direct Boot — so a task does not quietly disappear because you glanced at a notification and forgot about it.

**[Get it on Google Play](https://play.google.com/store/apps/details?id=com.hideaki.kk_reminder)**

> This is the public source mirror of the app published on Google Play. Credentials and signing material have been removed, so it does not build as-is — see [Building](#building).

---

## Features

**Persistent notification**
- Re-notify on a configurable interval until the task is handled
- Snooze from the notification, including a custom snooze duration that is remembered
- Exact alarms via `AlarmManager`, re-registered on boot
- Direct Boot aware — alarms still fire before the device is unlocked

**Task management**
- Tasks grouped into user-defined lists, with scheduled and non-scheduled sections
- Repeat per day (weekly, monthly, yearly, custom day-of-month/week patterns) with an optional end date
- Repeat per minute, with interval, count and duration
- Tags, with tag-based search
- Free-text search, sorting, cloning, bulk move between lists, and sharing
- Per-task notes with a plain edit mode and a checklist mode
- A separate "done" list

**Per-task customisation**
- Notification sound (including sounds from external storage), vibration pattern, and colour

**App-level**
- Google Drive backup and restore
- Dark theme, optionally following the system setting
- English and Japanese localisation
- Edge-to-edge display
- Accepts shared text from other apps (`ACTION_SEND`) and parses dates/times out of it
- Ads, removable by a one-time in-app purchase

---

## Tech stack

| | |
|---|---|
| Language | Java (no Kotlin) |
| Min SDK | 29 (Android 10) |
| Compile / Target SDK | 36 (Android 16) |
| Gradle / AGP | 9.4.1 / 9.2.1 |
| Version | 4.4.0.1 (`versionCode` 40400001) |
| Size | ~123 source files, ~31k lines |

Key dependencies: AndroidX (AppCompat, Preference, Activity, Media, Browser, CardView, ConstraintLayout), Material Components, Google Play Billing 9.1.0, Firebase Ads (AdMob), Google Drive REST API + Play Services Auth, Guava, Bolts Tasks.

---

## Project layout

Single Gradle module, `:app`, flat package `com.hideaki.kk_reminder`.

```
app/src/main/
├── java/com/hideaki/kk_reminder/
│   ├── MainActivity.java              Single-activity host: drawer, fragments, billing, ads
│   ├── ManuallySnoozeActivity.java    Transparent activity for the snooze picker
│   ├── *Fragment.java                 Screens (task list, task editor, settings, backup, …)
│   ├── *Receiver.java                 AlarmReceiver, StartupReceiver, DoneReceiver, …
│   ├── My*Adapter.java / *Adapter.java  ListView / ExpandableListView adapters
│   ├── Item.java, Tag.java, Notes.java, DayRepeat.java, MinuteRepeat.java, …
│   │                                  Serializable domain model (+ `*2`/`*3` migration variants)
│   ├── MyDatabaseHelper.java          SQLite schema
│   ├── DBAccessor.java                Database access
│   ├── DriveServiceHelper.java        Google Drive backup/restore
│   ├── UtilClass.java                 Shared constants and helpers
│   └── ColorPicker*.java, AnimCheckBox.java, PinnedHeaderExpandableListView.java
│                                      Vendored third-party widgets (see Licenses)
├── res/
│   ├── values/, values-ja/, values-night/   Strings (en/ja), themes, dark theme
│   ├── layout/, layout-night/
│   └── xml/                           PreferenceScreen definitions for every settings page
└── assets/                            About page (en/ja, light/dark) and update notes
```

**Data model.** Three SQLite tables — `todo`, `done`, `settings` — each row storing a Java-serialised blob rather than normalised columns. `MyDatabaseHelper` keeps two singletons: the normal database (`reminder.db`) and a Direct Boot copy (`reminder_copy.db`) in device-protected storage, which `StartupReceiver` reconciles once the device is unlocked.

**UI.** One activity hosting fragments behind a navigation drawer. Settings and the task editor are both built on `PreferenceFragmentCompat`, with custom `Preference` subclasses (`NotesPreference`, `MyCheckBoxPreference`, `TwoLineTitlePreference`, …) doing most of the work.

---

## What is not in this repository

This mirror is published without the credentials used to build and ship the Play Store release. Compared with the private development repository, the following are removed or masked:

| Item | Status |
|---|---|
| `app/google-services.json` | **Absent** — Firebase/AdMob configuration |
| `signingConfigs { … }` in `app/build.gradle` | **Removed** — keystore path, alias and passwords |
| `app_id` / `ad_unit_id` in `values/strings.xml` and `values-ja/strings.xml` | **Masked** as `******************` — AdMob unit IDs |
| `.idea/`, `.firebase/`, `.firebaserc`, release `.aab` | **Absent** — local IDE state, deploy cache, build output |

Everything else, including full commit history, is identical to the private repository.

---

## Building

**Prerequisites:** JDK 17 or later (Gradle 9.x refuses to run on anything older) and the Android SDK with API 36 installed. Create a `local.properties` pointing at your SDK:

```properties
sdk.dir=/path/to/Android/Sdk
```

Even then, a plain `./gradlew assembleDebug` **will fail**, because the Google Services plugin requires a configuration file that is not published here. To build your own copy:

**1. Supply a `google-services.json`.**
Create a Firebase project for the package name `com.hideaki.kk_reminder` (or change `applicationId` in `app/build.gradle` to one you own) and download its `google-services.json` into `app/`.

**2. Supply AdMob IDs.**
Replace the masked values in both string files with your own AdMob IDs, or with [Google's official test IDs](https://developers.google.com/admob/android/test-ads):

```xml
<!-- app/src/main/res/values/strings.xml and values-ja/strings.xml -->
<string name="app_id">ca-app-pub-3940256099942544~3347511713</string>
<string name="ad_unit_id">ca-app-pub-3940256099942544/9214589741</string>
```

The `app_id` must match the one in your `google-services.json`, or the app will crash on startup.

**3. Fix the debug signing config.**
`app/build.gradle` still contains `signingConfig signingConfigs.debug` under `buildTypes.debug`, but the matching `signingConfigs` block was stripped. Either delete that line to fall back to Gradle's default debug keystore, or add your own block:

```groovy
signingConfigs {
    debug {
        keyAlias 'your_alias'
        keyPassword 'your_password'
        storeFile file('/path/to/your.jks')
        storePassword 'your_store_password'
    }
}
```

**4. Build.**

```bash
./gradlew assembleDebug
```

The APK lands at `app/build/outputs/apk/debug/app-debug.apk`.

Google Drive backup/restore and the in-app purchase that disables ads are tied to the original Firebase project and Play Console listing, so they will not work against your own build without further setup.

---

## Licenses

The app bundles three third-party components, reproduced in full on the in-app *About this app* screen:

| Component | License | Copyright |
|---|---|---|
| [colorpicker](https://github.com/kristiyanP/colorpicker) (`petrov.kristiyan:colorpicker-library`) | MIT | 2016 Petrov Kristiyan |
| [PinnedHeaderExpandableListView](https://github.com/singwhatiwanna) | MIT | 2014 singwhatiwanna |
| AnimCheckBox | Apache 2.0 | 2017 Liaoguipeng |

These were originally Gradle dependencies; `colorpicker` was vendored into the repository once JCenter shut down.

The application's own source code is released under the [MIT License](LICENSE) — you are free to use, modify and redistribute it, provided the copyright notice and license text are retained.

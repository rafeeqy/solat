# Solat Roadmap

_Last updated: 2026-11-15_

Solat 1.0.1 shows the day's prayer times for a JAKIM zone, counts down to the next prayer and sends a notification at each one. A review of 1.0.1, checked on an emulator, found that the notifications aren't always right: a prayer you switch off still notifies, alarms are 8 hours late on a phone that isn't set to Malaysia time, and the first morning of a month can be missed. Version 1.1.0 fixes these, adds tests for them, and tidies up the build.

**Roles:** the project owner sets priorities, makes the product decisions and reviews each phase before it's merged. Claude does the engineering and reports at the end of each phase.

---

## Decisions

### Decided
| # | Decision | Outcome |
|---|---|---|
| D1 | Which version the fixes go into | **1.1.0** (versionCode 2), on a new `1.1.0` branch made from `1.0.1`. Phases 1–4 all go into 1.1.0. |
| D2 | How work is merged | **One branch per phase, merged locally** into `1.1.0`. Nothing is pushed. The owner reviews each branch before it's merged. |
| D3 | Docs | A roadmap (this file) and release notes in `docs/releases/`, and a README that says the project is vibe coded from 1.1.0 on. |

No decisions are open right now.

---

## Phases

Sizes: **S** is about one working session, **M** is 2–3 sessions. Each phase ends with a build that compiles and runs, and with a short report to the owner.

### Phase 0: Housekeeping (S)
- Set the version to 1.1.0 (versionCode 2).
- README: say the project is vibe coded, describe how it's made, and replace the location feature (the app doesn't use location) with how zones are actually chosen.
- Add this roadmap and the 1.1.0 release notes.

**Done when:** the app builds as 1.1.0 and the docs are in place.

### Phase 1: Notifications that are right and reliable (M)
1. **Switching a prayer off cancels its alarm.** ✅ `cancel()` looked for the alarm with a different intent from the one `schedule()` used, so it never found it. One function now builds the intent for both. The alarm receiver also checks the enabled prayers before posting, which covers alarms set by 1.0.x. *Checked on the emulator: switching Asr on adds tomorrow's 16:14 alarm, and switching it off removes it.*
2. **One time zone everywhere.** ✅ Prayer times were read in Malaysia time, but the countdown and alarms used the phone's time zone, so on a GMT phone the countdown pointed at a prayer that had passed and every alarm was 8 hours late. Everything that needs "now" or "today" now uses Malaysia time, through one `Clock` (which Phase 2's tests can also fix in place). *Checked on a GMT emulator: at 16:26 Malaysia time the countdown shows Maghrib in 2h25m, and the alarms are at 18:52, 20:05, 05:37 and 12:52 Malaysia time.*
3. **The month boundary.** ✅ On the last day of a month, the query for "today and tomorrow" returned the 1st and the last day of the *same* month, so the next month was never downloaded. The query now matches full dates, the repository downloads the month of whichever day is missing, and each month is saved in one database transaction. The countdown no longer freezes after the last loaded prayer, and it loads the new day's prayers at midnight. The monthly cleanup now deletes every month before the current one, not just last month. *Checked against the app's database: on 30 Nov the query returns only the 30th (it used to return 1 Nov as "tomorrow"), so December is downloaded.*
4. **Alarms that keep going.** ✅ When an alarm goes off, it schedules the next alarms through a background job. They're also scheduled again after a reboot, an app update, a manual clock change, and when exact alarms are allowed. A reboot no longer runs the work inside the receiver, where it could be cut off. The daily background job no longer waits for the battery to be above low (existing installs are updated too). Turning prayer times into alarms is now one use case (`RefreshPrayerAlarmsUseCase`), shared by the worker and the notification settings. *Checked on the emulator: after an app update and after a reboot, the alarms are scheduled again within about 30 seconds, without opening the app.*

Not checked on the emulator: an alarm actually going off and posting its notification, because that needs the emulator's clock moved forward. Phase 2's `PrayerAlarmHandlerTest` covers it instead.

**Done when:** each fix is checked on the emulator, and a switched-off prayer, a phone in another time zone and the last day of a month all behave correctly.

### Phase 2: Tests (S)
28 tests, all run on the JVM with `./gradlew testDebugUnitTest`. Robolectric 4.18 stands in for Android (Room, AlarmManager, notifications), so they don't need a phone or emulator. Versions match Pustaka's.
- `ScheduleNextPrayersUseCaseTest` ✅ (6): with a fake scheduler and a fixed clock. Covers each enabled prayer's next time, cancelling switched-off prayers (even with no times loaded), Malaysia time on a GMT phone, a prayer whose time is now, and after Isyak.
- `OfflineSolatAlarmSchedulerTest` ✅ (5): the real scheduler against Robolectric's AlarmManager. `cancel()` removes what `schedule()` set, keeps other prayers, scheduling again replaces, and the alarm carries the prayer and its time.
- `PrayerAlarmHandlerTest` ✅ (4): what happens when an alarm goes off. An enabled prayer posts "Maghrib (6:52 pm)", a switched-off prayer posts nothing, 1.0.x alarms show the time they carry, and other intents are ignored. To make this testable, the receiver's logic moved into `PrayerAlarmHandler`. The receiver keeps only `goAsync()` and queuing the next alarms.
- `SolatDaoTest` ✅ (7): an in-memory database. Covers a normal day, 30 Nov with only November saved, 30 Nov → 1 Dec, 31 Dec → 1 Jan, other zones, saving a month twice, and the cleanup.
- `OfflineSolatRepositoryTest` ✅ (6): with a fake API. Covers which months are downloaded on a normal day, at the end of a month and at the end of the year, offline behaviour, and Malaysia time on a GMT phone.
- The template example tests are removed. ✅

The tests were checked against the 1.0.1 bugs: with the old cancel intent, the old query and alarms in the phone's time zone put back, 9 of them fail.

**Done when:** `./gradlew testDebugUnitTest` passes. ✅

### Phase 3: Build and release (S)
- **Build time.** ✅ The time in Settings used the hour in place of the day ("04/11/2026" for a build on 15/11). Release builds now show the right date, e.g. "v1.1.0 (4:42 pm, 15/11/2026)", and debug builds show "v1.1.0 (debug)", so debug builds no longer recompile on every run.
- **`app/release/` out of git.** ✅ The 1.0.1 APK and baseline profiles are no longer tracked, and `app/.gitignore` ignores the folder. APKs are published through GitHub Releases. The files stay in the history of the `1.0.0` and `1.0.1` branches.
- **Version catalog.** ✅ Every library and plugin is now in `libs.versions.toml`, including the Hilt plugin. The Room compiler is 2.8.4, the same as the runtime (it was 2.8.3).
- **Compose BOM 2026.06.01.** ✅ The old BOM (2024.09.00) mixed versions: the Google Fonts library pulled Compose's runtime and text up to 1.11.3, while foundation stayed at 1.7.2 and Material 3 at 1.3.0. All of Compose is now 1.11.4, with Material 3 1.4.0. The newer BOMs (Compose 1.12) were left for a later version, because 1.12 is a bigger jump.
- **API responses.** ✅ Unknown fields are ignored, so a new field from the API can't stop prayer times from loading.
- **Old Android versions.** ✅ Checks for versions below Android 10 are removed: in the alarm scheduler (Phase 1), the notification channel and intent flags, and the notification settings shortcut. The Android 12 (exact alarms) and Android 13 (notification permission) checks stay, because the app still supports Android 10 and 11.

*Checked:* all 28 tests pass, and the debug build and the minified release build both succeed. The release build, signed with the local debug key, was installed on the emulator with its data cleared. It downloaded the zones and prayer times through R8-shrunk code, scheduled the right alarms, and the screens look the same as before the Compose update.

**Done when:** a release build succeeds and the version catalog holds every dependency. ✅

### Phase 4: Polish (S)
- A one-colour notification icon in place of the launcher icon.
- Move the English text written into the ViewModels into string resources, so it's translated with the rest of the app.
- Make sure the last zones in the list can scroll clear of the "Finished set up" button.
- The refresh button on the prayer screen does nothing yet (its action is a TODO). Make it download the prayer times again, or remove it.
- Add screenshots to the README's Demo section.

**Done when:** every phase above is merged and 1.1.0 is ready to release.

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
1. **Switching a prayer off cancels its alarm.** Today `cancel()` looks for the alarm with a different intent from the one `schedule()` used, so it never finds it. On the emulator, Asr's alarm stayed scheduled after Asr was switched off. One function will build the intent for both, and the alarm receiver will check the enabled prayers before posting a notification.
2. **One time zone everywhere.** Prayer times are read in Malaysia time, but the countdown and alarms use the phone's time zone. On a GMT emulator the countdown pointed at a prayer that had passed and every alarm was 8 hours late. Alarms will be scheduled from the API's own timestamps, and the countdown will use Malaysia time.
3. **The month boundary.** On the last day of a month, the query for "today and tomorrow" returns the 1st and the last day of the *same* month, so the next month is never downloaded and the next morning's alarms aren't set. The query will match exact dates. The month's details and its daily times will be saved in one database transaction, and the countdown will refresh itself at midnight.
4. **Alarms that keep going.** When an alarm goes off, it schedules that prayer's next one. Alarms are also scheduled again after a reboot (through a background job, so it isn't cut off), after the time or time zone changes, after an app update, and when exact alarms are allowed. The daily background job no longer waits for the battery to be above low.

**Done when:** each fix is checked on the emulator, and a switched-off prayer, a phone in another time zone and the last day of a month all behave correctly.

### Phase 2: Tests (S)
- Unit tests for `ScheduleNextPrayersUseCase`, with a fake scheduler and a fixed clock.
- Database tests for a normal day, 30 Nov → 1 Dec, and 31 Dec → 1 Jan.
- Remove the template example tests.

**Done when:** `./gradlew testDebugUnitTest` and the database tests pass.

### Phase 3: Build and release (S)
- Fix the build time shown in Settings, which uses the hour in place of the day ("04/11/2026" for a build on 15/11), and stop it changing the build on every run.
- Take `app/release/` out of git. APKs are published through GitHub Releases.
- Move dependencies into `libs.versions.toml`, update the Compose BOM, and give the Room compiler the same version as the Room runtime.
- Ignore unknown fields in API responses, so a new field from the API can't stop prayer times from loading.
- Remove code for Android versions older than the minimum (Android 10).

**Done when:** a release build succeeds and the version catalog holds every dependency.

### Phase 4: Polish (S)
- A one-colour notification icon in place of the launcher icon.
- Move the English text written into the ViewModels into string resources, so it's translated with the rest of the app.
- Make sure the last zones in the list can scroll clear of the "Finished set up" button.
- Add screenshots to the README's Demo section.

**Done when:** every phase above is merged and 1.1.0 is ready to release.

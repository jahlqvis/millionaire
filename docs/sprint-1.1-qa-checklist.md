# Sprint 1.1 QA Checklist

## Automated Checks

- [ ] Run `./gradlew :domain:domain-usecase:test`
- [ ] Run `./gradlew :app-tv:testDebugUnitTest`
- [ ] Run `./gradlew :app-tv:assembleDebug`
- [ ] Confirm APK is produced at `app-tv/build/outputs/apk/debug/app-tv-debug.apk`

## Android TV Manual QA

- [ ] Launch app on Android TV emulator or device
- [ ] Confirm Home screen renders correctly in dark game-show style
- [ ] Confirm primary CTA is clearly focused on first screen
- [ ] Navigate the entire app with D-pad only

## Player Setup

- [ ] Change player count between 1 and 4
- [ ] Confirm selected player count is visually clear
- [ ] Enter player names and verify text field focus is visible
- [ ] Leave one or more names blank and confirm defaults are applied
- [ ] Verify validation/error text is readable if triggered
- [ ] Verify `Tillbaka` returns to Home
- [ ] Verify `Starta spel` starts gameplay

## Gameplay

- [ ] Confirm round label and active player label are readable
- [ ] Confirm question panel is readable from TV distance
- [ ] Confirm all 4 answer options are clearly focusable
- [ ] Submit a correct answer and verify success feedback
- [ ] Submit a wrong answer and verify error feedback
- [ ] Confirm answer buttons lock during reveal state
- [ ] Confirm turn advances to next player after reveal
- [ ] Confirm rounds advance correctly

## Results

- [ ] Confirm winner or tie banner is clearly shown
- [ ] Confirm scoreboard ordering matches scores
- [ ] Confirm restart CTA is visible and focusable
- [ ] Confirm restart returns to Home and resets flow

## Regression / Navigation

- [ ] Verify no crashes during full 5-round game
- [ ] Verify back behavior is acceptable on each screen
- [ ] Verify app can start a second game after finishing the first
- [ ] Verify Swedish strings render correctly with proper characters

## Sign-off

- [ ] Automated checks passed
- [ ] Manual Android TV QA passed
- [ ] Any follow-up bugs filed as separate issues
- [ ] Sprint 1.1 ready to close

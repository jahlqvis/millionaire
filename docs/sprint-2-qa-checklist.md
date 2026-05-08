# Sprint 2 QA Checklist

## Automated Checks

- [ ] Run `./gradlew :domain:domain-usecase:test`
- [ ] Run `./gradlew :data:data-repository:testDebugUnitTest`
- [ ] Run `./gradlew :data:data-local-db:connectedDebugAndroidTest`
- [ ] Run `./gradlew :app-tv:testDebugUnitTest`
- [ ] Run `./gradlew :app-tv:connectedDebugAndroidTest`
- [ ] Run `./gradlew :app-tv:assembleDebug`

## Android TV Manual QA

- [ ] Launch app on Android TV emulator/device
- [ ] Confirm Home renders with `Starta spel` and `Historik`
- [ ] Confirm D-pad navigation works across all screens

## Lifeline QA (50/50)

- [ ] Start a new game and use `50/50` on first question
- [ ] Confirm removed answers are dimmed in place
- [ ] Confirm dimmed answers are not selectable
- [ ] Confirm `50/50` cannot be reused by same player
- [ ] Confirm next player can still use their own `50/50`

## Persistence + History QA

- [ ] Complete a full game and reach `Resultat`
- [ ] Restart to Home and open `Historik`
- [ ] Confirm latest game appears in history list
- [ ] Select history entry and verify player placements/scores
- [ ] Confirm empty state text appears when history is empty (fresh install/db clear)

## Audio QA

- [ ] Confirm app is stable when sound assets are missing
- [ ] Confirm answer/game-complete events do not crash app
- [ ] If raw audio files are added, verify selected/correct/wrong/complete playback once per event

## Regression / Navigation

- [ ] Confirm no crashes during full 5-round game
- [ ] Confirm back behavior remains acceptable
- [ ] Confirm app can run two games in a row without stale state
- [ ] Confirm Swedish strings render correctly with diacritics

## Sign-off

- [ ] Automated checks passed
- [ ] Manual TV QA passed
- [ ] Follow-up bugs captured as separate issues
- [ ] Sprint 2 ready to close

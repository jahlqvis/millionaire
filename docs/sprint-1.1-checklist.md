# Sprint 1.1 Implementation Checklist

Goal: refactor game orchestration into a ViewModel and polish the current Sprint 1 Android TV experience.

## 1) Core Refactor

- [ ] Add `GameUiState` model in `app-tv/.../tv/game/GameUiState.kt`
- [ ] Add `GameViewModel` in `app-tv/.../tv/game/GameViewModel.kt`
- [ ] Move start/answer/advance/finish orchestration from `MainActivity` into `GameViewModel`
- [ ] Keep `MainActivity` as navigation and screen wiring only

## 2) Screen State Cleanup

- [ ] Update `HomeScreen` to be presentation-only (state + callbacks)
- [ ] Update `PlayerSetupScreen` to be presentation-only
- [ ] Update `GameplayScreen` to be presentation-only
- [ ] Update `ResultsScreen` to be presentation-only
- [ ] Use explicit answer feedback state instead of ad-hoc nullable flow

## 3) Swedish Copy and Resources

- [ ] Move all user-facing hardcoded strings to string resources
- [ ] Normalize Swedish copy (`Fråga`, `Rätt svar!`, `poäng`, `krävs`)
- [ ] Quick review of seeded Swedish questions for consistency

## 4) TV UI Polish

- [ ] Add reusable TV UI primitives in `core/core-ui` (button/card/layout)
- [ ] Improve Home visual hierarchy and CTA readability
- [ ] Improve Player Setup focus flow for D-pad
- [ ] Improve Gameplay answer option focus/selection clarity
- [ ] Improve Results layout with clear winner and scoreboard hierarchy

## 5) Tests

- [ ] Add `GameViewModel` unit tests for orchestration flow
- [ ] Add 1 UI/integration happy-path test (start -> setup -> answer -> transition)

## 6) Verification

- [ ] Run tests: `./gradlew :domain:domain-usecase:test`
- [ ] Run app tests: `./gradlew :app-tv:testDebugUnitTest`
- [ ] Build APK: `./gradlew :app-tv:assembleDebug`
- [ ] Manual Android TV pass: focus, back behavior, readability, restart flow

## Suggested Commit Sequence

1. `refactor: extract game orchestration into GameViewModel`
2. `refactor: make screens state-driven`
3. `chore: move UI copy to resources`
4. `feat: add reusable TV UI primitives`
5. `feat: polish TV screens for Sprint 1.1`
6. `test: add GameViewModel and happy-path coverage`

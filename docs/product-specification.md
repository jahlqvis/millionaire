# Product Specification: Miljonaren TV

This document captures the agreed product vision and requirements. The current implementation does not yet satisfy every requirement below.

## Vision and audience

Miljonaren TV is a Swedish, family-friendly party quiz for friends and family playing together on one Android TV. It is inspired by a game-show format rather than a classic prize ladder. Use original branding, content and assets.

## Core gameplay

- Support 1–4 players, including solo play. Players share the TV remote and take individual turns.
- At setup, the host chooses 3, 5 or 10 rounds; default to 5. Each player gets one question per round and the same number of turns.
- At setup, the host chooses one bundled question theme for the whole game. The existing mixed questions are the default theme.
- A standard game should target 10–15 minutes. Longer configurations, especially 10 rounds, may take longer.
- Award one point for a correct answer and no points for an incorrect answer or timeout. Do not penalize mistakes.
- After an answer or timeout, show the correct answer and the outcome for 5 seconds, then advance automatically.
- The player with the highest score wins, subject to the tie-breaker rules below.

### Pause and resume

- Pressing Back during a game pauses it and returns to the main menu, where an option to resume is available.
- Resume returns to the same game state. Timers and answer-feedback countdowns do not advance while the game is paused.
- Starting a new game while one is paused requires confirmation before discarding the paused game.
- The paused game is available only while the app remains open. Closing or restarting the app loses the game.

### Optional timer

- The host can enable or disable a 30-second timer for each question; it is off by default.
- On timeout, award no point, reveal the correct answer and advance.
- Pause the timer while a lifeline result is displayed, then resume it.

### Tie-breakers

- If multiple players share the highest score, only the tied players enter the tie-breaker.
- In each tie-breaker round, each remaining player answers one question of comparable difficulty.
- Players who answer incorrectly or time out drop out, provided at least one remaining player answers correctly in that round. If everyone answers incorrectly or times out, nobody drops out.
- Continue until one player remains or three tie-breaker rounds have been played. If multiple players remain after three rounds, they share the win.
- Unused lifelines carry over; used lifelines do not refresh.

## Lifelines

Each player may use each lifeline once per game:

- **50/50:** Remove two incorrect answers.
- **Ask the audience:** Display a simulated audience vote as a bar chart with percentages for the four answers. Pressing Continue dismisses the result and resumes the timer.
- **Phone a friend:** Display a simulated friend's suggested answer and confidence on the TV.

Audience and friend suggestions should each be correct approximately 80% of the time, not guaranteed to be correct. If 50/50 has already been used on the question, their suggestions must be limited to the remaining answers while retaining the 80% chance of being correct.

## Questions and content

- UI and questions are in Swedish. Content must be family-friendly.
- Bundle a curated question library with the app; gameplay does not require internet access.
- Support multiple themed JSON question packs. Packs are added to the project and bundled into a new app build; on-device import is not required. See `docs/question-pack-format.md` for the manifest and pack format.
- Select a random mix of categories and difficulty levels, balancing difficulty across players within each round.
- Avoid reusing questions within a game when possible. Across games, avoid questions from the last 10 completed games when possible.
- If fresh questions run out, including during tie-breakers, reuse eligible questions from the hardest difficulty first, preferring the least recently used within that difficulty. Continue to balance difficulty between players where possible.
- Target 600 reviewed questions for the initial library, distributed across categories and difficulty levels. A four-player, 10-round game requires 40 questions; up to three tie-breaker rounds can add 12. Eleven maximum-length games could therefore use up to 572 questions, although the distribution of difficulties may still require reuse.
- Questions will be created and reviewed in a separate AI session, then added to the project as JSON files and bundled into app builds. Use a JSON format with stable question IDs, category, difficulty, question text, four answers and the correct answer. Validate imports for missing fields and duplicate questions or IDs within a pack. Recent-use tracking distinguishes questions from different themes.

## Results, history and audio

- Show the winner or shared winners and the players' scores at the end of a game.
- Store recent completed games locally, including player names, scores and placements, and make them available in the history screen.
- Make the history retention limit configurable in settings, with a default of 100 games. Players can clear their game history.
- Track question use separately from displayed game history, so clearing history or changing its retention limit does not inadvertently reset repeat avoidance.
- Include sound effects for gameplay events, with an option to mute them.

## Delivery

### First release

Deliver the core rules, selectable rounds, optional timer, pause and resume, tie-breakers, all three lifelines, expanded offline question library, configurable game history and optional sound effects.

Verify the full experience on an Android TV emulator and at least one physical Android TV or Google TV device. Check remote-only navigation, offline play, complete games, pause and resume, timers, lifelines, tie-breakers and history. Add automated checks for game rules and verify the currently declared minimum Android version, Android 7.0 (API 24), rather than assuming compatibility.

The intended initial distribution is private installation on family and friends' Android TV devices.

### Later phases

Expand content and prioritize further improvements based on playtesting. No online features or additional game modes are required for the first release.

## Open decisions

- Question category and difficulty distribution, and the review process for accuracy and family suitability.

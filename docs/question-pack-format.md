# Bundled question pack format

The app offers one selectable theme per bundled question pack. The existing **Blandat** theme reads `data/data-local-content/src/main/resources/questions.json`; it contains the original 25-question seed, not the 600-question content target. Players choose one theme when setting up a game.

## Add a theme

1. Create a UTF-8 JSON question pack in `data/data-local-content/src/main/resources/question-packs/`, using the format below. For example: `space.json`.
2. Add an entry to `data/data-local-content/src/main/resources/question-packs.json`:

   ```json
   { "id": "space", "nameSv": "Rymden", "resource": "/question-packs/space.json" }
   ```

   Add it to the manifest's `packs` array alongside the existing `general` entry. IDs must be unique, stable, lowercase and use only letters `a`–`z`, digits, `_` or `-`. Swedish display names must be unique. Resource filenames use letters, digits, `_` or `-` followed by `.json`.
3. Run `./gradlew :data:data-local-content:test` and rebuild the app. Adding packs on the TV without rebuilding is not supported.

Question IDs must be unique within a pack and should stay stable across releases. IDs can be reused in another pack: recent-use tracking distinguishes them by theme. Previous use of the original pack is retained under the `general` ID; do not change that ID when updating the existing pack.

## Question pack format

Use UTF-8 JSON in this format:

```json
{
  "version": 1,
  "questions": [
    {
      "id": "example-001",
      "textSv": "Vad är huvudstaden i Sverige?",
      "category": "Geografi",
      "difficulty": "EASY",
      "options": [
        { "id": "A", "textSv": "Stockholm" },
        { "id": "B", "textSv": "Göteborg" },
        { "id": "C", "textSv": "Malmö" },
        { "id": "D", "textSv": "Uppsala" }
      ],
      "correctOptionId": "A"
    }
  ]
}
```

- Keep IDs stable and unique within the pack; do not reuse an ID for a different question in the same pack.
- All text must be nonempty and Swedish. Difficulty must be `EASY`, `MEDIUM` or `HARD`.
- Each question must have exactly four distinct answers with IDs `A`, `B`, `C`, `D`, and `correctOptionId` must refer to one of them.
- Duplicate IDs, question text and answer text are rejected case-insensitively. The validation also rejects invalid versions, missing fields and malformed choices.
- Validation cannot determine factual accuracy, family suitability or copyright status: review those separately before adding content.

Run `./gradlew :data:data-local-content:test` to validate the manifest, every bundled pack and the parser. The app also validates them when it loads.

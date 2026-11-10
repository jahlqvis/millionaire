# Bundled question pack format

The app reads `data/data-local-content/src/main/resources/questions.json`. Add reviewed questions to this file before building the app; importing files on the TV is not supported. The current pack is the original 25-question seed, not the 600-question content target.

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

- Keep IDs stable and unique across the entire pack; do not reuse an ID for a different question.
- All text must be nonempty and Swedish. Difficulty must be `EASY`, `MEDIUM` or `HARD`.
- Each question must have exactly four distinct answers with IDs `A`, `B`, `C`, `D`, and `correctOptionId` must refer to one of them.
- Duplicate IDs, question text and answer text are rejected case-insensitively. The validation also rejects invalid versions, missing fields and malformed choices.
- Validation cannot determine factual accuracy, family suitability or copyright status: review those separately before adding content.

Run `./gradlew :data:data-local-content:test` to validate the bundled pack and parser. The app also validates the pack when it loads.

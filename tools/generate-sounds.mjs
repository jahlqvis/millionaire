// Generate original, short PCM effects without third-party audio assets.
// Run from any directory with: node tools/generate-sounds.mjs
import { mkdirSync, writeFileSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const root = resolve(dirname(fileURLToPath(import.meta.url)), "..");
const output = resolve(root, "core/core-audio/src/main/res/raw");
const sampleRate = 22050;
const effects = {
  answer_selected: [[740, 0.09]],
  answer_correct: [[660, 0.10], [880, 0.16]],
  answer_wrong: [[330, 0.14], [220, 0.20]],
  game_complete: [[523.25, 0.12], [659.25, 0.12], [783.99, 0.12], [1046.5, 0.28]],
};

function wav(notes) {
  const samples = [];
  for (const [frequency, duration] of notes) {
    const count = Math.round(sampleRate * duration);
    for (let index = 0; index < count; index++) {
      const time = index / sampleRate;
      const fadeIn = Math.min(1, time / 0.008);
      const fadeOut = Math.min(1, (count - index - 1) / (sampleRate * 0.03));
      const tone = Math.sin(2 * Math.PI * frequency * time);
      samples.push(Math.round(tone * fadeIn * fadeOut * 0.3 * 32767));
    }
  }
  const buffer = Buffer.alloc(44 + samples.length * 2);
  buffer.write("RIFF", 0);
  buffer.writeUInt32LE(buffer.length - 8, 4);
  buffer.write("WAVEfmt ", 8);
  buffer.writeUInt32LE(16, 16);
  buffer.writeUInt16LE(1, 20);
  buffer.writeUInt16LE(1, 22);
  buffer.writeUInt32LE(sampleRate, 24);
  buffer.writeUInt32LE(sampleRate * 2, 28);
  buffer.writeUInt16LE(2, 32);
  buffer.writeUInt16LE(16, 34);
  buffer.write("data", 36);
  buffer.writeUInt32LE(samples.length * 2, 40);
  samples.forEach((sample, index) => buffer.writeInt16LE(sample, 44 + index * 2));
  return buffer;
}

mkdirSync(output, { recursive: true });
for (const [name, notes] of Object.entries(effects)) {
  writeFileSync(resolve(output, `${name}.wav`), wav(notes));
}

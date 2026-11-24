# Architecture notes

Recall is a single-activity Android app. Jetpack Compose renders the screens and owns the current screen state; a small repository persists the question bank locally. The starter content is Kotlin data rather than a remote service, so the app can be used offline after installation.

## Source layout

| File | Responsibility |
| --- | --- |
| `MainActivity.kt` | Activity entry point, Compose theme and screens, topic selection, answer feedback, navigation, and add/edit/delete dialogs. |
| `QuizQuestion.kt` | Immutable question model: stable ID, topic, prompt, four choices, correct-choice index, and explanation. |
| `QuestionBank.kt` | Seven topic names and the 140 built-in questions. Each topic has twenty questions; each question has one correct choice and three distractors. |
| `QuizQuestionRepository.kt` | Loads and saves the question list in app-private `SharedPreferences`, including first-launch initialization and malformed-data recovery. |
| `AndroidManifest.xml` | Declares the application and launcher activity. The app does not request network or runtime permissions. |
| `docs/USER_GUIDE.md` | Instructions for running on a phone and using the quiz. |

## Model and invariants

`QuizQuestion` is a value object. Its `options` list contains four distinct strings, and `correctOptionIndex` must identify one of those strings. The repository validates these properties when it reads saved JSON. The editor applies the same rules before saving a new or edited question. Questions have an ID so editing, deleting, and selection can identify an item even when the question text changes.

The seven topic strings are defined once in `QuestionBank.topics` and used by the topic picker, editor, and repository validation. The initial bank contains exactly 20 questions for each topic (140 total). Users can grow or reduce those counts by adding or deleting questions; topic counts on the home screen are derived from the current saved data.

## Startup and persistence

1. `MainActivity` creates `QuizQuestionRepository` with the application context.
2. The repository checks the `quiz_questions_v2` preference key. If it is absent, it saves and returns `QuestionBank.starterQuestions`.
3. If the key contains a JSON array, the repository parses each item and keeps only questions with a recognized topic, a non-empty prompt, exactly four distinct non-empty options, and a valid correct-choice index.
4. If the JSON itself cannot be parsed, the repository restores the starter bank. A valid saved empty array remains empty, so intentionally deleting all questions does not seed them again on the next launch.
5. When the user adds, edits, or deletes a question, the UI sends the updated list to `saveQuestions`. The repository writes the full array with `SharedPreferences.Editor.apply()`.

Preferences are private to the app. They survive normal app restarts and updates, but clearing app storage or uninstalling removes them. There is no account, server, or sync behavior.

## UI state and interactions

The app switches between a topic list and a quiz for the selected topic. For a quiz, the visible list is filtered from the saved bank. The selected question index is clamped when the list changes so deleting the last item does not leave the screen pointing past the end. Moving to another question or topic clears the selected answer so the next question begins unanswered.

Selecting a choice displays immediate correct/incorrect feedback, highlights the correct choice, and shows the question explanation. Previous and Next move within the current topic. The topic action returns to topic selection. Add opens a blank editor; Edit pre-fills the current question; Delete requires confirmation. Saving an edited question preserves its ID, while a new question receives a generated ID.

## Build configuration

- Android Gradle Plugin: 8.13.2
- Kotlin: 2.4.21, using the `compilerOptions` DSL for the JVM target
- Compose BOM: 2026.06.00
- Compile/target SDK: 36
- Minimum SDK: 24 (Android 7.0)
- Java/Kotlin bytecode target: 17
- Gradle wrapper: 8.14.4

Version declarations live in `gradle/libs.versions.toml`; Android module settings live in `app/build.gradle.kts`. The root README documents the build and installation steps.

## Extension guidance

For larger data sets, consider moving persistence to Room and UI actions into a ViewModel. Keep validation at the persistence boundary as well as in the editor. If questions are imported, validate topic names, question sizes, option count, correct-choice index, duplicate IDs, and payload limits before replacing saved data. If adding cloud sync, define conflict handling and authentication rather than treating local preferences as a shared database.

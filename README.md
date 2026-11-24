# Recall — Kotlin Topic Quiz App

Recall is an Android multiple-choice study app built with Kotlin and Jetpack Compose. It includes seven selectable topics, twenty starter questions per topic, and four choices for every question. The app checks an answer immediately, explains the result, and saves question edits locally on the device.

## Included topics

| Topic | Starter questions |
| --- | ---: |
| Cloud | 20 |
| AI-ML | 20 |
| Backend | 20 |
| System Design | 20 |
| HTML | 20 |
| CSS | 20 |
| JavaScript | 20 |
| **Total** | **140** |

## Features

- Select a topic from the home screen and see its current question count.
- Answer each question by choosing one of four options.
- See whether the choice was correct, the correct answer when needed, and a short explanation.
- Navigate through a topic with **Previous** and **Next** and view progress.
- Add, edit, and delete questions. The editor requires a prompt, four distinct non-empty choices, and one marked correct answer.
- Choose a topic for each new or edited question.
- Save the question bank locally in app-private storage. No account, network connection, or runtime permission is required while using the app.
- Start with 20 curated questions per topic. The displayed count changes when questions are added or deleted.

## Requirements

- Android Studio with Android SDK Platform 36 installed.
- JDK 17 (Android Studio's bundled JDK is suitable).
- The included Gradle wrapper bootstraps Gradle 8.14.4; no separate Gradle installation is needed.
- Internet access the first time Gradle syncs, so it can resolve the Gradle distribution, Android Gradle Plugin, and AndroidX dependencies.
- An Android device or emulator running Android 7.0 (API 24) or later.

The project uses Android Gradle Plugin 8.13.2, Kotlin 2.4.21, Compose BOM 2026.06.00, and Activity Compose 1.13.0. Versions are defined in `gradle/libs.versions.toml`.

## Open and run

1. Open Android Studio and choose **Open**.
2. Select the project root folder containing `settings.gradle.kts`.
3. Allow Gradle to sync and install any missing SDK components it requests.
4. Start an emulator or connect an Android device with USB debugging enabled.
5. Select the `app` run configuration and press **Run**.

To build from a PowerShell terminal:

```powershell
.\gradlew.bat :app:assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

## Using Recall

### Choose a topic and answer questions

1. Select **Cloud**, **AI-ML**, **Backend**, **System Design**, **HTML**, **CSS**, or **JavaScript**.
2. Read the question and tap one of the four choices.
3. Review the correct/incorrect feedback and explanation.
4. Use **Next** and **Previous** to move within the selected topic. The progress indicator shows the current question and total for that topic.
5. Tap **Topics** in the app bar to return to the topic list.

### Create or change a question

Tap **＋ Add** in the app bar. Choose a topic, enter the question, fill all four distinct choices, and mark the correct one with its radio button. An explanation is optional. Save the question to open it immediately.

To edit or delete a question, open it and use **Edit question** or **Delete** below the answers. Delete asks for confirmation. Edits and changes are saved on the device.

## Further documentation

- [User guide](docs/USER_GUIDE.md) — topic selection, answering, and question management.
- [Architecture notes](docs/ARCHITECTURE.md) — question model, seed bank, persistence, and state flow.

## Project layout

```text
.
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/codealpha/flashcards/
│       │   ├── MainActivity.kt
│       │   ├── QuestionBank.kt
│       │   ├── QuizQuestion.kt
│       │   └── QuizQuestionRepository.kt
│       └── res/values/
│           ├── strings.xml
│           └── themes.xml
├── docs/
│   ├── ARCHITECTURE.md
│   └── USER_GUIDE.md
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── gradlew
├── gradlew.bat
├── build.gradle.kts
├── gradle.properties
└── settings.gradle.kts
```

## Design and implementation

### Quiz screen

The home screen groups the seven topics into selectable tiles. The quiz screen shows one prompt at a time, four answer choices, immediate feedback, a short explanation, progress, and navigation. The answer order rotates across the starter bank so the correct choice is not always in the same position.

### Data model and seed content

`QuizQuestion` stores a stable ID, topic, prompt, four options, the correct option index, and an optional explanation. `QuestionBank` supplies exactly twenty starter questions for each of the seven requested topics (140 total). Its helper assigns the correct choice to a rotating option position.

### Persistence and editing

`QuizQuestionRepository` serializes questions to JSON in app-private `SharedPreferences`. A missing quiz-data key seeds the starter bank. A saved empty array remains empty, so deleting the final question does not silently recreate deleted content. The editor enforces four non-empty, distinct choices and records the selected correct answer.

## Configuration

- **Application ID / namespace:** `com.codealpha.flashcards`
- **Minimum Android version:** API 24 (Android 7.0)
- **Compile and target SDK:** API 36
- **JVM target:** Java 17
- **Storage:** app-private local preferences; no network permission is declared.

To change build or dependency versions, edit `app/build.gradle.kts` and `gradle/libs.versions.toml`.

## Troubleshooting

- **Gradle sync cannot find an SDK:** Install Android SDK Platform 36 from **Tools → SDK Manager**.
- **The first wrapper run cannot start:** Check network access so Gradle 8.14.4 can be downloaded from `services.gradle.org`, then retry.
- **Gradle cannot resolve dependencies:** Check network access and retry sync. The first build downloads plugins and libraries from Google's Maven repository and Maven Central.
- **A different JDK is selected:** Set **Gradle JDK** to JDK 17 under **Settings → Build, Execution, Deployment → Build Tools → Gradle**.
- **A topic shows fewer than 20 questions:** The app starts each topic with 20; edits and deletions change the saved count.
- **Questions appear reset:** Clearing app storage or uninstalling Recall removes its local question bank.

## Possible extensions

Natural next additions include session scoring, question shuffle, difficulty levels, search, import/export, accessibility review, and automated UI or repository tests.

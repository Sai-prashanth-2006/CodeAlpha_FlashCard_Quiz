# Recall — CodeAlpha App Development Internship

**Project:** Task 1 — Flashcard Quiz App  
**Program:** CodeAlpha App Development Internship  
**Platform:** Android  
**Language:** Kotlin

Recall is a topic-based Android quiz app developed for the CodeAlpha App Development Internship. It helps learners practice multiple-choice questions in seven software and web development topics. The app begins with 140 questions, gives immediate answer feedback, and lets users maintain their own question bank.

## Project objectives

This project demonstrates how to build a simple, usable Android learning app that:

- Organizes study questions by topic.
- Presents one question and four answer choices at a time.
- Identifies the correct answer and explains it.
- Supports question creation, editing, and deletion.
- Saves changes locally so they remain available after the app is closed.
- Uses Kotlin and Jetpack Compose for a native Android interface.

## Topics and starter question bank

Each topic starts with 20 questions. Every question has four distinct options, one correct answer, and a short explanation.

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

The number shown for a topic reflects its current saved questions. Users can add or delete questions, so the count can change from the initial 20.

## Features

- **Topic selection:** Choose a topic from the home screen.
- **Multiple-choice practice:** Answer each question by selecting one of four options.
- **Immediate feedback:** See whether the selected option is correct, view the correct answer when needed, and read an explanation.
- **Question navigation:** Move forward or backward within the selected topic and follow progress.
- **Question management:** Add, edit, and delete questions.
- **Input validation:** A saved question must have a prompt, four non-empty and distinct options, and one selected correct answer. An explanation is optional.
- **Local persistence:** The question bank is saved in app-private storage on the device.
- **Offline use:** After installation, the quiz does not need an account or network connection.

## Technology stack

| Component | Technology |
| --- | --- |
| Programming language | Kotlin |
| Android UI | Jetpack Compose |
| UI components | Material 3 |
| Build system | Gradle Wrapper with Kotlin DSL |
| Local persistence | Android SharedPreferences with JSON serialization |
| Java/Kotlin target | JDK 17 |
| Minimum Android version | Android 7.0 (API 24) |

The project is configured with Android Gradle Plugin 8.13.2, Kotlin 2.4.21, Compose BOM 2026.06.00, Activity Compose 1.13.0, and Gradle 8.14.4. Dependency versions are catalogued in gradle/libs.versions.toml.

## Application design

The app uses a single Activity and Compose-based screens. The main screen switches between the topic list and the selected topic's quiz. A repository handles persistence so the UI does not need to encode JSON or preference details.

| File | Responsibility |
| --- | --- |
| app/src/main/java/com/codealpha/flashcards/MainActivity.kt | Activity setup, Compose UI, topic and question navigation, answer feedback, and add/edit/delete flows. |
| app/src/main/java/com/codealpha/flashcards/QuizQuestion.kt | Data model for a question, its topic, four options, correct-option index, and explanation. |
| app/src/main/java/com/codealpha/flashcards/QuestionBank.kt | Topic names and the 140 built-in starter questions. |
| app/src/main/java/com/codealpha/flashcards/QuizQuestionRepository.kt | Loads and saves the local question list. |
| app/src/main/AndroidManifest.xml | App metadata and launcher activity declaration. |
| docs/USER_GUIDE.md | Phone setup and instructions for using Recall. |
| docs/ARCHITECTURE.md | Model, state, persistence, and implementation notes. |

### Repository layout

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

### Question lifecycle

1. On first launch, the repository sees that no quiz question data has been saved and initializes the bank with the 140 starter questions.
2. The user selects a topic and answers questions in that topic.
3. When the user adds, edits, or deletes a question, the updated list is written to app-private preferences.
4. On a later launch, the app loads the saved question bank. A saved empty list remains empty rather than being reseeded.

Correct options rotate through the four positions in the built-in question bank so they are not all in the same place. The editor preserves an existing question ID when editing and assigns a new ID when adding a question.

## Requirements

- Android Studio with Android SDK Platform 36.
- JDK 17. Android Studio's bundled JDK is suitable.
- Android 7.0 / API 24 or later on a physical device or emulator.
- Internet access for the first Gradle sync/build to download the Gradle distribution, plugins, and dependencies.
- For a physical phone: a USB data cable and USB debugging enabled.

You do not need to install Gradle separately; use the included Gradle Wrapper.

## Run in Android Studio

1. Open Android Studio and select **Open**.
2. Choose the project root directory, the folder containing settings.gradle.kts.
3. Wait for Gradle sync to complete. Install Android SDK Platform 36 if Android Studio requests it.
4. Start an Android emulator, or connect a phone with USB debugging enabled.
5. Select the **app** run configuration and the desired device.
6. Press **Run**.

### Run on a physical Android phone

1. On the phone, open **Settings → About phone** and tap **Build number** seven times to enable Developer options. Menu names can vary by manufacturer.
2. Open **Developer options** and turn on **USB debugging**.
3. Connect the unlocked phone to the computer using a data-capable USB cable.
4. Accept the phone prompt to authorize USB debugging from this computer.
5. In Android Studio, select the phone in the device selector and press **Run**.

See [docs/USER_GUIDE.md](docs/USER_GUIDE.md) for additional phone setup guidance and troubleshooting.

## Build or install from PowerShell

Open PowerShell in the project root, the folder containing gradlew.bat.

Build a debug APK:

    .\gradlew.bat :app:assembleDebug

The APK is generated at:

    app/build/outputs/apk/debug/app-debug.apk

To install and launch the app on a connected, authorized phone:

    .\gradlew.bat :app:installDebug

Use gradlew.bat as a single filename; do not insert a directory separator before .bat. The first build may take longer while Gradle downloads required files.

## Use the app

1. Choose one of the seven topics.
2. Read the question and select an answer.
3. Review the correct/incorrect feedback and explanation.
4. Use **Previous** or **Next** to move within the topic. The progress display shows the current position.
5. Use **Topics** to return to topic selection.
6. Select **＋ Add** to create a question. Choose its topic, enter the prompt and four distinct options, mark the correct choice, and optionally enter an explanation.
7. Open a question and select **Edit question** to change it, or **Delete** to remove it. Deletion requires confirmation.

## Data and privacy

The saved question bank is stored in app-private preferences on the phone. The application does not require an account and does not sync data to a server. It declares no internet permission for app functionality. User changes survive normal app restarts, but clearing the app's storage or uninstalling Recall removes the saved bank. Android backup is disabled in the manifest.

## Troubleshooting

- **Gradle cannot find Android SDK Platform 36:** In Android Studio, open **Tools → SDK Manager** and install Android SDK Platform 36.
- **Gradle sync or the first build cannot download dependencies:** Check the computer's internet connection, then sync or build again.
- **Android Studio uses the wrong Java version:** Set **Gradle JDK** to JDK 17 in **Settings → Build, Execution, Deployment → Build Tools → Gradle**.
- **The phone does not appear in Android Studio:** Unlock it, reconnect the USB cable, enable USB debugging, and accept the authorization prompt. Try another data-capable cable or check that the Windows device driver is installed.
- **The old jvmTarget Gradle error appears:** Confirm that the project is open at its root and that app/build.gradle.kts uses the Kotlin compilerOptions configuration from this project; then sync Gradle again.
- **A topic contains fewer than 20 questions:** Twenty is the initial starter count. Adding or deleting saved questions changes the displayed count.
- **Questions are missing after reinstalling:** App data is local and is removed when the app is uninstalled or its storage is cleared.

## Internship learning outcomes

The implementation provides practical experience with:

- Structuring a small Android application with Kotlin.
- Building reusable screens and interactive controls with Jetpack Compose.
- Managing UI state for topic selection, question navigation, answer selection, and dialogs.
- Modeling and validating user-generated data.
- Persisting structured data locally using JSON and SharedPreferences.
- Configuring an Android project with Gradle, SDK levels, and a JVM target.
- Building and installing an Android application on an emulator or physical device.

## Further documentation

- [User guide](docs/USER_GUIDE.md) — phone installation and app usage.
- [Architecture notes](docs/ARCHITECTURE.md) — source responsibilities, data model, state flow, and persistence.

## Potential future improvements

Possible next steps include session scoring, a quiz summary, randomized question order, difficulty levels, search, import/export, Room database storage, accessibility review, and automated unit or UI tests.


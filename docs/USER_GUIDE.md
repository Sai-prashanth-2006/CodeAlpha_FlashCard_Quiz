# Recall user guide

## Run Recall on your Android phone

### Install through Android Studio

1. Open the project in Android Studio and wait for Gradle sync to finish.
2. On the phone, open **Settings → About phone** and tap **Build number** seven times. The exact menu names vary by phone; search Settings for “Build number” if needed.
3. Open **Developer options** and enable **USB debugging**.
4. Connect the phone to the computer with a data-capable USB cable. Unlock the phone and accept the prompt to allow USB debugging from this computer. Choose **File transfer** if the phone asks for a USB mode.
5. In Android Studio, select the phone from the device selector beside the **Run** button, select the `app` configuration, and press **Run**. Android Studio builds, installs, and launches Recall.

### Install from PowerShell

Open PowerShell in the project root (the folder containing `gradlew.bat`), connect and authorize the phone as above, then run:

```powershell
.\gradlew.bat :app:installDebug
```

Use `gradlew.bat` as one filename; do not put a path separator between `gradlew` and `.bat`. To build an APK without installing it, run `.\gradlew.bat :app:assembleDebug`. The APK is created at `app\build\outputs\apk\debug\app-debug.apk`; copy it to the phone and open it to install. Android may ask you to allow installs from that file manager. Installing through Android Studio is usually easier and handles the debug signing automatically.

If the phone does not appear, unlock it, reconnect the cable, check that USB debugging is enabled, accept any authorization prompt, and confirm that Windows has the phone's USB driver. In Android Studio, use **Tools → Troubleshoot Device Connections** if available.

## Pick a topic

Open Recall to see the topic list. Choose Cloud, AI-ML, Backend, System Design, HTML, CSS, or JavaScript. Each topic begins with twenty questions; the tile shows the current saved count.

## Answer a question

Read the prompt and choose one of the four answer cards. Recall marks the selected choice, highlights the correct answer, and displays a short explanation. Use **Next** to continue or **Previous** to return to the prior question. The progress bar and counter show your position in the selected topic. Tap **Topics** in the app bar to return to the topic list.

## Add a question

1. Tap **＋ Add** in the app bar.
2. Choose one of the seven topics.
3. Enter the question and all four answer choices. The choices must be non-empty and different.
4. Select the radio button under the correct choice.
5. Optionally add an explanation, then tap **Save question**.

The new question opens immediately and is saved locally.

## Edit or delete a question

Open the question and tap **Edit question** to change its topic, prompt, choices, correct answer, or explanation. Tap **Delete** and confirm to remove it. You can add a replacement question at any time.

## Saved data

The question bank is stored in the app's private device storage. It remains after the app closes, but clearing Recall's app storage or uninstalling Recall removes it. The app has no account or cloud sync.

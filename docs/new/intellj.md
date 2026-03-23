# XOYZ — Running & Testing in IntelliJ IDEA (No Android Studio)

> This guide assumes you have already completed the JDK 17 and Android SDK
> setup from `SETUP_WINDOWS_PURE_JAVA.md`. IntelliJ IDEA Community Edition
> (free) is all you need.

---

## Part 1 — Open the Project

### Step 1 — Open the project folder

1. Launch IntelliJ IDEA.
2. On the Welcome screen click **Open** (not "New Project").
3. Navigate to your `xoyz` folder and click **OK**.
4. When prompted **"Trust and Open Project?"** → click **Trust Project**.

IntelliJ will detect `build.gradle.kts` automatically and begin importing.

### Step 2 — Wait for Gradle sync

The bottom status bar will show **"Gradle: sync"** with a progress spinner.
This downloads Gradle (~150 MB) and all dependencies (~50 MB) on first run.

**Do not click anything until the sync finishes.**

When it's done the status bar shows: `Gradle sync finished`

**If sync fails:**

- Click the **Gradle** panel on the right edge → click the **Refresh** (↺) button.
- Check that `local.properties` exists in the project root with correct content:
  ```
  sdk.dir=C:/Users/YourName/Android/Sdk
  ```
- Make sure `JAVA_HOME` is set. Go to **File → Project Structure → SDK** and confirm a JDK 17 is listed.

---

## Part 2 — Configure the JDK in IntelliJ

1. Go to **File → Project Structure** (or `Ctrl+Alt+Shift+S`).
2. Under **Project → SDK**, select **JDK 17**.
    - If it's not listed click **+** → **Add JDK** → browse to your JDK 17 folder.
3. Set **Project language level** to **17**.
4. Click **Apply → OK**.

Also set the Gradle JVM:

1. Go to **File → Settings** (`Ctrl+Alt+S`).
2. Navigate to **Build, Execution, Deployment → Build Tools → Gradle**.
3. Set **Gradle JVM** to **JDK 17** (or "Project SDK").
4. Click **Apply → OK**.

---

## Part 3 — Run the Unit Tests (No Device Needed)

The unit tests live in `src/test/kotlin/` and run entirely on the JVM.
This is the fastest way to verify the core game logic is working.

### Option A — Run all tests at once

In the **Project** panel on the left:

1. Right-click on `src/test/kotlin/com/xoyz/game`
2. Click **Run 'Tests in com.xoyz.game'**

### Option B — Run a single test file

1. Navigate to any test file, for example:
   `src/test/kotlin/com/xoyz/game/core/WinCheckerTest.kt`
2. Open it.
3. Click the **green play button ▶** in the gutter next to the class name.
4. Or press `Ctrl+Shift+F10` with the file open.

### Option C — Run via Gradle tool window

1. Open the **Gradle** panel (right side of screen).
2. Expand: `xoyz → Tasks → verification`
3. Double-click **test**

### Reading the test results

The **Run** panel at the bottom shows a tree of results:

```
✅ WinCheckerTest
   ✅ player1 wins on every possible line using symbol X
   ✅ player2 wins on every possible line using symbol O
   ✅ space diagonal win
   ...
✅ BoardTest
✅ GameSessionTest
...
```

A green bar = all tests passed.
A red bar = at least one test failed — click the failing test to see the assertion error.

---

## Part 4 — Run on a Physical Android Device

IntelliJ IDEA Community does **not** include the Android plugin, so you cannot
run on a device directly from the IDE Run button.
Use the terminal inside IntelliJ instead — it's just as fast.

### Step 1 — Open the built-in terminal

Press `` Ctrl+` `` (backtick) or go to **View → Tool Windows → Terminal**.

A terminal opens at the project root automatically.

### Step 2 — Connect your device

Plug in your phone via USB (or pair wirelessly — see `SETUP_WINDOWS_PURE_JAVA.md`).

```powershell
adb devices
# Should show something like:
# R5CT1234ABC    device
```

If it shows `unauthorized` → accept the prompt on the phone.

### Step 3 — Build and install

```powershell
.\gradlew.bat assembleDebug
adb install -r build\outputs\apk\debug\xoyz-debug.apk
```

Or in one line:

```powershell
.\gradlew.bat assembleDebug && adb install -r build\outputs\apk\debug\xoyz-debug.apk
```

### Step 4 — Launch the app on the device

```powershell
adb shell am start -n com.xoyz.game/.ui.MainActivity
```

The app will open on the phone.

### Step 5 — Watch logs in real time

In the same terminal (or open a second terminal tab with **+**):

```powershell
adb logcat -s XOYZ:* AndroidRuntime:E *:F
```

Every `XoyzLogger.d(...)` call in the code appears here instantly as you use the app.

---

## Part 5 — Rebuild After Code Changes

When you edit any Kotlin file, IntelliJ compiles it automatically (shown in the
status bar). To push the new version to your device:

```powershell
# In the IntelliJ terminal
.\gradlew.bat assembleDebug && adb install -r build\outputs\apk\debug\xoyz-debug.apk
```

**Tip — create a Run Configuration for this:**

1. Go to **Run → Edit Configurations** (`Alt+Shift+F10` then Edit).
2. Click **+** → **Shell Script**.
3. Name it `Build & Deploy`.
4. Script text:
   ```
   .\gradlew.bat assembleDebug && adb install -r build\outputs\apk\debug\xoyz-debug.apk && adb shell am start -n com.xoyz.game/.ui.MainActivity
   ```
5. Click **OK**.

Now you can press `Shift+F10` to build, install, and launch in one click.

---

## Part 6 — Verify the Winning Lines Table (Quick Smoke Test)

To visually confirm the 49-line lookup table is correct without running on a device,
write a quick `main()` function anywhere and run it in IntelliJ:

1. Create a new file: `src/test/kotlin/com/xoyz/game/utils/SmokeTest.kt`

```kotlin
package com.xoyz.game.utils

fun main() {
    val lines = WinningLinesTable.ALL_LINES
    println("Total lines: ${lines.size}")          // should print 49
    println("Total masks: ${WinningLinesTable.ALL_MASKS.size}")  // should print 49

    lines.forEachIndexed { i, line ->
        val bits = Integer.bitCount(WinningLinesTable.ALL_MASKS[i])
        check(bits == 3)   { "Line $i mask has $bits bits, expected 3" }
        check(line.size == 3) { "Line $i has ${line.size} cells, expected 3" }
    }
    println("All 49 lines verified OK")
}
```

2. Click the **▶** button next to `fun main()`.
3. The Output panel shows:
   ```
   Total lines: 49
   Total masks: 49
   All 49 lines verified OK
   ```

---

## Part 7 — Common IntelliJ Issues

| Problem | Fix |
|---------|-----|
| **"Cannot find symbol GameSession"** after opening | Wait for Gradle sync to finish; then **File → Invalidate Caches → Invalidate and Restart** |
| **Red underlines everywhere** but build works | Gradle JVM mismatch — set Gradle JVM to JDK 17 in Settings |
| **Gradle sync failed: SDK location not found** | Check `local.properties` exists with `sdk.dir=C:/Users/.../Android/Sdk` (forward slashes) |
| **"Unresolved reference: androidx"** | Gradle sync incomplete — open Gradle panel and click Refresh ↺ |
| **Tests won't run — "No tests found"** | Right-click the `test` source root → **Mark Directory as → Test Sources Root** |
| **Build slow / fan spinning** | Go to **Settings → Build → Gradle** → enable **"Build and run using: Gradle"** |
| **Terminal doesn't open at project root** | Close and reopen IntelliJ from the project folder, or `cd C:\path\to\xoyz` in the terminal |

---

## Summary — What Works Where

| Action | How to do it in IntelliJ |
|--------|--------------------------|
| Run unit tests | Right-click test folder → Run, or ▶ gutter button in test file |
| See test results | **Run** panel at bottom |
| Build APK | Terminal: `.\gradlew.bat assembleDebug` |
| Install to device | Terminal: `adb install -r ...apk` |
| Launch app on device | Terminal: `adb shell am start -n com.xoyz.game/.ui.MainActivity` |
| View logs | Terminal: `adb logcat -s XOYZ:* AndroidRuntime:E *:F` |
| Rebuild after changes | Terminal: `.\gradlew.bat assembleDebug && adb install -r ...` |
| Check Gradle tasks | Gradle panel → Tasks tree on the right |
| Fix broken imports | File → Invalidate Caches → Restart |
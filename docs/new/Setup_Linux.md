# XOYZ — Setup Guide (Linux / macOS / WSL)

---

## 1. Install Java JDK 17+

### Ubuntu / Debian
```bash
sudo apt update
sudo apt install openjdk-17-jdk
java --version   # should show 17.x.x
```

### Arch Linux
```bash
sudo pacman -S jdk17-openjdk
```

### macOS (Homebrew)
```bash
brew install openjdk@17
```

### Set JAVA_HOME (add to `~/.bashrc` or `~/.zshrc`)
```bash
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
export PATH="$JAVA_HOME/bin:$PATH"
source ~/.bashrc   # or source ~/.zshrc
```

Verify:
```bash
echo $JAVA_HOME
java --version
```

**Possible errors:**
- `java: command not found` — JDK not installed or not on PATH; re-run the install command and reload your shell.
- `JAVA_HOME points to a JRE, not a JDK` — ensure you installed `openjdk-17-jdk` (not `openjdk-17-jre`).

---

## 2. Install Android SDK (Command-Line Tools)

```bash
cd ~
mkdir -p Android/Sdk/cmdline-tools
cd Android/Sdk/cmdline-tools

# Download latest command-line tools (check https://developer.android.com/studio for latest URL)
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
unzip commandlinetools-linux-*.zip
mv cmdline-tools latest
rm commandlinetools-linux-*.zip
```

### Set ANDROID_HOME (add to `~/.bashrc` or `~/.zshrc`)
```bash
export ANDROID_HOME="$HOME/Android/Sdk"
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"
source ~/.bashrc
```

### Install required SDK packages
```bash
sdkmanager --install \
  "platform-tools" \
  "build-tools;34.0.0" \
  "platforms;android-34"
```

### Accept all licenses
```bash
sdkmanager --licenses
# Press 'y' and Enter for every prompt
```

**Possible errors:**
- `sdkmanager: command not found` — `cmdline-tools/latest/bin` is not on PATH; check your export.
- `Warning: File not found: revision` — retry the `--install` command; sometimes a transient network issue.
- `Failed to read or create install properties file` — run with `sudo` once or fix ownership: `sudo chown -R $USER $ANDROID_HOME`.

---

## 3. Create `local.properties`

Gradle needs to know where your SDK lives:
```bash
cd /path/to/xoyz
echo "sdk.dir=$ANDROID_HOME" > local.properties
```

This file is in `.gitignore` — never commit it.

**Possible errors:**
- `SDK location not found. Define a valid SDK location with an ANDROID_HOME environment variable or by setting the sdk.dir path in your project's local properties file` — the file is missing or `sdk.dir` points to the wrong path.

---

## 4. Make Scripts Executable

```bash
chmod +x gradlew scripts/*.sh
```

---

## 5. Run First-Time Setup Script

```bash
./scripts/setup.sh
```

This script:
- Checks Java, `JAVA_HOME`, `ANDROID_HOME`, ADB
- Downloads the Gradle wrapper JAR if missing
- Creates `local.properties` automatically
- Reports any problems with colour-coded output

---

## 6. Connect Your Android Device

### USB
1. Enable **Developer Options**: Settings → About Phone → tap **Build Number** 7 times.
2. Enable **USB Debugging** inside Developer Options.
3. Connect the phone with a USB cable.
4. Accept the "Allow USB debugging?" prompt on the phone.

```bash
adb devices
# Should list your device, e.g.:
# List of devices attached
# R5CT1234ABC    device
```

### Wireless (Android 11+)
1. Enable **Wireless Debugging** in Developer Options.
2. Tap **Pair device with pairing code**.
3. On your PC:
```bash
adb pair <ip>:<pairing-port>
# Enter the 6-digit code shown on the phone
adb connect <ip>:<debug-port>
adb devices
```

**Possible errors:**
- `unauthorized` — accept the prompt on the phone, or revoke and re-allow USB debugging.
- `no devices/emulators found` — USB cable issue, wrong cable (use data cable, not charge-only), or driver problem.
- `adb: command not found` — `platform-tools` not on PATH; check your `ANDROID_HOME` export.

---

## 7. Build & Install

```bash
# Build debug APK
./scripts/build.sh
# Output: build/outputs/apk/debug/xoyz-debug.apk

# Install to connected device
./scripts/install.sh

# Build + install in one command
./scripts/build_and_install.sh

# Manually
./gradlew assembleDebug
adb install -r build/outputs/apk/debug/xoyz-debug.apk
```

**Possible errors:**
- `FAILURE: Build failed with an exception` → scroll up for `> Task :...` and the actual error.
- `Duplicate class kotlin.collections...` — Kotlin stdlib version conflict; run `./gradlew dependencies` to diagnose.
- `Manifest merger failed` — check `AndroidManifest.xml` for missing or duplicate attributes.

---

## 8. Run Unit Tests (No Device Required)

```bash
# All tests
./gradlew test

# Single class
./gradlew test --tests "com.xoyz.game.core.WinCheckerTest"

# With full output
./gradlew test --info

# HTML report (opens in browser)
open build/reports/tests/test/index.html
```

---

## 9. View Logs

```bash
# Filtered to XOYZ + errors
./scripts/logcat.sh

# Manually
adb logcat -s XOYZ:* AndroidRuntime:E *:F
```

---

## 10. Clean Build

```bash
./scripts/clean.sh
# or
./gradlew clean
```

---

## Quick Reference

| Command | Description |
|---------|-------------|
| `./scripts/setup.sh` | First-time environment check & setup |
| `./scripts/build.sh` | Build debug APK |
| `./scripts/install.sh` | Install APK to connected device |
| `./scripts/build_and_install.sh` | Build + install |
| `./scripts/logcat.sh` | Stream filtered device logs |
| `./scripts/clean.sh` | Delete build artifacts |
| `./gradlew test` | Run all unit tests |
| `./gradlew assembleRelease` | Build release APK (needs signing config) |
| `adb devices` | List connected devices |
| `adb logcat -c` | Clear logcat buffer |

---

## Common Error Reference

| Error | Cause | Fix |
|-------|-------|-----|
| `JAVA_HOME not set` | Env var missing | Add export to `~/.bashrc`, reload shell |
| `SDK location not found` | Missing `local.properties` | `echo "sdk.dir=$ANDROID_HOME" > local.properties` |
| `License not accepted` | Licenses not agreed | `sdkmanager --licenses` |
| `Could not determine java version` | JDK < 17 | Install JDK 17+ |
| `Gradle wrapper JAR missing` | Checked-out repo without JAR | Run `./scripts/setup.sh` |
| `adb: no devices` | Device not connected or unauthorised | Check USB/wireless setup |
| `INSTALL_FAILED_UPDATE_INCOMPATIBLE` | Existing signed app on device | `adb uninstall com.xoyz.game` then reinstall |
| `Task :compileDebugKotlin FAILED` | Kotlin compile error | Read the error message; fix the source |
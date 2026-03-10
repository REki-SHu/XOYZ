# XOYZ — Setup Guide (Windows PowerShell)

> All commands below are for **Windows PowerShell** (5.1+) or **PowerShell Core** (7+).
> Run PowerShell as a regular user unless noted otherwise.

---

## 1. Install Java JDK 17+

### Option A — Winget (recommended, Windows 10/11)
```powershell
winget install Microsoft.OpenJDK.17
```

### Option B — Manual Download
Download from: https://adoptium.net/temurin/releases/?version=17
Choose **Windows x64 JDK .msi**, install with defaults.

### Set JAVA_HOME (System Environment Variable)
```powershell
# Find where Java was installed
$javaPath = (Get-Command java).Source | Split-Path | Split-Path
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", $javaPath, "User")
$env:JAVA_HOME = $javaPath

# Verify
java --version   # should show 17.x.x
echo $env:JAVA_HOME
```

**Possible errors:**
- `java : The term 'java' is not recognized` — restart PowerShell after installation; the PATH update needs a new session.
- `'JAVA_HOME' is not recognized` — the SetEnvironmentVariable call succeeded but you need a new PowerShell window.

---

## 2. Install Android SDK (Command-Line Tools)

### Create SDK directory
```powershell
New-Item -ItemType Directory -Force "$env:USERPROFILE\Android\Sdk\cmdline-tools"
Set-Location "$env:USERPROFILE\Android\Sdk\cmdline-tools"
```

### Download and unzip command-line tools
```powershell
# Download (check https://developer.android.com/studio for the latest URL)
Invoke-WebRequest -Uri "https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip" `
    -OutFile "cmdline-tools.zip"

Expand-Archive -Path "cmdline-tools.zip" -DestinationPath "."
Rename-Item -Path "cmdline-tools" -NewName "latest"
Remove-Item "cmdline-tools.zip"
```

### Set ANDROID_HOME
```powershell
$androidHome = "$env:USERPROFILE\Android\Sdk"

[System.Environment]::SetEnvironmentVariable("ANDROID_HOME", $androidHome, "User")
$env:ANDROID_HOME = $androidHome

# Add tools to PATH
$sdkPath = "$androidHome\cmdline-tools\latest\bin;$androidHome\platform-tools"
$current = [System.Environment]::GetEnvironmentVariable("PATH", "User")
[System.Environment]::SetEnvironmentVariable("PATH", "$current;$sdkPath", "User")
$env:PATH = "$env:PATH;$sdkPath"
```

Open a **new** PowerShell window so the PATH changes take effect, then:
```powershell
sdkmanager --version    # should print a version number
```

### Install required SDK packages
```powershell
sdkmanager --install "platform-tools" "build-tools;34.0.0" "platforms;android-34"
```

### Accept all licenses
```powershell
# Type 'y' and press Enter for every prompt
sdkmanager --licenses
```

**Possible errors:**
- `sdkmanager : The term 'sdkmanager' is not recognized` — restart PowerShell; PATH not updated yet.
- `Error: Could not determine SDK root` — `ANDROID_HOME` not set. Verify with `echo $env:ANDROID_HOME`.
- `Warning: Could not create settings` — first run only; safe to ignore.

---

## 3. Create `local.properties`

```powershell
Set-Location C:\path\to\xoyz
# Use forward slashes — Gradle requires them
$sdkPath = $env:ANDROID_HOME -replace '\\', '/'
"sdk.dir=$sdkPath" | Out-File -FilePath "local.properties" -Encoding utf8 -NoNewline
```

Verify the file looks like:
```
sdk.dir=C:/Users/YourName/Android/Sdk
```

**Important:** Use **forward slashes** (`/`) not backslashes in this file.

**Possible errors:**
- `SDK location not found` — backslashes in `sdk.dir`; rewrite with `/`.

---

## 4. Set Script Execution Policy (one-time)

By default, PowerShell blocks script execution.
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

---

## 5. Build the Project

```powershell
# Navigate to the project root
Set-Location C:\path\to\xoyz

# Build debug APK
.\gradlew.bat assembleDebug
```

PowerShell uses `.\gradlew.bat` (the Windows batch wrapper).

**Possible errors:**
- `'.\gradlew.bat' is not recognized` — you're not in the project root, or the file is missing.
- `Gradle could not start` — `JAVA_HOME` is wrong or JDK < 17.
- `Could not resolve com.android.application` — internet access blocked; check proxy settings.

---

## 6. Connect Your Android Device

### USB
1. Enable **Developer Options**: Settings → About Phone → tap **Build Number** 7 times.
2. Enable **USB Debugging** inside Developer Options.
3. Connect with a USB data cable.
4. Accept the "Allow USB debugging?" prompt on the phone.

```powershell
adb devices
# List of devices attached
# R5CT1234ABC    device
```

### Wireless Debugging (Android 11+)
1. Enable **Wireless Debugging** in Developer Options.
2. Tap **Pair device with pairing code**.
3. In PowerShell:
```powershell
adb pair <ip>:<pairing-port>   # enter the 6-digit code
adb connect <ip>:<debug-port>
adb devices
```

**Possible errors:**
- `adb: command not found` — `platform-tools` not on PATH; add `$env:ANDROID_HOME\platform-tools` to PATH.
- `unauthorized` — accept the RSA key prompt on the phone.
- USB driver issues — install **Google USB Driver** via `sdkmanager --install "extras;google;usb_driver"` and update the driver in Device Manager.

---

## 7. Install APK to Device

```powershell
# Build first
.\gradlew.bat assembleDebug

# Install
adb install -r build\outputs\apk\debug\xoyz-debug.apk
```

---

## 8. Run Unit Tests (No Device Required)

```powershell
# All tests
.\gradlew.bat test

# Single class
.\gradlew.bat test --tests "com.xoyz.game.core.WinCheckerTest"

# With full output
.\gradlew.bat test --info

# Open HTML report
Start-Process "build\reports\tests\test\index.html"
```

---

## 9. View Logs

```powershell
# Filtered to XOYZ + errors
adb logcat -s XOYZ:* AndroidRuntime:E *:F

# Clear logcat first
adb logcat -c
```

---

## 10. Clean Build

```powershell
.\gradlew.bat clean
```

---

## Quick Reference

| Command | Description |
|---------|-------------|
| `.\gradlew.bat assembleDebug` | Build debug APK |
| `.\gradlew.bat assembleRelease` | Build release APK |
| `.\gradlew.bat test` | Run unit tests |
| `.\gradlew.bat clean` | Delete build artifacts |
| `.\gradlew.bat dependencies` | Print dependency tree |
| `adb devices` | List connected devices |
| `adb install -r <apk>` | Install/re-install APK |
| `adb uninstall com.xoyz.game` | Uninstall app |
| `adb logcat -c` | Clear log buffer |
| `sdkmanager --list` | List installed SDK packages |

---

## Common Error Reference

| Error | Cause | Fix |
|-------|-------|-----|
| `JAVA_HOME not set` | Env var missing | Set via System Properties or PowerShell command above |
| `SDK location not found` | Missing/wrong `local.properties` | Recreate with forward slashes |
| `License not accepted` | Licenses not agreed | `sdkmanager --licenses` |
| `gradlew.bat not found` | Wrong directory | `Set-Location` to project root |
| `Execution Policy` error | Scripts blocked | `Set-ExecutionPolicy RemoteSigned -Scope CurrentUser` |
| `adb not found` | PATH missing platform-tools | Add `$ANDROID_HOME\platform-tools` to PATH |
| `INSTALL_FAILED_UPDATE_INCOMPATIBLE` | Signed vs unsigned mismatch | `adb uninstall com.xoyz.game` |
| `Unsupported class file major version` | JDK < 17 | Install JDK 17+ |
| `Could not resolve dependencies` | No internet / proxy | Configure Gradle proxy in `~\.gradle\gradle.properties` |
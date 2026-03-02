# Build & Environment Setup Guide

## 1. Install Java JDK 17+

### Ubuntu/Debian
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

### Arch Linux
```bash
sudo pacman -S jdk17-openjdk
```

### Verify
```bash
java --version
# Should show 17.x.x or higher
```

### Set JAVA_HOME
Add to `~/.zshrc` (or `~/.bashrc`):
```bash
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
```

## 2. Install Android SDK (Command-Line Tools Only)

### Download
```bash
cd ~
mkdir -p Android/Sdk/cmdline-tools
cd Android/Sdk/cmdline-tools

# Download the latest command-line tools
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
unzip commandlinetools-linux-*.zip
mv cmdline-tools latest
rm commandlinetools-linux-*.zip
```

### Set ANDROID_HOME
Add to `~/.zshrc`:
```bash
export ANDROID_HOME="$HOME/Android/Sdk"
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"
```

Then reload:
```bash
source ~/.zshrc
```

### Install SDK Packages
```bash
sdkmanager --install \
  "platform-tools" \
  "build-tools;34.0.0" \
  "platforms;android-34"
```

### Accept Licenses
```bash
sdkmanager --licenses
# Accept all with 'y'
```

## 3. Verify ADB
```bash
adb --version
# Should show Android Debug Bridge version
```

## 4. Connect Your Device

### USB Debugging
1. Enable Developer Options on your phone (tap Build Number 7 times)
2. Enable USB Debugging in Developer Options
3. Connect via USB
4. Run `adb devices` — your device should appear

### Wireless Debugging (Android 11+)
1. Enable Wireless Debugging in Developer Options
2. Tap "Pair device with pairing code"
3. On your PC:
```bash
adb pair <ip>:<port>
# Enter the pairing code shown on phone
adb connect <ip>:<port>
# Use the port shown under "Wireless debugging" (not the pairing port)
```
4. Verify: `adb devices`

## 5. Build the Project

```bash
cd /path/to/xoyz

# First time: run the setup script to check everything
chmod +x scripts/*.sh gradlew
./scripts/setup.sh

# Then build
./gradlew assembleDebug
```

### Common Issues

| Issue | Fix |
|-------|-----|
| `JAVA_HOME not set` | Set it in `~/.zshrc` as shown above |
| `SDK location not found` | Create `local.properties` with `sdk.dir=/path/to/Android/Sdk` |
| `License not accepted` | Run `sdkmanager --licenses` |
| `Could not determine java version` | Ensure JDK 17+ is installed |

## 6. Create local.properties

This file should NOT be committed to git. Create it manually:
```bash
echo "sdk.dir=$ANDROID_HOME" > local.properties
```

## 7. Quick Reference

```bash
# Build debug APK
./gradlew assembleDebug

# Install to device
adb install -r build/outputs/apk/debug/xoyz-debug.apk

# View logs
adb logcat -s XOYZ:* *:E

# Build release APK (needs signing config)
./gradlew assembleRelease

# Clean build
./gradlew clean

# Run all tests
./gradlew test
```

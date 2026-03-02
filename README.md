# XOYZ — The 3D Tic-Tac-Toe Game

A twist on classic Tic-Tac-Toe played on a **3×3×3 cube** with **four symbols**: **X, O, Y, Z**.

## Concept

- **Player 1** controls **X** and **Y**
- **Player 2** controls **O** and **Z**
- The game is played over a **3×3 cube** (27 cells across 3 layers)
- Rules are a work in progress — see [docs/RULES.md](docs/RULES.md)

## Features (Planned)

- [x] Project scaffolding & build system
- [ ] 3D cube rendering (OpenGL ES / LibGDX)
- [ ] Local two-player gameplay
- [ ] Multiplayer via shared room code
- [ ] Game rules engine
- [ ] Animations & polish

## Tech Stack

| Component       | Technology                          |
|-----------------|-------------------------------------|
| Language         | Kotlin                             |
| Platform         | Native Android                     |
| 3D Rendering     | LibGDX (or raw OpenGL ES)          |
| Build System     | Gradle (Kotlin DSL) + AGP          |
| Multiplayer      | TBD (Firebase / custom server)     |

## Prerequisites

- **Java JDK 17+** with `JAVA_HOME` set
- **Android SDK** with:
  - `platform-tools` (ADB)
  - `build-tools;34.0.0`
  - `platforms;android-34`
- `ANDROID_HOME` environment variable pointing to your SDK
- A physical Android device with USB/wireless debugging enabled (or an emulator)

## Building & Installing

```bash
# First-time setup (checks tools, downloads gradle wrapper)
chmod +x scripts/*.sh gradlew
./scripts/setup.sh

# Build the debug APK
./scripts/build.sh

# Install to a connected device
./scripts/install.sh

# Build + install in one go
./scripts/build_and_install.sh
```

Or manually:

```bash
./gradlew assembleDebug
adb install -r build/outputs/apk/debug/xoyz-debug.apk
```

## Project Structure

```
xoyz/
├── build.gradle.kts            # Gradle config (plugins, android, dependencies)
├── settings.gradle.kts         # Project settings
├── gradle.properties           # Gradle JVM & Android flags
├── gradlew                     # Gradle wrapper script
├── proguard-rules.pro          # ProGuard/R8 rules
├── gradle/wrapper/             # Gradle wrapper config
├── src/
│   ├── main/
│   │   ├── AndroidManifest.xml
│   │   ├── kotlin/com/xoyz/game/
│   │   │   ├── core/           # Game logic, rules engine
│   │   │   ├── engine/         # 3D rendering, OpenGL/LibGDX
│   │   │   ├── ui/             # Activities, UI components
│   │   │   ├── multiplayer/    # Networking, room codes
│   │   │   └── utils/          # Helpers, constants
│   │   ├── res/                # Android resources
│   │   └── assets/             # 3D models, shaders, textures
│   ├── test/                   # Unit tests
│   └── androidTest/            # Instrumented tests
├── docs/                       # Design docs, architecture, rules
├── scripts/                    # Build & deploy scripts
└── LICENSE
```

## License

This project is licensed under the MIT License — see [LICENSE](LICENSE).

# XOYZ — 3D Tic-Tac-Toe

A four-symbol, 3D Tic-Tac-Toe variant played on a **3×3×3 cube** (27 cells, 49 possible winning lines).

| Player   | Symbols |
|----------|---------|
| Player 1 | X, Y    |
| Player 2 | O, Z    |

---

## Project Status

| Phase | Description                        | Status     |
|-------|------------------------------------|------------|
| 0     | Project scaffolding & build system | ✅ Done     |
| 1     | Core game logic + unit tests       | ✅ Done     |
| 2     | 3D rendering (LibGDX)              | 🔲 Pending |
| 3     | Android UI integration             | 🔲 Pending |
| 4     | Multiplayer via Firebase           | 🔲 Pending |
| 5     | Polish & animations                | 🔲 Pending |

---

## Architecture

```
┌────────────────────────────────────────────────────────┐
│                       XOYZ App                         │
├──────────────┬──────────────────┬──────────────────────┤
│  ui/         │  engine/         │  multiplayer/        │
│  Activities  │  CubeRenderer    │  RoomManager         │
│  ViewModel   │  CameraController│  GameSync            │
│              │  InputHandler    │  ConnectionManager   │
├──────────────┴──────────────────┴──────────────────────┤
│                    core/  (pure Kotlin, no Android)    │
│  GameSession · Board · RulesEngine · WinChecker        │
│  TurnManager · MoveValidator · Player · Move           │
├────────────────────────────────────────────────────────┤
│                    utils/                              │
│  WinningLinesTable · BoardConstants · Extensions       │
└────────────────────────────────────────────────────────┘
```

### Key Design Decisions

- **Immutable core**: `Board` and `GameSession` never mutate — every change returns a new instance. Safe to snapshot, undo, or cache.
- **SOLID throughout**: Each class has one job. Swap `WinChecker`, `MoveValidator`, or `RulesEngine` by passing a different implementation into `GameSession.create()`.
- **Bitboard win detection**: All 49 winning lines are precomputed into bitmasks at startup. Checking for a win costs 49 bitwise AND operations — effectively O(1).
- **Pure Kotlin core**: The `core/` and `utils/` packages have zero Android or rendering dependencies, making them trivially testable on the JVM.

---

## Module Breakdown

```
src/main/kotlin/com/xoyz/game/
├── core/
│   ├── Board.kt              Immutable 27-cell board snapshot
│   ├── BoardPosition.kt      3D coordinate ↔ flat index conversion
│   ├── CellState.kt          EMPTY | X | O | Y | Z
│   ├── GameSession.kt        Orchestrates a full game (immutable)
│   ├── GameState.kt          InProgress | Won(player, line) | Draw
│   ├── Move.kt               (player, symbol, position) value object
│   ├── MoveValidator.kt      Interface + DefaultMoveValidator
│   ├── Player.kt             (id, name, symbols) data class
│   ├── RulesEngine.kt        Interface + StandardRulesEngine
│   ├── TurnManager.kt        Interface + AlternatingTurnManager
│   └── WinChecker.kt         Interface + BitboardWinChecker
├── engine/                   Phase 2 interface contracts
│   ├── CameraController.kt
│   ├── CubeRenderer.kt
│   └── InputHandler.kt
├── multiplayer/
│   ├── ConnectionManager.kt  Network lifecycle (stub)
│   ├── GameSync.kt           RoomSnapshot ↔ GameSession converter
│   ├── NetworkProtocol.kt    DTOs for Firebase / WebSocket
│   └── RoomManager.kt        Interface for room CRUD + sync
├── ui/
│   ├── GameViewModel.kt      LiveData bridge between core and UI
│   ├── LocalGameActivity.kt  Local 2-player game screen
│   └── MainActivity.kt       Entry point / main menu
└── utils/
    ├── BoardConstants.kt     Dimension constants (single source of truth)
    ├── Extensions.kt         Kotlin extension functions
    ├── WinningLinesTable.kt  Precomputed 49-line lookup table + bitmasks
    └── XoyzLogger.kt         Centralised Android Log wrapper
```

---

## Prerequisites

- **Java JDK 17+** with `JAVA_HOME` set
- **Android SDK** (`build-tools;34.0.0`, `platforms;android-34`, `platform-tools`)
- `ANDROID_HOME` pointing to your SDK directory
- A connected Android device (USB or wireless) with Debugging enabled

---

## Quick Start

```bash
# 1. First-time setup (checks tools, creates local.properties, downloads Gradle wrapper)
chmod +x scripts/*.sh gradlew
./scripts/setup.sh

# 2. Build the debug APK
./scripts/build.sh

# 3. Install to a connected device
./scripts/install.sh

# 4. Stream logs
./scripts/logcat.sh
```

Or all in one step:
```bash
./scripts/build_and_install.sh
```

---

## Running Tests

```bash
# All unit tests (pure JVM — no device required)
./gradlew test

# Specific test class
./gradlew test --tests "com.xoyz.game.core.WinCheckerTest"

# With verbose output
./gradlew test --info
```

---

## License

MIT — see [LICENSE](LICENSE).
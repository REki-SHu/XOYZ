# XOYZ — API & Command Reference

This document describes every public function, class, and script command in the project,
along with their purpose, parameters, return values, and known error conditions.

---

## Table of Contents

1. [Build Scripts](#1-build-scripts)
2. [core/ — Game Logic](#2-core--game-logic)
3. [utils/ — Utilities & Lookup Table](#3-utils--utilities--lookup-table)
4. [multiplayer/ — Networking Layer](#4-multiplayer--networking-layer)
5. [ui/ — Android UI Layer](#5-ui--android-ui-layer)
6. [engine/ — Rendering Contracts](#6-engine--rendering-contracts)

---

## 1. Build Scripts

All scripts live in `scripts/` and must be run from the **project root**.

---

### `./scripts/setup.sh`
**Platform:** Linux / macOS  
**Purpose:** First-time environment check and project initialisation.

**What it does:**
1. Checks that `java` (JDK 17+) is installed and `JAVA_HOME` is set.
2. Checks that `ANDROID_HOME` is set and the SDK exists.
3. Checks that `adb` is on the PATH.
4. Verifies `android-34` platform and `build-tools;34.0.0` are installed.
5. Downloads the Gradle wrapper JAR if it is missing.
6. Creates `local.properties` with `sdk.dir=$ANDROID_HOME`.
7. Makes `gradlew` and all `scripts/*.sh` executable.

**Usage:**
```bash
chmod +x scripts/setup.sh
./scripts/setup.sh
```

**Possible errors:**
- `ERROR: JAVA_HOME is not set` — add `export JAVA_HOME=...` to `~/.bashrc` and reload.
- `Gradle not installed globally` — the script will attempt to download the Gradle distribution and generate the wrapper. If it fails, install Gradle with `sudo apt install gradle`.
- `ANDROID_HOME NOT FOUND` — install the Android SDK and export the variable.

---

### `./scripts/build.sh`
**Platform:** Linux / macOS  
**Purpose:** Builds the debug APK.

**What it does:**
- Runs `./gradlew assembleDebug`.
- Prints the APK path and size on success.
- Exits with code 1 if the APK is not found after the build.

**Usage:**
```bash
./scripts/build.sh
```

**Output:** `build/outputs/apk/debug/xoyz-debug.apk`

**Possible errors:**
- `Build failed` — scroll up for the Gradle error; usually a compile error or missing SDK package.

---

### `./scripts/install.sh`
**Platform:** Linux / macOS  
**Purpose:** Installs the debug APK to a connected device.

**What it does:**
1. Checks that `build/outputs/apk/debug/xoyz-debug.apk` exists.
2. Verifies at least one ADB device is connected.
3. Runs `adb install -r <apk>`.
4. Optionally launches the app via `adb shell am start`.

**Usage:**
```bash
./scripts/install.sh
```

**Possible errors:**
- `APK not found` — run `./scripts/build.sh` first.
- `No devices connected` — connect a device and enable USB debugging.
- `INSTALL_FAILED_UPDATE_INCOMPATIBLE` — the device has an existing installation signed with a different key. Run `adb uninstall com.xoyz.game` first.

---

### `./scripts/build_and_install.sh`
**Platform:** Linux / macOS  
**Purpose:** Runs `build.sh` then `install.sh` in sequence.

---

### `./scripts/clean.sh`
**Platform:** Linux / macOS  
**Purpose:** Deletes all Gradle build artifacts.

**Usage:**
```bash
./scripts/clean.sh
```

---

### `./scripts/logcat.sh`
**Platform:** Linux / macOS  
**Purpose:** Streams filtered device logs to the terminal.

**Filter:** Shows only `XOYZ` tag logs, `AndroidRuntime` errors, and fatal messages.

**Usage:**
```bash
./scripts/logcat.sh
# Press Ctrl+C to stop
```

---

### Windows equivalents

| Linux command | PowerShell equivalent |
|---------------|-----------------------|
| `./gradlew assembleDebug` | `.\gradlew.bat assembleDebug` |
| `./gradlew test` | `.\gradlew.bat test` |
| `./gradlew clean` | `.\gradlew.bat clean` |
| `adb install -r <apk>` | `adb install -r build\outputs\apk\debug\xoyz-debug.apk` |
| `adb logcat -s XOYZ:*` | `adb logcat -s XOYZ:* AndroidRuntime:E *:F` |

---

## 2. `core/` — Game Logic

### `CellState` (enum)

| Value | Owner | Description |
|-------|-------|-------------|
| `EMPTY` | — | Unoccupied cell |
| `X` | Player 1 | Player 1's primary symbol |
| `Y` | Player 1 | Player 1's secondary symbol |
| `O` | Player 2 | Player 2's primary symbol |
| `Z` | Player 2 | Player 2's secondary symbol |

**Properties:**
- `isOccupied: Boolean` — true if not `EMPTY`.
- `displayChar: Char` — single character for console output (`.`, `X`, `O`, `Y`, `Z`).

---

### `BoardPosition(layer, row, col)`

Represents a cell address on the 3×3×3 board.

| Parameter | Type | Range | Description |
|-----------|------|-------|-------------|
| `layer` | Int | 0–2 | 0 = top, 2 = bottom |
| `row` | Int | 0–2 | 0 = front, 2 = back |
| `col` | Int | 0–2 | 0 = left, 2 = right |

**Properties:**
- `flatIndex: Int` — flat 0–26 index: `layer*9 + row*3 + col`.

**Factory:**
- `BoardPosition.fromFlatIndex(index: Int): BoardPosition` — inverse of `flatIndex`. Throws `IllegalArgumentException` if `index` is not in 0..26.

**Errors:**
- `IllegalArgumentException: layer must be 0..2` — any coordinate out of range.

---

### `Player(id, name, symbols)`

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | Int | Must be 1 or 2 |
| `name` | String | Display name |
| `symbols` | Set\<CellState\> | Exactly 2 non-EMPTY symbols |

**Functions:**
- `owns(symbol: CellState): Boolean` — returns true if `symbol` is in this player's set.

**Factory:**
- `Player.player1(name)` — creates Player 1 with `{X, Y}`.
- `Player.player2(name)` — creates Player 2 with `{O, Z}`.

**Errors:**
- `IllegalArgumentException` if `id` is not 1 or 2, `symbols.size != 2`, or `EMPTY` is in `symbols`.

---

### `Move(player, symbol, position)`

| Parameter | Type | Description |
|-----------|------|-------------|
| `player` | Player | The player making the move |
| `symbol` | CellState | The symbol being placed (must be owned by `player`) |
| `position` | BoardPosition | Target cell |
| `timestamp` | Long | Epoch millis (auto-set if omitted) |

**Errors:**
- `IllegalArgumentException: Player X does not own symbol Y` — symbol not in player's set.

---

### `Board`

Immutable snapshot of all 27 cells.

**Factory:**
- `Board.empty()` — all cells `EMPTY`.
- `Board.fromArray(cells: Array<CellState>)` — constructs from a 27-element array (copied, not referenced).

**Reading:**
- `getCell(position: BoardPosition): CellState`
- `getCell(index: Int): CellState` — flat index 0–26.
- `isEmpty(position: BoardPosition): Boolean`
- `emptyCells(): List<BoardPosition>` — all unoccupied positions.
- `isFull: Boolean`

**Mutation (returns new Board):**
- `withMove(position: BoardPosition, state: CellState): Board` — places `state` at `position`.

  **Errors:**
  - `IllegalArgumentException: Cell already occupied` — target cell is not empty.
  - `IllegalArgumentException: Cannot place EMPTY` — use the raw factory for testing instead.

**Bitmask helper:**
- `buildMask(predicate: (CellState) -> Boolean): Int` — Int with bit *i* set when `predicate(cell[i])` is true. Used by `BitboardWinChecker`.

---

### `GameSession`

Immutable game orchestrator.

**Factory:**
- `GameSession.newGame(player1Name, player2Name)` — creates a fresh game with default collaborators.
- `GameSession.create(player1, player2, board, moveValidator, rulesEngine, history, state)` — fully customisable, useful in tests.

**Properties:**
- `player1: Player`, `player2: Player`
- `board: Board` — current board.
- `currentPlayer: Player` — whose turn it is.
- `state: GameState` — `InProgress`, `Won`, or `Draw`.
- `isInProgress: Boolean`
- `history: List<Move>` — every accepted move in order.

**Functions:**
- `applyMove(move: Move): MoveResult` — attempts to apply `move`.
  - Returns `MoveResult.Accepted(newSession)` — the updated session (original unchanged).
  - Returns `MoveResult.Rejected(reason)` — with a human-readable explanation.

**Why immutable?**  
Each call to `applyMove` returns a fresh `GameSession`. The original is never touched.
This means you can keep a reference to any previous snapshot for undo or testing.

---

### `GameState` (sealed class)

| Subclass | Properties | Meaning |
|----------|------------|---------|
| `InProgress` | — | Game is ongoing |
| `Won` | `winner: Player`, `winningLine: IntArray` | A player completed a line |
| `Draw` | — | All 27 cells filled, no winner |

---

### `MoveValidator` (interface)

**`validate(board, move, currentPlayer): ValidationResult`**

Returns:
- `ValidationResult.Valid` — the move is legal.
- `ValidationResult.Invalid(reason: String)` — illegal, with explanation.

**`DefaultMoveValidator`** checks:
1. `move.player.id == currentPlayer.id` — correct turn.
2. `move.player.owns(move.symbol)` — player owns the symbol.
3. `board.isEmpty(move.position)` — cell is unoccupied.

---

### `WinChecker` (interface)

**`findWinningLine(board: Board, player: Player): IntArray?`**

Returns the matching 3-element flat-index array, or `null` if no win.

**`BitboardWinChecker`** — O(49) bitwise AND implementation:
1. Builds `playerMask` — Int with bit `i` set for each cell owned by `player`.
2. Iterates `WinningLinesTable.ALL_MASKS`; returns the first matching line.

---

### `RulesEngine` (interface)

**`evaluate(board: Board, lastMove: Move, players: List<Player>): GameState`**

Called after a move is applied to the board.

**`StandardRulesEngine`** evaluation order:
1. Check win for `lastMove.player`.
2. If no win and `board.isFull` → `Draw`.
3. Otherwise → `InProgress`.

---

### `TurnManager` (interface)

**`currentPlayer: Player`** — who moves next.  
**`advance(): TurnManager`** — returns new manager advanced by one turn (immutable).

**`AlternatingTurnManager(players, activeIndex)`** — alternates between index 0 and 1.

---

## 3. `utils/` — Utilities & Lookup Table

### `BoardConstants`

| Constant | Value | Description |
|----------|-------|-------------|
| `LAYERS` | 3 | Number of layers |
| `ROWS` | 3 | Rows per layer |
| `COLS` | 3 | Columns per layer |
| `TOTAL_CELLS` | 27 | All cells |
| `TOTAL_WIN_LINES` | 49 | All possible winning lines |

---

### `WinningLinesTable`

**`ALL_LINES: Array<IntArray>`** — 49 entries, each a 3-element flat-index array.

**`ALL_MASKS: IntArray`** — 49 entries; each Int has exactly 3 bits set corresponding to `ALL_LINES[i]`.

**Line categories:**

| Category | Count | Description |
|----------|-------|-------------|
| A — Layer rows | 9 | 3 rows × 3 layers |
| B — Layer cols | 9 | 3 cols × 3 layers |
| C — Layer diags | 6 | 2 diags × 3 layers |
| D — Vertical cols | 9 | Same (row, col) across all 3 layers |
| E — Vert-plane row-diags | 6 | 2 diags × 3 col-planes |
| F — Vert-plane col-diags | 6 | 2 diags × 3 row-planes |
| G — Space diagonals | 4 | Corner-to-corner through centre |
| **Total** | **49** | |

**Errors:**
- A runtime `require(lines.size == 49)` assertion fires at class initialisation if the table is miscounted — this would be a bug.

---

### `XoyzLogger`

Centralised Android `Log` wrapper. All calls are tagged `"XOYZ"`.

| Function | Android Level | Controlled by `debugEnabled` |
|----------|---------------|------------------------------|
| `v(message)` | VERBOSE | Yes |
| `d(message)` | DEBUG | Yes |
| `i(message)` | INFO | No |
| `w(message, throwable?)` | WARN | No |
| `e(message, throwable?)` | ERROR | No |

Set `XoyzLogger.debugEnabled = false` in release builds to suppress verbose output.

**Note:** All calls are wrapped in `try/catch` so they never crash in JVM unit tests where `android.util.Log` is not available.

---

### Extension Functions (`Extensions.kt`)

- `CellState.ownerIn(player1, player2): Player?` — returns the player who owns this state, or `null` for `EMPTY`.
- `Int.hasBit(index: Int): Boolean` — true if bit `index` is set in this Int.

---

## 4. `multiplayer/` — Networking Layer

### `RoomManager` (interface)

| Function | Description |
|----------|-------------|
| `createRoom(hostName, onResult)` | Creates a room; callback receives `RoomSnapshot` or error |
| `joinRoom(roomCode, guestName, onResult)` | Joins existing room |
| `observeRoom(roomCode, onUpdate): Unsubscribe` | Subscribes to real-time updates; returns a lambda to unsubscribe |
| `sendMove(roomCode, move, onResult)` | Publishes a `RemoteMove` |
| `closeRoom(roomCode, onResult)` | Closes and cleans up the room |

**`Unsubscribe`** is a `() -> Unit` lambda — call it to stop receiving updates and free resources.

---

### `GameSync` (object)

- `toGameSession(snapshot: RoomSnapshot): GameSession?` — converts a server `RoomSnapshot` to a local `GameSession`. Returns `null` if the snapshot's board size is wrong (logs an error).
- `toRemoteMove(move: Move, playerIndex: Int): RemoteMove` — serialises a local `Move` for transmission.

---

### `NetworkProtocol.kt` — DTOs

| Class | Purpose |
|-------|---------|
| `RoomSnapshot` | Complete room state stored in Firebase / WebSocket server |
| `RemotePlayerInfo` | Player uid, name, ready-flag |
| `RemoteMove` | playerIndex, symbolName, flatIndex, timestamp |
| `RoomStatus` | `WAITING` / `IN_PROGRESS` / `FINISHED` |

---

### `ConnectionManager`

- `connect(onConnected, onError)` — initialises the network connection (stub).
- `disconnect()` — tears down the connection.
- `state: State` — `DISCONNECTED | CONNECTING | CONNECTED | ERROR`.

---

## 5. `ui/` — Android UI Layer

### `GameViewModel : ViewModel`

**LiveData (observe in Activity/Fragment):**

| Property | Type | Emits when |
|----------|------|------------|
| `gameState` | `LiveData<GameState>` | After every move |
| `board` | `LiveData<Board>` | After every move |
| `currentPlayer` | `LiveData<Player>` | After every accepted move |
| `lastMoveResult` | `LiveData<MoveResult?>` | After every `applyMove` call |
| `moveHistory` | `LiveData<List<Move>>` | After every accepted move |

**Functions:**

- `startNewGame(player1Name, player2Name)` — resets the session.
- `applyMove(flatIndex: Int, symbol: CellState)` — validates and applies a move. Emits `lastMoveResult`.

  | Condition | Result emitted |
  |-----------|----------------|
  | Valid move | `MoveResult.Accepted` |
  | Wrong symbol for player | `MoveResult.Rejected` |
  | Invalid flat index | `MoveResult.Rejected` |
  | Cell occupied | `MoveResult.Rejected` |
  | Game already ended | `MoveResult.Rejected` |

---

### `MainActivity`

- `onLocalGameClicked()` — launches `LocalGameActivity`. Called from XML `android:onClick`.
- `onMultiplayerClicked()` — stub; will launch `LobbyActivity` in Phase 4.

---

### `LocalGameActivity`

Extras (pass via `Intent`):
- `LocalGameActivity.EXTRA_P1_NAME` — Player 1 display name (default `"Player 1"`).
- `LocalGameActivity.EXTRA_P2_NAME` — Player 2 display name (default `"Player 2"`).

Observes `GameViewModel` and shows win/draw dialogs automatically.

---

## 6. `engine/` — Rendering Contracts

These interfaces define the Phase 2 API surface. Implement them with LibGDX or raw OpenGL ES.

### `CubeRenderer`

| Function | Description |
|----------|-------------|
| `onCreate()` | Initialise GPU resources |
| `onResize(width, height)` | Handle surface resize |
| `onFrame(board, deltaMs)` | Draw one frame |
| `setHighlight(flatIndex)` | Highlight cell; -1 to clear |
| `playWinAnimation(lineIndices)` | Glow the 3 winning cells |
| `onDestroy()` | Release GPU resources |

### `CameraController`

| Function | Description |
|----------|-------------|
| `onDrag(deltaX, deltaY)` | Orbit camera by drag delta |
| `onPinch(scaleFactor)` | Zoom; >1 = zoom in |
| `snapTo(preset)` | Snap to `ISOMETRIC`, `TOP`, `FRONT`, `RIGHT` |
| `reset()` | Return to default isometric |

### `InputHandler`

| Function | Description |
|----------|-------------|
| `onTap(screenX, screenY): BoardPosition?` | Ray-cast; returns hit cell or null |
| `onDrag(...)` | Forward to `CameraController.onDrag` |
| `onPinch(scaleFactor)` | Forward to `CameraController.onPinch` |

---

*End of API Reference*
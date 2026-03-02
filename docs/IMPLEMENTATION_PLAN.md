# Implementation Plan

## Phase 0: Project Setup ✅
- [x] Project structure & Gradle configuration
- [x] Documentation (architecture, rules, rendering, multiplayer)
- [x] Build & install scripts
- [x] License & README

## Phase 1: Core Game Logic (No UI)
> Pure Kotlin, no Android dependencies. Fully testable.

- [ ] **Board model**: `Board` class representing 3×3×3 cube
  - 3D array of `CellState` enum (`EMPTY, X, O, Y, Z`)
  - Methods: `getCell(layer, row, col)`, `setCell(...)`, `isFull()`
- [ ] **Player model**: `Player` data class (id, name, symbols)
- [ ] **Move model**: `Move` data class (player, symbol, position)
- [ ] **Turn manager**: Tracks whose turn it is, validates turn order
- [ ] **Rules engine**: (placeholder until rules are finalized)
  - `isValidMove(board, move): Boolean`
  - `checkWin(board): WinResult?`
  - `getWinningLines(): List<Line3D>`
- [ ] **Game session**: Orchestrates a full game (init → moves → end)
- [ ] **Unit tests**: Cover all core logic

### Key files:
```
src/main/kotlin/com/xoyz/game/core/
├── Board.kt
├── CellState.kt
├── Player.kt
├── Move.kt
├── TurnManager.kt
├── RulesEngine.kt
└── GameSession.kt
```

## Phase 2: 3D Rendering
> LibGDX integration for visualizing the cube.

- [ ] **LibGDX setup**: Add dependencies, create AndroidApplication
- [ ] **Cube renderer**: Draw 3×3×3 wireframe grid
- [ ] **Cell renderer**: Show symbols in occupied cells
- [ ] **Camera**: Orbital camera with touch rotation/zoom
- [ ] **Input**: Tap → ray cast → cell selection
- [ ] **Layer visibility**: Toggle or spread layers for clarity
- [ ] **Desktop module**: For faster iteration without deploying to phone

### Key files:
```
src/main/kotlin/com/xoyz/game/engine/
├── GameRenderer.kt
├── CubeBuilder.kt
├── CameraController.kt
├── InputHandler.kt
├── CellHighlighter.kt
└── SymbolRenderer.kt
```

## Phase 3: Android UI Integration
> Wire everything together in an Android app.

- [ ] **MainActivity**: App entry, navigation
- [ ] **GameActivity**: Hosts LibGDX view + game HUD
- [ ] **Main menu**: New game, join game, settings
- [ ] **HUD overlay**: Current player, symbol selection, turn indicator
- [ ] **Game over screen**: Winner announcement, play again

### Key files:
```
src/main/kotlin/com/xoyz/game/ui/
├── MainActivity.kt
├── GameActivity.kt
├── MenuScreen.kt
├── GameHUD.kt
└── GameOverDialog.kt
```

## Phase 4: Multiplayer
> Online play via room codes.

- [ ] **Firebase setup**: Project config, google-services.json
- [ ] **Room creation**: Generate code, create DB entry
- [ ] **Room joining**: Enter code, connect to room
- [ ] **State sync**: Real-time board state synchronization
- [ ] **Reconnection**: Handle disconnects gracefully
- [ ] **Lobby UI**: Waiting screen, player readiness

### Key files:
```
src/main/kotlin/com/xoyz/game/multiplayer/
├── RoomManager.kt
├── GameSync.kt
├── NetworkProtocol.kt
└── ConnectionManager.kt
```

## Phase 5: Polish
- [ ] Animations (symbol placement, win highlight)
- [ ] Sound effects
- [ ] Haptic feedback
- [ ] Theme/color customization
- [ ] Tutorial / how-to-play screen
- [ ] App icon & splash screen

## Dependencies Summary

| Dependency | Purpose | Phase |
|------------|---------|-------|
| Kotlin stdlib | Language | 0 |
| AndroidX Core | Android basics | 0 |
| AndroidX AppCompat | UI compatibility | 0 |
| LibGDX | 3D rendering | 2 |
| Firebase RTDB | Multiplayer sync | 4 |
| Firebase Auth | Anonymous auth | 4 |
| JUnit 5 | Unit testing | 1 |
| Mockk | Mocking for tests | 1 |

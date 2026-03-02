# Architecture Overview

## System Diagram

```
┌─────────────────────────────────────────────────┐
│                   XOYZ App                      │
├─────────────┬──────────────┬────────────────────┤
│   UI Layer  │ Game Engine  │   Multiplayer      │
│  (Activities│ (3D Render,  │   (Networking,     │
│   Fragments)│  Input)      │    Room Codes)     │
├─────────────┴──────┬───────┴────────────────────┤
│              Core Game Logic                     │
│  (Board State, Rules Engine, Turn Manager)       │
├──────────────────────────────────────────────────┤
│              Android Platform / LibGDX           │
└──────────────────────────────────────────────────┘
```

## Module Breakdown

### `core/` — Game Logic
- **Board representation**: 3×3×3 cube as a 3D array (27 cells)
- **Cell state**: `EMPTY | X | O | Y | Z`
- **Turn manager**: Alternates between Player 1 (X/Y) and Player 2 (O/Z)
- **Rules engine**: Win conditions, valid moves, symbol selection logic
- **Game state**: `IN_PROGRESS | PLAYER1_WINS | PLAYER2_WINS | DRAW`

### `engine/` — 3D Rendering
- **Cube renderer**: Draws the 3×3×3 grid in 3D space
- **Camera controller**: Rotate/zoom around the cube
- **Cell renderer**: Renders X/O/Y/Z symbols in cells
- **Input handler**: Tap detection → ray casting → cell selection
- **Animation system**: Symbol placement, win line highlight

### `ui/` — Android UI
- **MainActivity**: Entry point, navigation
- **GameActivity**: Hosts the 3D game view
- **MenuScreen**: Main menu, game mode selection
- **SettingsScreen**: Player names, preferences
- **LobbyScreen**: Multiplayer room creation/joining

### `multiplayer/` — Networking
- **RoomManager**: Create/join rooms via shared codes
- **GameSync**: Synchronize game state between players
- **Protocol**: Message format for moves, state updates
- **Connection**: WebSocket / Firebase Realtime DB abstraction

### `utils/` — Utilities
- **Constants**: Board dimensions, symbol definitions
- **Extensions**: Kotlin extension functions
- **Logger**: Debug logging wrapper

## Data Flow

```
User Tap → InputHandler → CellSelection → RulesEngine.validateMove()
    → BoardState.update() → Renderer.refresh() → [Multiplayer.broadcast()]
```

## Key Design Decisions

1. **Separation of concerns**: Game logic is completely independent of rendering.
   The `core/` module has zero Android/OpenGL dependencies — it's pure Kotlin.
   This makes it testable and potentially shareable with a server.

2. **LibGDX vs Raw OpenGL**: LibGDX is recommended for the 3D rendering layer.
   It provides scene graph, input handling, and cross-platform support out of the box.
   If we go with raw OpenGL ES, we'll need to implement all of this ourselves.

3. **Multiplayer abstraction**: The multiplayer layer is behind an interface so we
   can swap Firebase, WebSockets, or any other backend without touching game logic.

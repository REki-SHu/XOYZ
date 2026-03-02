# Multiplayer Architecture

## Overview

Players can play XOYZ together over the internet by sharing a **room code**.
One player creates a room and gets a code; the other joins using that code.

## Architecture Options

### Option A: Firebase Realtime Database (Recommended for MVP)

**How it works:**
- Room = a node in Firebase RTDB keyed by room code
- Both players listen to the same node for real-time state updates
- Moves are written to the DB, triggering listeners on the other side

**Pros:**
- No backend to deploy or maintain
- Real-time sync out of the box
- Free tier is generous (100 concurrent connections)
- Easy auth (anonymous auth for quick start)

**Cons:**
- Vendor lock-in to Firebase/Google
- Requires internet (no LAN play)
- Game logic validation should still be client-side (or use Cloud Functions)

**Dependencies:**
```kotlin
implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
implementation("com.google.firebase:firebase-database-ktx")
implementation("com.google.firebase:firebase-auth-ktx")
```

### Option B: Custom WebSocket Server

**How it works:**
- Lightweight server (Ktor / Node.js) manages rooms
- Clients connect via WebSockets
- Server validates moves and broadcasts state

**Pros:** Full control, no vendor lock-in, can add LAN play.
**Cons:** Need to deploy and maintain a server.

### Option C: Peer-to-Peer (WebRTC)

**How it works:**
- Players connect directly via WebRTC data channels
- Still need a signaling server for initial connection

**Pros:** Lowest latency, no ongoing server costs.
**Cons:** Complex NAT traversal, harder to implement.

## Room Code System

- 6-character alphanumeric code (e.g., `X7K2M9`)
- Generated server-side (or in Firebase)
- Expires after game ends or after timeout (e.g., 30 minutes)
- Case-insensitive for user convenience

## Game State Sync

### State Model
```
Room {
    code: String
    status: WAITING | IN_PROGRESS | FINISHED
    player1: PlayerInfo
    player2: PlayerInfo?
    board: Int[3][3][3]    // 0=empty, 1=X, 2=O, 3=Y, 4=Z
    currentTurn: 1 | 2
    moveHistory: List<Move>
    winner: 1 | 2 | null
    createdAt: Timestamp
}

Move {
    player: 1 | 2
    symbol: X | O | Y | Z
    position: (layer, row, col)
    timestamp: Timestamp
}
```

### Sync Flow
```
Player A makes move
  → Write move to Firebase/server
  → Server validates (optional)
  → Other player's listener fires
  → Update local board state
  → Re-render
```

## Security Considerations

- Validate moves on both clients (or server)
- Prevent move replay / tampering
- Rate limiting
- Room code brute-force protection
- Anonymous auth to prevent abuse

## Implementation Phases

1. **Phase 1**: Local two-player on same device (no networking)
2. **Phase 2**: Firebase-based multiplayer with room codes
3. **Phase 3**: Polish (reconnection, spectating, chat)

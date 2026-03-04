# Technical Approach & Algorithms

This document details the reasoning and implementation strategies for key technical decisions in the XOYZ architecture, specifically focusing on the multiplayer networking strategy and the algorithmic approach to game state validation (win/loss/valid moves).

---

## 1. Multiplayer Networking: P2P vs. Cloud Server

When designing the multiplayer architecture, two primary approaches were evaluated: **Peer-to-Peer (WebRTC)** and a **Dedicated/Cloud Server (Firebase RTDB)**. 

### Why not Peer-to-Peer (WebRTC)?
While P2P is often praised for its ultra-low latency and "serverless" nature, it is not the ideal fit for a turn-based mobile board game for the following reasons:

1. **NAT Traversal Issues:** Mobile networks (4G/5G) heavily rely on Carrier-Grade NAT (CGNAT). Establishing a direct WebRTC connection between two phones on different cellular networks is notoriously difficult and will often fail.
2. **Fallback to TURN:** Because direct P2P connections frequently fail over mobile networks, you would inevitably have to implement and pay for a **TURN server** to relay the traffic. This defeats the "free/serverless" benefit of P2P.
3. **Signaling Server Still Required:** Even in a purely P2P architecture, players need a central server (a signaling server) to exchange room codes and initial SDP connection offers/answers.
4. **Latency Requirements:** P2P is crucial for 60-FPS action games where 20ms latency matters. XOYZ is a turn-based board game; a delay of 100ms–300ms to register a move is completely unnoticeable to players.

### The Recommended Approach: Firebase RTDB (Option A)
We recommend sticking to **Firebase Realtime Database** (or a lightweight custom WebSocket server) for the MVP:

* **Zero NAT Headaches:** Both clients simply connect to Google's cloud servers. You never have to worry about cellular firewalls or port forwarding.
* **Built-in Synchronization:** Firebase natively handles state synchronization, offline caching, and conflict resolution.
* **Cost Effective:** The free tier easily supports 100 concurrent connections and 10GB/month of data transfer. Because XOYZ game states are tiny (just a few bytes), this can support thousands of games a month for free.

---

## 2. Game Logic: Win/Loss & Move Validation

The XOYZ board is a 3×3×3 cube containing exactly **27 cells** and exactly **49 possible winning lines**. 

### Why Avoid If-Else Ladders and Decision Trees?
* **If-Else Ladders:** Writing out 49 `if` statements (e.g., `if (board[0][0][0] == X && board[0][0][1] == X ...`) is extremely bloated, highly prone to developer typos, and a nightmare to maintain.
* **Decision Trees:** Decision trees (like Minimax algorithms) are designed for predicting future game states (building an AI opponent). They are massive overkill for simply checking if the *current* state is a win.

### The Optimal Approach: Bitboards (Absolute Fastest)
Since there are only 27 cells, the entire board state fits perfectly inside a single standard 32-bit Integer. We can use bitwise operations for incredibly fast validation.

**How it works:**
1. Conceptually flatten the 3×3×3 board into 27 bits (indices 0 through 26).
2. Maintain two integer variables representing each player's state:
   * `player1Board` (Stores `1`s wherever X or Y are placed)
   * `player2Board` (Stores `1`s wherever O or Z are placed)
3. **Precompute the 49 Winning Masks:** Calculate an integer representation for each of the 49 winning lines. For example, if a winning line occupies cells 0, 1, and 2, its mask is `1 | 2 | 4 = 7`.

**Implementation Example (Kotlin):**
```kotlin
// Precomputed array of the 49 winning line bitmasks
val winningMasks = intArrayOf(
    7,        // Line 0, 1, 2
    56,       // Line 3, 4, 5
    // ... all 49 combinations
)

fun checkWin(playerBoard: Int): Boolean {
    for (mask in winningMasks) {
        if ((playerBoard and mask) == mask) return true
    }
    return false
}
```
*Why this is good:* The CPU can process 49 bitwise `AND` operations in a fraction of a microsecond. Memory footprint is practically zero.

### The Alternative: Precomputed Index Arrays (Fast & More Readable)
If maintaining a traditional array is preferred for UI rendering purposes, you can still use a precomputed lookup array instead of bitwise math.

**How it works:**
1. Flatten your 3D array (`CellState[3][3][3]`) into a 1D array (`Array<CellState>(27)`).
2. Precompute the 49 winning lines as an array of integer arrays, each holding the 3 indices that make up a line.

**Implementation Example (Kotlin):**
```kotlin
val winLines = arrayOf(
    intArrayOf(0, 1, 2),    // Top layer, first row
    intArrayOf(0, 9, 18),   // Vertical column
    // ... 47 more combinations
)

fun getWinner(board: Array<CellState>): Player? {
    for (line in winLines) {
        val a = board[line[0]]
        if (a != CellState.EMPTY && a == board[line[1]] && a == board[line[2]]) {
            return getPlayerFromSymbol(a) // Helper function
        }
    }
    return null
}
```

### Validating Moves
Because the board size is strictly limited to 27 cells and is static, checking valid moves requires zero complex logic.

When a user taps a cell (resolved via raycasting in LibGDX to an index 0-26):
1. Check if the cell at that index is `CellState.EMPTY`.
2. Check if the game state is `IN_PROGRESS` (not already won or drawn).
3. Check if it is currently that player's turn.

No pathfinding or tree traversal is needed.

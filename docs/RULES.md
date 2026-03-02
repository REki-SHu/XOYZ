# Game Rules

> ⚠️ **Work in Progress** — Rules are not finalized yet. This document will be
> updated as the game design evolves.

## Overview

XOYZ is a 3D variant of Tic-Tac-Toe played on a **3×3×3 cube** (3 layers of 3×3 grids).

## Players & Symbols

| Player   | Symbols |
|----------|---------|
| Player 1 | X, Y    |
| Player 2 | O, Z    |

Each player controls **two** symbols instead of one.

## Board

- 3 layers (top, middle, bottom)
- Each layer is a 3×3 grid
- Total: **27 cells**

```
Layer 0 (Top)      Layer 1 (Mid)      Layer 2 (Bottom)
┌───┬───┬───┐      ┌───┬───┬───┐      ┌───┬───┬───┐
│   │   │   │      │   │   │   │      │   │   │   │
├───┼───┼───┤      ├───┼───┼───┤      ├───┼───┼───┤
│   │   │   │      │   │   │   │      │   │   │   │
├───┼───┼───┤      ├───┼───┼───┤      ├───┼───┼───┤
│   │   │   │      │   │   │   │      │   │   │   │
└───┴───┴───┘      └───┴───┴───┘      └───┴───┴───┘
```

## Turn Structure

> TODO: Define how turns work.
> - Does each player place one symbol per turn?
> - Can they choose which of their two symbols to place?
> - Is there a forced alternation between symbols?

## Win Conditions

> TODO: Define what constitutes a win.
> - Three in a row on a single layer? (horizontal, vertical, diagonal)
> - Three in a row across layers? (vertical columns, 3D diagonals)
> - Does a winning line need to be the SAME symbol, or can it be any
>   combination of a player's two symbols?
> - How many winning lines exist on a 3×3×3 cube?

### Possible Winning Lines (3×3×3 cube)
- **Rows within a layer**: 3 rows × 3 layers = 9
- **Columns within a layer**: 3 columns × 3 layers = 9
- **Diagonals within a layer**: 2 diagonals × 3 layers = 6
- **Vertical columns**: 3 × 3 = 9
- **Vertical plane diagonals**: 2 × 3 (row-planes) + 2 × 3 (col-planes) = 12
- **Space diagonals**: 4
- **Total: 49 possible lines**

## Draw Condition

> TODO: Define draw conditions.
> - All 27 cells filled with no winner?

## Additional Rules to Decide

- [ ] Symbol selection: can a player choose which symbol to play on each turn?
- [ ] Are there any restrictions on symbol placement?
- [ ] Scoring system (if any)?
- [ ] Time limits per turn?
- [ ] Best of N rounds?

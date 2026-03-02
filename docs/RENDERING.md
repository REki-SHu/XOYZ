# 3D Rendering Approach

## Decision: LibGDX vs Raw OpenGL ES vs Filament

### Option A: LibGDX (Recommended)

**LibGDX** is a mature, lightweight, cross-platform game framework with excellent
Android support and a Kotlin-friendly API.

**Pros:**
- Built-in 3D scene graph, camera, input handling
- Model loading (OBJ, GLTF, FBX via fbx-conv)
- Works seamlessly with Gradle — no IDE required
- Large community, extensive documentation
- Cross-platform: same code can run on desktop for testing
- ~5-8 MB added to APK size

**Cons:**
- Slightly opinionated architecture (ApplicationAdapter pattern)
- Some abstractions may be more than we need for a simple cube

**Dependencies (Gradle):**
```kotlin
// In app/build.gradle.kts
val gdxVersion = "1.12.1"

implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
implementation("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")
```

### Option B: Raw OpenGL ES 3.0

Write shaders and rendering pipeline directly.

**Pros:** No dependencies, full control, smallest APK.
**Cons:** Massive boilerplate — vertex buffers, shader compilation, matrix math,
input raycasting all need to be written from scratch.

### Option C: Filament (Google)

Modern PBR renderer.

**Pros:** Beautiful rendering, Google-supported.
**Cons:** Complex setup, smaller community, heavier.

## Rendering Requirements

### The Cube
- 3×3×3 wireframe cube grid
- Each cell needs to be individually selectable
- Visual distinction between layers (transparency, color coding)
- Occupied cells show the placed symbol (X, O, Y, Z) as 3D text or flat decals

### Camera
- Orbital camera: rotate around the cube by swiping
- Pinch to zoom
- Snap-to-angle options (top view, side view, isometric)

### Input
- Tap on a cell to select it
- Ray casting from screen tap to 3D cell coordinates
- Visual feedback: highlight hovered/selected cell

### Animations
- Symbol placement animation (scale up, fade in)
- Winning line highlight (glow, pulse)
- Layer transition (slide layers apart for better visibility)

### Visual Style
- Clean, minimal aesthetic
- Semi-transparent cube faces
- Bold, clear symbols
- Player-specific colors (e.g., Player 1 = blue, Player 2 = red)

## Asset Pipeline

If using LibGDX:
- 3D models: create in Blender → export as GLTF → convert with `fbx-conv`
- OR: generate geometry programmatically (simpler for a cube grid)
- Textures: PNG files in `assets/`
- Shaders: GLSL files in `assets/shaders/` (if custom shaders needed)

## Desktop Testing

With LibGDX, we can add a `:desktop` module that runs the same game code
in a LWJGL window — much faster iteration than deploying to a phone every time.

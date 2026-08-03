# Roadmap

Each wave is **one feature**. The issues under a wave are the work that feature needs, and the wave closes when its definition of done holds. Waves are ordered by dependency, not by how much I want to build them — a wave only starts once everything it leans on exists.

This file is the shape of the plan. The [project board](https://github.com/users/FinnWillow/projects/1/views/1) is the live view: it has the real issues, their state, and anything filed since I last touched this file. If the two disagree, the board wins.

The rendering waves follow Learn OpenGL chapter for chapter, from Getting Started through Advanced Lighting, plus the parts of In Practice the engine genuinely needs: debugging folds into Wave 3, and text rendering gets Wave 10 to itself. Waves 9 and 10 together are the 2D half of the engine, which the book only ever teaches inside its Breakout game. **PBR is deliberately out of scope** — the rendering track stops at the chapter before it.

---

# Wave 0: Library foundation

Every third-party library I intend to build *with* gets installed, configured, and proven to run inside this project before any engine code is written around it. The whole point is to build with these libraries instead of around them, so they land before everything else. This wave is a prerequisite rather than a feature — it is the only one that works that way.

## Issues

- [X] **JOML** — math library installed and used for all vector and matrix work. This one is also a standing rule rather than a one-off task: if a structure ever shows up that duplicates a JOML type (a hand-rolled Vector3, say), that structure gets deleted and the code refactored onto JOML.
- [X] **ode4j** — physics installed, with an empty world that steps without errors. No bodies yet; this is the bare minimum "it compiles and runs" check.
- [X] **JCEF** — chromium embedded, booting in windowless mode and loading a page without errors.
- [X] **artemis-lousia** — artemis-odb is discontinued, so the ECS runs on my own fork installed via JitPack. Fixes to it have to be made in house from here on, sadly or thankfully depending on how you look at it. Proven by a world that creates an entity and processes a system.

## Definition of done

Every library above has a passing test under `src/test/java/studio/paperwing/Test*.java` that exercises it for real, and the fork resolves from JitPack in a clean build.

## Notes

This wave used to name **Jolt** as the physics library. I went with **ode4j** instead — it is what is in the pom and what the physics test drives, so the Jolt entry is dead.

The old JCEF item also demanded a working test GUI drawing inside the OpenGL window and talking to the java code. That is a real goal but it is not a library-installation goal, so it moved down to Wave 13 where the editor shell actually gets built.

---

# Wave 1: Rendering fundamentals

*Learn OpenGL: Getting Started.*

Get correct pixels on screen and understand the pipeline end to end. This wave is still allowed to live in `Main` and be ugly — Waves 2 and 3 are where it gets cleaned up.

## Issues

- [ ] **Shaders** — a shader class, as per the tutorial, that can change input values and be used for drawing a model.
- [ ] **Textures** — load bitmap textures and pass them to a shader that draws them on the model. Loading from a hardcoded path is fine here; the real asset system is Wave 6.
- [ ] **Transformations** — position, rotation and scale applied to a model through a transform matrix.
- [ ] **Coordinate system** — properly formed MVP matrices, with depth testing and back face culling switched on. Both get a proper treatment later in Wave 7; here they just need to be enabled and correct.
- [ ] **Camera** — a virtual camera that can position, rotate and scale (zoom in and out) in 3D space.

## Definition of done

A textured model draws through a full MVP matrix with depth testing and back face culling enabled, and a camera can move around it.

## Notes

Learn OpenGL is the one and only source of truth for scope across every rendering wave. It will tell you what features must be added and, just as importantly, which ones must not. If it is not in the chapter a wave names, it is not in that wave.

---

# Wave 2: Windowing and input

Move the window out of `Main`. Everything that is about *being an application* rather than about drawing lives here.

## Issues

- [ ] **Singular window object** — owns the OpenGL window and its lifecycle, and on its own lets other components connect to it and do things to it.
- [ ] **Input/event system** — input properly disconnected from the window, so action events from mouse, keyboard or anything else can be read, and window events like closing or resizing can be raised.
- [ ] **Frame loop and timing** — a loop that owns its update and render steps on a defined timestep, instead of the render loop doing everything inline.

## Definition of done

`Main` contains no GLFW calls at all, and a key press or window resize reaches gameplay code as an event rather than as a callback wired straight to the window.

## Notes

Build the frame loop expecting a fixed-step update alongside the variable-step render, even though nothing needs it yet. Wave 11 steps physics on a fixed rate, and retrofitting a second rate into a loop that only ever knew one means rewriting the loop instead of adding to it.

---

# Wave 3: Renderer core and diagnostics

*Includes Learn OpenGL: In Practice — Debugging.*

The other half of the split: everything about drawing gets its own reusable object, so the six rendering waves that follow are built on real architecture instead of piling onto `Main`.

The debugging chapter lands here rather than later because every wave after this one is harder to build without it. Chasing a silent failure through deferred shading with nothing but a manual `glGetError` call is not a thing I want to do to myself.

## Issues

- [ ] **Singular renderer object** — the other half of the window; provides basic rendering capability to objects that need it.
- [ ] **Shader abstraction** — the Wave 1 shader class promoted into something that owns its uniforms and can be reused across draws. Materials stay out of it until Wave 5 defines what one actually is.
- [ ] **Error handling** — `processErrors`, `processShaderErrors` and `processProgramErrors` currently live on `Main` and get imported by the tests. They belong to the renderer.
- [ ] **Debug output** — a debug context with `glDebugMessageCallback`, so errors arrive automatically with their own context and a stack trace instead of being polled for by hand.
- [ ] **Shader diagnostics** — validating shaders against the GLSL reference compiler, and the technique of debugging a shader by writing intermediate values out as color.

## Definition of done

`Main` contains no OpenGL calls at all — it constructs a runtime, starts it, and exits. The OpenGL tests drive the renderer instead of static helpers hanging off `Main`, and a deliberately broken draw call reports itself through the debug callback without anyone asking it to.

## Notes

Do not hard-code a forward pass into the renderer. Wave 8 restructures the frame into a deferred g-buffer pass and a lighting resolve, and Wave 9 then has to composite a 2D pass against whatever that frame ends up being. Both are much cheaper if the pass structure was never assumed to be fixed.

The debugging chapter's framebuffer-output technique needs framebuffers, which is Wave 7. It gets picked up there rather than here.

Worth fixing while in the area: `processErrors` reads a single code off the error queue, but OpenGL can have several errors pending at once and only clears one per `glGetError` call. It should drain in a loop until it sees `GL_NO_ERROR`.

---

# Wave 4: ECS and scenes

Make "entities as data driven by systems" real. Artemis is already installed; this wave is about what gets built on top of it and what the engine's own components and systems look like.

This lands **before** the remaining rendering chapters on purpose. Lights, models, sprites and instanced draws all want to be components, and bolting ECS on after six waves of standalone rendering code means rewriting all six.

## Issues

- [ ] **Entity conventions** — decide and document how components and systems get declared, including where the `components` package has to sit, since artemis looks for a folder named `components` somewhere in the project rather than an exact package route. Pick a real name for the base game object here; anything is more sensible than "data entity".
- [ ] **Core components** — at minimum a transform and a model/mesh reference.
- [ ] **Render system** — a system that walks entities holding a transform and a model and feeds them to the Wave 3 renderer.
- [ ] **Scene object** — groups entities and calls systems on them, globally.
- [ ] **Scene switching** — change the current scene at runtime.
- [ ] **Packed scenes** — a scene marked as packed can be instantiated by the programmer; one that is not marked as such cannot.

## Definition of done

Two or more entities, each with their own transform and model, render and behave according to their systems in the same run with no errors, and switching scenes swaps what is on screen. The example systems can be basic.

## Notes

Design the scene object knowing that Wave 9 needs it to hold 2D and 3D content side by side in separate dictionaries. Nothing here has to implement that, but a scene that assumes it only ever holds one kind of thing will have to be torn open later.

The render system built here submits one draw per entity. Wave 7 replaces that with instancing, so keep draw submission behind a seam the renderer owns rather than spread through the system.

---

# Wave 5: Lighting

*Learn OpenGL: Lighting.*

Phong shading, start to finish. Lights become components and a lighting system feeds them to the renderer.

## Issues

- [ ] **Colors** — a light source object and the color model everything else builds on.
- [ ] **Basic lighting** — ambient, diffuse and specular put together into Phong.
- [ ] **Materials** — surface material properties driving the lighting maths.
- [ ] **Lighting maps** — diffuse and specular maps replacing flat material constants.
- [ ] **Light casters** — directional lights, point lights and spotlights.
- [ ] **Multiple lights** — several casters of mixed type combined in one pass.

## Definition of done

A scene with a directional light, several point lights and a spotlight renders correctly on materials using diffuse and specular maps, with every light expressed as an ECS component.

## Notes

This has to come before Wave 6, not after, even though an asset system sounds more foundational than shading. Assimp hands back meshes carrying diffuse and specular maps, and those are only meaningful once this chapter has defined what a material is. Load models first and the mesh class gets built against a material concept that does not exist yet.

---

# Wave 6: Assets and model loading

*Learn OpenGL: Model Loading, plus the engine's own resource layer.*

Stop hardcoding data into source files. Everything the engine draws comes off disk through a resource handle.

## Issues

- [ ] **Assimp integration** — the loader wired up and importing scenes.
- [ ] **Mesh** — engine-side mesh data with its own buffers and draw call.
- [ ] **Model** — a mesh tree loaded from a file and drawn as one unit.
- [ ] **Texture assets** — the ad-hoc texture loading from Wave 1 promoted into the asset system.
- [ ] **Project layout** — a defined on-disk structure for a project's assets. The editor's folder explorer in Wave 14 reads this, so it has to exist first.
- [ ] **Resource handles** — assets referenced by handle, loaded once and cached, rather than re-read at every use.

## Definition of done

A model and its textures load off disk by handle and render under Wave 5's lighting, with no vertex or image data written in java source.

---

# Wave 7: Advanced OpenGL

*Learn OpenGL: Advanced OpenGL.*

The chapter that turns a renderer into a capable one. **Instancing is the load-bearing item here** — it is what makes the thousands-of-objects goal in the README achievable at all.

## Issues

- [ ] **Depth testing** — depth functions, the depth buffer, and z-fighting.
- [ ] **Stencil testing** — the stencil buffer and object outlining.
- [ ] **Blending** — transparency, discarding fragments, and sorting blended draws.
- [ ] **Face culling** — winding order and cull configuration, properly this time.
- [ ] **Framebuffers** — render to texture, and post-processing off the back of it.
- [ ] **Cubemaps** — skyboxes and environment mapping.
- [ ] **Advanced data and GLSL** — buffer management, uniform buffer objects, interface blocks.
- [ ] **Geometry shader** — the third stage wired into the shader abstraction.
- [ ] **Instancing** — one draw call for many entities. Replaces the per-entity submission from Wave 4.
- [ ] **Anti-aliasing** — MSAA, both on the default framebuffer and off-screen.

## Definition of done

A scene renders through an off-screen framebuffer with MSAA, a skybox behind it, blended transparent surfaces sorted correctly, and a large instanced entity count drawn in a single call.

## Notes

Blending, sorting and instancing here are what Wave 9 builds its sprite batcher out of, and framebuffers are what let Wave 3's deferred debugging technique finally get picked up. This chapter has more downstream reach than its position in the book suggests.

---

# Wave 8: Advanced lighting

*Learn OpenGL: Advanced Lighting. The last of the 3D rendering chapters — the one after it is PBR, which is out of scope.*

## Issues

- [ ] **Blinn-Phong** — the specular fix over plain Phong.
- [ ] **Gamma correction** — a correct linear pipeline end to end.
- [ ] **Shadow mapping** — directional light shadows.
- [ ] **Point shadows** — omnidirectional shadow maps.
- [ ] **Normal mapping** — tangent space and per-fragment normals.
- [ ] **Parallax mapping** — height and depth mapping over normal maps.
- [ ] **HDR** — high dynamic range rendering and tone mapping.
- [ ] **Bloom** — the blur and composite pass.
- [ ] **Deferred shading** — a g-buffer pass and lighting resolve.
- [ ] **SSAO** — screen space ambient occlusion on the deferred path.

## Definition of done

A scene renders through a deferred path with HDR, bloom, SSAO, gamma-correct output, normal mapped surfaces and shadow-casting directional and point lights.

---

# Wave 9: 2D rendering

The engine is meant to handle 2D as a first-class mode, not as 3D with the camera pointed at a wall. A scene can be both 2D and 3D at once, storing each in its own dictionary, so this wave builds the 2D half of that: an orthographic pass, sprites, and a batcher that keeps the draw call count flat.

It needs Wave 7 for blending, sorting and instancing, and it follows Wave 8 rather than preceding it because the 2D pass has to composite against a finished 3D frame. Wave 8 turns that frame into a deferred g-buffer pass and a lighting resolve, so building the composite point first means building it against a renderer that is about to change shape.

## Issues

- [ ] **Orthographic pass** — a 2D projection and a defined point in the frame where it composites against the 3D pass, since a scene can run both.
- [ ] **2D scene data** — the 2D half of a scene stored separately from the 3D half, per the dual-dictionary design, built on the Wave 4 scene object.
- [ ] **Sprite** — a textured quad with position, rotation, scale and tint, expressed as a component.
- [ ] **Sprite sheets and atlases** — sprites addressing a region of a shared texture rather than owning one each.
- [ ] **Sprite batching** — many sprites drawn in few calls, on top of Wave 7's instancing. This is the thousands-of-objects goal applied to 2D.
- [ ] **Sort order** — explicit draw ordering and sorting layers, since blended 2D cannot lean on the depth buffer the way the 3D path does.
- [ ] **2D camera** — pan and zoom in the orthographic pass, independent of the 3D camera.

## Definition of done

A scene draws a large number of sprites from a shared atlas in a handful of draw calls, correctly ordered and blended, with a 2D camera panning over them — and the same scene can hold 3D content that renders in the same frame.

---

# Wave 10: Text rendering

*Learn OpenGL: In Practice — Text Rendering.*

Glyphs on screen from inside the engine. This follows Wave 9 rather than standing alone because a glyph is a sprite: the atlas, the batcher and the orthographic pass all already exist by this point, and text should reuse them instead of building a second 2D path beside the first.

This is not made redundant by the editor UI. JCEF draws the editor chrome, but it cannot draw *inside* the GL viewport — anything a running game needs to show, and any overlay that has to sit on top of the scene while the editor is not even open, comes from here.

## Issues

- [ ] **Glyph rasterization** — a font rasterized into usable glyph data.
- [ ] **Glyph atlas** — glyphs packed into a texture atlas with their metrics, instead of a texture per character.
- [ ] **Text draw path** — a string drawn at a position, size and color, submitted through Wave 9's sprite batcher, so a page of text costs about what a page of sprites costs.
- [ ] **Signed distance field text** — the extension the chapter points at, for text that stays sharp when scaled.
- [ ] **Debug overlay** — frame time and frame stats drawn over the scene, which is the first thing this feature buys and a down payment on the Wave 13 profiler panel.

## Definition of done

Arbitrary strings render at arbitrary positions, sizes and colors over a 3D scene, and a live frame-time overlay draws on top of the scene without the editor running.

## Notes

Learn OpenGL uses FreeType, which would mean adding `lwjgl-freetype` to the pom. The cheaper route is `stb_truetype`, which is already available through the `lwjgl-stb` dependency the project pulls in today. Worth deciding before starting rather than during.

---

# Wave 11: Physics

Wire ode4j into the ECS so bodies actually simulate and the things on screen follow them.

This is the one wave placed by preference rather than dependency. It needs nothing past Wave 4, so it can be pulled forward at any point; it sits here so the rendering track from Wave 5 to Wave 10 runs uninterrupted. Move it up the moment gameplay simulation starts mattering more than the next rendering chapter.

## Issues

- [ ] **Physics world subsystem** — ODE world creation, stepping and teardown owned by the engine rather than by a test.
- [ ] **Body and collider components** — rigid bodies and collision shapes expressed as components.
- [ ] **Fixed timestep** — the physics step runs on a fixed step, decoupled from render frame rate, using the seam left for it in the Wave 2 frame loop.
- [ ] **Transform sync** — simulated body positions drive the transform components the renderer reads.
- [ ] **Collision events** — contacts surfaced to systems as events rather than polled.

## Definition of done

A stack of boxes dropped onto a floor falls, collides, and settles, drawn entirely from ECS transforms driven by the simulation.

---

# Wave 12: Audio

OpenAL is already in the pom and doing nothing. This wave gives it a job.

## Issues

- [ ] **Audio device lifecycle** — OpenAL device and context owned by the engine.
- [ ] **Sound assets** — audio files loaded through the Wave 6 asset system.
- [ ] **Source and listener components** — sound emitted from an entity, heard from the camera.

## Definition of done

A positional sound plays from a moving entity and pans correctly as the camera moves.

---

# Wave 13: Editor shell

The IDE, structurally — the JCEF surface and the panel chrome, not the level editor itself. Expect a mix of writing straight HTML/CSS and editing in the IDE itself, similar to how Android Studio has it. The layout deliberately avoids the usual 700 panels and dropdowns: the scene editor is the whole window by default, with a horizontal panel below it and a floating one to the side.

## Issues

- [ ] **Embedded browser surface** — the JCEF view composited with the engine window instead of living in its own AWT frame.
- [ ] **Java to JS bridge** — a two-way message router, so web code can call into the engine and the engine can push state back out.
- [ ] **Panel chrome** — the center editor panel, the bottom panel and the floating side panel, with only the center one visible by default.
- [ ] **Console panel** — engine errors and log output rendered into the bottom panel.
- [ ] **Profiler panel** — frame timings living alongside the console, reading the stats the Wave 10 overlay already collects.

## Definition of done

A control in the web UI visibly changes something in the GL scene — a button that changes the background color or the color of a mesh, whatever — and engine log output shows up in the console panel. This is the old JCEF goal from Wave 0, moved to where it actually belongs.

---

# Wave 14: Level editor

The built-in 3D level editor I promised from the very start. Everything here is driven by context menus rather than a wall of toolbars.

## Issues

- [ ] **Selection and picking** — click an entity in the viewport to select it.
- [ ] **Context menu editing** — add and remove entities, components, and parent/child connections straight from the viewport.
- [ ] **Property viewer** — the floating panel reflects the selection: the whole entity component view when an entity is selected, a single component when one is focused.
- [ ] **Component authoring** — create new component types from the add panel.
- [ ] **Scene tree** — the bottom panel as a scene tree for parenting and selection, in both a cascading view and a free form tree view.
- [ ] **Folder explorer** — the bottom panel as a project file browser reading the Wave 6 project layout, again in both a cascading and a free form tree view.
- [ ] **2D/3D scene mode** — toggle the mode of the scene, not the view. One scene can be both at once, storing each in its own dictionary, on the split Wave 9 built.
- [ ] **Edit vs play mode** — enter and exit edit mode.

## Definition of done

I can build a small scene entirely from the editor, save it, and run it, without hand-editing anything on disk.

---

# Unscheduled

Things I know are coming but refuse to design yet, because I do not want to corner myself into decisions I might revert right away. These become waves when they turn into present problems.

- **PBR** — the Learn OpenGL chapter the rendering track deliberately stops before. Theory, lighting and IBL. It is the obvious next rendering wave whenever I decide the deferred path from Wave 8 has earned it.
- **Sprite animation** — frame sequences over the Wave 9 sprite path. Held back with the rest of animation rather than split off on its own.
- **Slang** — shader language compiler, worth a look as a replacement for hand-written GLSL. Wave 8 is roughly the point where hand-writing this many shader variants starts to hurt.
- **Animation** — skeletal animation and an animation editor.
- **Shader and sound editors** — the editor side of those two subsystems.
- **Multi-platform natives** — the pom hardcodes `natives-windows`. Linux and macOS need profiles before anyone else can build this.
- **Packaging and export** — turning a project into something a player can actually run.

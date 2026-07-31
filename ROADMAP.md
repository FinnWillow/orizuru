# Wave 1: Building the floor of the engine.

This phase focuses on building the bare minimum working parts of the engine that later waves hinge on to work.

## Phase 1: Library integrations.

This phase focuses on installing and properly configuring the libraries required in Wave 2. Idea being that I set the foundation for what's to come, and build with them instead of arround them.

### DoD:

- [ ] **JOML**

- it will be considered done if every site that would need it uses it. For example, if a Vector3 style structure exists that serves the purpose of a JOML Vector3 then that structure must be deleted and the code must be refactored to use the JOML version.

- [ ] **JCEF**

- installed and running a test GUI: something like a button that changes the background color of the window, or the color of some mesh, or whatever. Point being that something draws inside the OpenGL window, and the java code can properly interact with the web code.

- [ ] **Jolt**

- just like the others, installed and working in the engine, with a test empty physics world that shows everything compiling with no errors. at this moment in time there might not be any phisycs bodies that can be visibly interacting in the world, so this test should serve the bare minimum "it's working" test.

- [ ] **Artemis**

- after some checks and deliberations, i realized that artemis is completely discontinued. for it, a proper fork and installation of the fork via JitPack is required. Fixes to the codebase will be have to be made in house sadly, or thankfully, depending on how you see it. just like jolt, a empty world with no entities proving that it's working with no errors.

## Phase 2: Basic rendering.

These mainly look at what is in the Learn OpenGL getting started section; main focus is on building the first small parts of the engine, and nothing more.

### DoD:

- [ ] **Shaders**

- A shader class (as per the tutorial) that can change input values and be used for drawing a model.

- [ ] **Textures**

- Load bitmap textures, pass it to a shader that can properly draw the texture on the model.

- [ ] **Transformations**

- Position, rotation and scale that can be applied to the model using a Transform matrix.

- [ ] **Coordinate System**

- Properly formed MVP matrices along with proper depth testing and back face culling.

- [ ] **Camera**

- A virtual camera that can position rotate and scale (zoom in and out) in 3D space.

### Note:

For the definition of done to be completed the tutorial on Learn OpenGL will tell you all the things that you have to know, along with what features must be added, and what features are not. Use it as the one and only source of truth in terms of features. Along with that, the JOML math library is highly recomended to be installed in this phase and used for the different coordinate systems, vector math, and matrix multiplications.

## Phase 3: Basic abstraction.

The point of this phase is to move away from the Main completely and abstract every subsystem into something that can be reused. If not done already, all the parts in phase 1 should arlready have an abstract class or reusable object definition.

### DoD:

- [ ] **Singular window object**

- on its own it should let other components connect to a OpenGL window and do things to it.

- [ ] **Singular renderer object**

- this is the other half of the window and it should provide basic rendering capabilities to objects that need it.

- [ ] **Input/event system**

- the point of this is to properly abstract and disconnect the input system from the window so we can read action events from the mouse, keyboard or anything else, and also to call events like closing or resizing the window.

- [ ] **Data entity**

- this is supposed to be the base game object. since this engine is driven by "entities as data driven by systems" aka ECS this is the part where each part of the acronym is built how this data driven entity is called in terms of java class is up to the dev in that moment. please choose a more sensible name than "Data entity". It is considered done when you can make an entity with multiple components that get processed by their associated systems. example: one or more entities, each with their own model, physics body and transform, running with no errors in the same run. 

- [ ] **Scene system**

- something that can group data entities and call systems on them, globally. it must allow for changing the current scene, and it must let the programmer instantiate it as a packed scene only if it is marked as such. it is considered done when you can have one or more entities behaving according to their systems. again, these systems can be basic example systems.

# Wave 2: The walls and ceiling of the engine.

This wave focuses on adding everything together, making the missing things into reality, and when time comes this wave will become soundly structured, but untill i have the first wave done, i cant really tell what this entails, mainly thanks to ECS. But expect from this wave most if not all of the base infrastructure to be in place for a CLI game engine that can be used as is.

# Wave 3: Building the second floor.

This wave focuses on the IDE side of the engine. wiring up the ui into something useable and useful (expect a mix between writing straight up HTML/CSS and editing in the IDE itself, similar to how Android studio has it). In adition this phase will focus on one of the last phases of the engine, being the built in 3D level editor that i promised from the very start.
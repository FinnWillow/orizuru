# What this project is.

Tendoncy is a game engine focused on bringing together all the things I like to have or I'd like to have in one spot. Java, Jolt 3D physics engine, web driven ui, a proper 2D and 3D level editor inside a highly customizeable UI, and a ECS framework to put it all together.

# How am I going to achieve this?

By leaning heavily on build ready OSS libraries, I plan on integrating into this engine. A large portion of the systems present in this engine will not be made in house. The ones that I know of for certain are Jolt Physics, JOML for math, JCEF for the chrome/gui and Artemis ODB for the ECS, to name a few.

In the [ROADMAP.md](./ROADMAP.md) I will be describing to the best of my abbilities what each wave of implementations and what each phase within it will add to the engine.

# Requirements

- Java SDK 15 or higher.

- Maven 3.9 or newer.

# Common commands

```pwsh
mvn clean # cleans up the compiled files

mvn compile exec:java # compiles and runs the project

mvn test # runs the testing suite built under src/test/java/studio/paperwing/Test*.java
```
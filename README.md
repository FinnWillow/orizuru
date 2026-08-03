# What this project is.

Orizuru is a ECS driven game engine powered by artemis-lousia (a fork of artemis-odb managed currently by PaperWing 
Studio) that aims to be a highly optimized game engine that can handle thousands of objects running simultaneously at 
the same time.

# How am I going to achieve this?

By leaning heavily on build ready OSS libraries, I plan on integrating into this engine. A large portion of the systems 
present in this engine will not be made in house. The ones that I know of for certain are ode4j for Physics, JOML for 
math, JCEF for the chrome/gui and artemis-lousia for the ECS, to name a few.

In the [ROADMAP.md](./ROADMAP.md) I will be describing to the best of my abbilities what each wave of implementations 
will add to the engine. If you want a more up to date view of the roadmap, ckeck out the 
[project](https://github.com/users/FinnWillow/projects/1/views/1) page on github!

# Requirements

- Java SDK 15 or higher.

- Maven 3.9 or newer.

# Common commands

```pwsh
mvn clean       # cleans up the compiled files

mvn compile exec:java       # compiles and runs the project

mvn test        # runs the testing suite built under src/test/java/studio/paperwing/Test*.java
```

For testing (in case you don't know or forgot how JUnit Test works):

```pwsh
mvn test        # tests all the Test files in the test/ folder.

mvn test -Dtest=Test*       # tests the provided file, or files by rejex

mvn test -Dtest=Test*#methodName*        # tests the provided method or methods in the file by rejex.
```
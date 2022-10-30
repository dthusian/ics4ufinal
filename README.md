# ics4ufinal

This is the program I submitted for my Grade 12 Computer Science final project. It implements a basic Mahjong game with two standout features: 3D rendering and networked multiplayer.

It is written in Java using the LWJGL3 library.

## Running the code

There should be a .jar included with the source code. If not,
you can build one with Gradle. Run `./gradlew jar` (or if using
Windows CMD, `gradlew jar`). The jar must be executed on the same directory
as the assets directory.

You can play multiplayer with another player if they run the game,
and you type in their ip address, a colon, then the port number 34567.

## Architecture

The main game class is the MahjongGame class. This MahjongGame class
can have up to 4 controllers connected to it, some of which might be
controlled by bots or networked players. The MahjongGame server
only sends updates to the game state, so clients must maintain
their own local copy of the game state.

This is accomplished through the MahjongClientState class, which
tracks the all the game state that the client knows. In order to 
power animations, the client stub also updates the AnimationManager
class, which track tile model orientations and animations.

## Stuff

Tiles generated with https://jsfiddle.net/ejvpgLzd/4/

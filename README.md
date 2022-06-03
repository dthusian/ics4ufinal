
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
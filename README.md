
## Architecture

The main game class is the MahjongGame class. This MahjongGame class
can have up to 4 controllers connected to it, some of which might be
controlled by bots or networked players. The MahjongGame server
only sends updates to the game state, so clients must maintain
their own local copy of the game state.

This is accomplished through the MahjongClientState class, which
tracks the all the game state that the client knows. This class
is also passed to the MahjongRenderer class to render, because
the renderer only knows what the client knows anyway.
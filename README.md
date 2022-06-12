
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

Debug tiles generated with https://jsfiddle.net/Lbo6g5kt/2/

## TODO
- Test network code
- Impl pon/chi/kan
- Impl yaku/win condition
- Impl UI
- Impl replay game
- Impl animations (maybe)
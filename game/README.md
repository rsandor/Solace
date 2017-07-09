# The Game Directory

The `game/` directory holds all of the data for a game using the Solace engine. This document describes
how to modify and add additional files so a game author can create the exact game they desire on the
engine.

## Game Files and Common Extensions
Many game objects the engine processes can be describe by using files of various formats within the
game directory. Here is a quick list of common file extension recognized by the engine and what each
represents:

- `.config.xml`: Engine configuration
- `.emote.json`: Emote command definition
- `.js`: Game script


### Engine Configurations

**Extension:** `.config.xml`

Engine configuration are used to override specific aspects of the game engine, such as how combat calculations are
performed, the id of the starting room, etc. Configurations are structured as a hierarchical key-value map and can
be accessed via the `solace.util.Config` utility from both within the engine and by scripts.

Here's an example from the `prompt.js` command script that uses the configuration to fetch the default game prompt:
```js
var defaultPrompt = Config.get('default.prompt');
```

On game load the engine will search the `game/` directory for any files matching the `.config.xml` extension and load
the configurations into the `Config` utility. This means a game author can add as many additional custom configurations
as desired (as it may make it easier to tweak scripts, etc.).

### Emote Commands

**Extension:** `.emote.json`

Emotes are role playing commands that have no impact other than to add flavor to interactions between players.
On game load the engine will search the `game/` directory for any JSON files matching the `.emote.json` extension
and load them into the game. If the game encounters an unexpected format for the JSON file a warning will be
given in the server logs and the emote will be skipped.

Here is an example emote that conforms to the expected format:
```json
{
  "name": "example",
  "toPlayer": "Message to send to the player when they use the emote.",
  "toRoom": "Message to send to the room. %s (user name)",
  "withTarget": {
    "toPlayer": "Sent to the player when the emote has a target: %s (target name)",
    "toTarget": "Sent to target of the emote: %s (user name)",
    "toRoom": "Sent to room when an emote has a target: %s (user name) %s (target name)"
  }
}
```

### Game Scripts

**Extension:** `.emote.json`

Game scripts are JavaScript files that the game loads on startup that add programmatic functionality to the game.
A full treatment on scripting is out of the scope of this document, see [scripting.md](./scripting.md) for more
details.
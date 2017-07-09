# The Game Directory

The `game/` directory holds all of the data for a game using the Solace engine. This document describes
how to modify and add additional files so a game author can create the exact game they desire on the
engine.

## Game Files and Common Extensions
Many game objects the engine processes can be describe by using files of various formats within the
game directory. Here is a quick list of common file extension recognized by the engine and what each
represents:

- `.config.xml`: A game engine configuration file


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

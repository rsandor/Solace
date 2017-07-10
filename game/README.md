# The Game Directory

The `game/` directory holds all of the data for a game using the Solace engine. This document describes
how to modify and add additional files so an author can customize a game running on the solace engine
without the need to directly program against the engine itself.

## Game Files and Common Extensions
Many game objects the engine processes can be describe by using files of various formats within the
game directory. Here is a quick list of common file extension recognized by the engine and what each
represents:

- `.area.xml`: Area files
- `.config.xml`: Engine configuration
- `.emote.json`: Emote command definition
- `.race.json`: Player race definitions
- `.js`: Game script
- `.skill.json`: Player skill definition

### Area Files

**Extension:** `.area.xml`

Areas define the rooms, items, and mobiles that make up a game world for the solace engine. On game load the engine
will search the `game/` directory for any files matching the `.area.xml` extension and load the area as described in
each file it finds. Any errors the engine encounters while processing area files will be output as in the server's
log.

A full treatment of area creation is outside the scope of this document, please see [Solace Area Creation](./areas.md)
for more information.


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
  "name": "scowl",
  "toPlayer": "You scowl menacingly.",
  "toRoom": "%s scowls menacingly.",
  "withTarget": {
    "toPlayer": "You narrow your eyes, cock your head, and scowl at %s.",
    "toTarget": "%s gets a mean look upon their face and scowls at you.",
    "toRoom": "Something's amiss... %s just gave %s a pretty mean look."
  }
}
```

### Player Races

**Extension:** `.race.json`

Races are one of the primary character customization methods of the solace engine. Each race grants a single passive
enhancement and cooldown action to player characters. On game load the engine will search the `game/` directory for any
JSON files with the `.race.json` extension and register them as player races. Any errors that occur while loading a
race will be displayed in the server's log output.

here's an example of the format expected for `.race.json` files:
```json
{
  "name": "dwarf",
  "passives": ["stout-hearted"],
  "cooldowns": ["skullknock"]
}
```

### Game Scripts

**Extension:** `.js`

Game scripts are JavaScript files that the game loads on startup that add programmatic functionality to the game.
A full treatment on scripting is out of the scope of this document, see [scripting.md](./scripting.md) for more
details.

### Player Skills

**Extension:** `.skill.json`

Skills are one of the primary character customization methods of the solace engine. They define a set of passive
enhancements and cooldown actions that players can use to interact with the game world. On game load the engine will
search the `game/` directory for any JSON files with the `.skill.json` extension and register them as game skills.
Any errors encountered while processing skill definitions will be displayed in the server's log output.

Here's an example of the format for `.skill.json` files:
```json
{
  "id": "one-handed",
  "name": "One-handed",
  "passives": [
    { "level": 15, "name": "parry" },
    { "level": 40, "name": "second attack" },
    { "level": 80, "name": "third attack" },
    { "level": 100, "name": "battle trance" },
  ],
  "cooldowns": [
    { "level": 5, "name": "flurry" },
    { "level": 35, "name": "slash" },
    { "level": 65, "name": "riposte" },
    { "level": 95, "name": "coup" }
  ]
}
```



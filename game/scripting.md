# Solace Game Scripting

## Overview
The Solace game engine allows for various game features to be added and changed through
the use of scripting. Scripts are written in JavaScript and automatically loaded by the
game engine as part of engine initialization.

### Script Files
On load the engine looks for any files with the `.js` extension in the `game/` directory.
For each file it finds it attempts to load the script via the scripting engine. Note that
no particular order of execution is guaranteed.

### Core Scripts
The `scripts/` directory in the main repository contains core scripts used by to initialize
the scripting engine. On every scripting reload these are evaluated prior to any of the
scripts in the `game/` engine.

The following global utilities and services are exposed to all scripts via the core scripts:

- `Commands` - allows for game play command scripting (see below)
- `Log` - Provides the `solace.util.Log` utility service for server logging.
- `Strings` - The `solace.util.Strings` utility service used for complex string operations.
- `Color` - The `solace.util.Color` utility service for handling colors.
- `Config` - The `solace.io.Config` asset manager for reading configuration values.
- `Messages` - The `solace.io.Messages` asset manager for reading large text game messages.
- `Roll` - The `solace.util.Roll` utility used for rolling random numbers.
- `format` - The `java.lang.String.format` function for doing `sprintf` style formatting.
- `StringBuilder` - The `java.lang.StringBuilder` class used to efficiently build large strings.
- `Arrays` - `java.lang.Arrays` utility class.
- `Joiner` = The `com.google.common.base.Joiner` string utility for joining arrays together.

### Safe Execution
When the engine loads a player script it will evaluate the script in an environment such
that global engine scope cannot be accidentally altered. In JavaScript this equates to
wrapping the script's contents in a closure and then executing *that* closure. This means
that any author created scripts need not worry about adhering to module patterns and the
like.

Finally, all scripts are always executed in
"[safe mode](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Strict_mode)"
via the `'use strict';` pragma. Authors are free to include the pragma in their scripts
as it will be handled automatically, and without error, by the scripting engine.

## Command Scripting
Creating new commands via the scripting engine is rather straight forward. To do so the
script author should make use of the built-in `Commands` service that is exposed to all
scripts.

### Example: adding a command with `.add`
```js
'use strict';

/**
 * Allows the player to dance.
 * @param player The player executing the command.
 * @param params All parameters to the command including the command name.
 */
Commands.add('dance', function (player, params) {
  // Inform the player that they are dancing
  player.sendln("You dance up a storm.");

  // Send the dancing message to all other players in the room
  player.getRoom().sendMessage(
    format("%s dances up a storm!", player.getName(),
    player
  );
});
```

## Cooldown Scripting
Cooldown actions can also be scripted using the scripting engine. Creating cooldown commands
also uses the `Commands` global.

### Example: adding a cooldown command with `.addCooldown`
```js
'use strict';

/**
 * Potency 800 attack spell that costs 20% MP.
 * @author Ryan Sandor Richards
 */
Commands.addCooldown('flamestrike', {
  // Cooldowns have a slew of properties that can be set to change their behaviors:
  cooldownDuration: Commands.GLOBAL_COOLDOWN,
  initiatesCombat: true,
  castTime: 3,
  basePotency: 400,
  savingThrow: 'reflex',
  castMessage: 'You begin casting flamestrike...',
  mpCost: 20,

  /**
   * The "run" property should be a lambda that is executed when a players runs the command.
   * @param player The player executing the cooldown.
   * @param target The attack target for the cooldown.
   * @param level The level of the skill that grants the cooldown.
   * @param cooldown The `solace.cmd.CooldownCommand` instance for the cooldown.
   */
  run: function (player, target, level, cooldown) {
    return cooldown.executeAttack(player, target);
  }
});
```
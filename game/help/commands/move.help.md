@name(move)
@author(Ryan Sandor Richards)

# Command: move
Usage: `move [direction]`

The move commands allow the character to move to an adjacent room in the given
direction. Directions can be cardinal (north, south, east, or west), elevation
(up or down) or possibly specially defined (such as doors, inns, etc.).

When using the `look` command, exits will be displayed as {r}red{x} in the given
description.

There are many aliases for the move command, they are:

* `go`, `enter`, `exit` - Work exactly like `move`
* `north` - Same as `move north`
* `south` - Same as `move south`
* `east` - Same as `move east`
* `west` - Same as `move west`
* `up` - Same as `move up`
* `down` - Same as `move down`

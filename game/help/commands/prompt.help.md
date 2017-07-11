@name(prompt)
@author(Ryan Sandor Richards)

# Command: prompt
Usage: `prompt [format]`

The `prompt` command allows a player to fully customize their in-game prompt via
a format string. Special character sequences that begin with a `%` will be
replaced in the prompt with numbers and information in the game.

For example, the following command:

  `prompt '%h/%H %s/%S: '`

Would show a prompt with the current and total HP and SP, like so:

  100/100 40/40:

Colors can be incorporated into prompts to make them more readable. For a full
list of format options see `help format`.

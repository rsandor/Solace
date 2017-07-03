# The Global Cooldown (GCD)
Each player has a "global cooldown" (GCD) that acts a shared timer for cooldown
skills with a duration of 2 game ticks. While a player is "on" the GCD they
cannot use any of their cooldown skills that are associated with the shared
timer.

For a character with, for example, the `flurry` and `slash` cooldowns (both of
which are on the GCD) once they execute `flurry` they must wait 2 full ticks
before executing either of the actions.

Cooldowns that are on the gcd have the fact clearly marked in the informational
header on their help pages.

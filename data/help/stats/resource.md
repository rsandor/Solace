# Resource Stats
The following stats:

* Hit Points (HP)
* Magic Points (MP)
* Stamina Points (SP)

act as depletable and restorable pools that can be used to perform certain
actions such as using a skill or casting a spell. All resource stats have both
current and maximum values.

The current value of a resource stat determines the total amount of the
resource availble at the current time. Some actions such as skills and spells
have an associated cost that depletes the current value for one or more of a
character's resource stats. Once the current value of the stat has been depleted
actions that have an associated cost in one of the resources will fail to
execute.

A character's current value for each stat is naturally restored over time, and
characters can recieve bonuses to this restoration by resting, sleeping, or
being in special places (like healing temples). Restorative effects such as
potions and spells can also restore a character's current resource stat values.

A character's maximum value for each stat increases by leveling and can be
effected both negatively and positively by actions such as spells, or by items
such as magical equipment.

For more information about a specific resource stat see the following:

* `help hp`
* `help mp`
* `help sp`

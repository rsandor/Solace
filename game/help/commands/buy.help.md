@name(buy)
@author(Ryan Sandor Richards)

# Command: buy
Usage: `buy [name | inventory number]`

The buy command allows players to buy items from shops in the game world. When
the item is bought an amount of gold equal to the selling price is removed from
the character's coffers and the new item is added to their inventory.

Players can see the sell price for an item in a shop by using the `list`
command. If a player does not have enough gold to buy the item then no
transaction will occur.

See also: `help shop`, `help list`
